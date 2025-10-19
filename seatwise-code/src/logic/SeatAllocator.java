package logic;
import dao.AllocationDAO;
import dao.DatabaseConnection;
import model.AllocationRecord;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import dao.StudentDAO;
import dao.RoomDAO;
import model.Student;
import model.Room;
public class SeatAllocator {
    public static class AllocationResult {
        public final boolean success;
        public final List<String> warnings;
        public final List<AllocationRecord> allocations;
        public AllocationResult(boolean success, List<String> warnings, List<AllocationRecord> allocations) {
            this.success = success; this.warnings = warnings; this.allocations = allocations;
        }
    }
    public AllocationResult allocateSeatsForExam(String examSlotId) {
        List<String> warnings = new ArrayList<>();
        List<AllocationRecord> allocations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verify exam slot exists
            String checkExamSql = "SELECT COUNT(*) FROM ExamSlot WHERE exam_slot_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkExamSql)) {
                ps.setString(1, examSlotId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        warnings.add("Exam slot '" + examSlotId + "' does not exist. Please upload exam slots first.");
                        return new AllocationResult(false, warnings, allocations);
                    }
                }
            }
            conn.setAutoCommit(false);
            StudentDAO sdao = new StudentDAO();
            RoomDAO rdao = new RoomDAO();
            List<Student> students = sdao.getAllStudents();
            List<Room> rooms = rdao.getAllRooms();
            List<Room> normal = new ArrayList<>();
            List<Room> backup = new ArrayList<>();
            int totalStudents = students.size();
            for (Room r: rooms) { if (r.isBackup()) backup.add(r); else normal.add(r); }
            int normalCap = normal.stream().mapToInt(Room::getCapacity).sum();
            int backupCap = backup.stream().mapToInt(Room::getCapacity).sum();
            if (totalStudents > normalCap) {
                if (totalStudents > normalCap + backupCap) {
                    String msg = "Total students ("+totalStudents+") exceed total capacity ("+(normalCap+backupCap)+")";
                    warnings.add(msg);
                    AllocationDAO.saveAdminWarning(msg);
                    conn.rollback();
                    return new AllocationResult(false, warnings, allocations);
                } else {
                    normal.addAll(backup);
                    warnings.add("Normal rooms insufficient; backup rooms included.");
                    AllocationDAO.saveAdminWarning("Backup rooms used for exam " + examSlotId);
                }
            }
            Map<String, Queue<Student>> byBranch = new HashMap<>();
            for (Student s: students) byBranch.computeIfAbsent(s.getBranch(), k->new LinkedList<>()).add(s);
            int maxBranch = byBranch.values().stream().mapToInt(Queue::size).max().orElse(0);
            if (maxBranch > (totalStudents/2 + 1)) {
                warnings.add("Branch imbalance detected. Relaxed allocation applied.");
                AllocationDAO.saveAdminWarning("Branch imbalance for exam " + examSlotId);
            }
            PriorityQueue<Map.Entry<String,Integer>> pq = new PriorityQueue<>((a,b)->b.getValue()-a.getValue());
            for (Map.Entry<String, Queue<Student>> e: byBranch.entrySet()) pq.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().size()));
            List<Student> ordered = new ArrayList<>();
            while (!pq.isEmpty()) {
                Map.Entry<String,Integer> first = pq.poll();
                String b1 = first.getKey();
                Queue<Student> q1 = byBranch.get(b1);
                if (q1!=null && !q1.isEmpty()) ordered.add(q1.poll());
                if (first.getValue()-1 > 0) first = new AbstractMap.SimpleEntry<>(b1, first.getValue()-1); else first = null;
                Map.Entry<String,Integer> second = null;
                if (!pq.isEmpty()) second = pq.poll();
                if (second!=null) {
                    String b2 = second.getKey();
                    Queue<Student> q2 = byBranch.get(b2);
                    if (q2!=null && !q2.isEmpty()) ordered.add(q2.poll());
                    if (second.getValue()-1 > 0) second = new AbstractMap.SimpleEntry<>(b2, second.getValue()-1); else second = null;
                }
                if (first!=null) pq.add(first);
                if (second!=null) pq.add(second);
            }
            int idx=0;
            for (Room room: normal) {
                for (int seat=1; seat<=room.getCapacity() && idx<ordered.size(); seat++) {
                    Student s = ordered.get(idx++);
                    allocations.add(new AllocationRecord(s.getStudentId(), s.getName(), s.getBranch(), room.getRoomId(), seat, examSlotId));
                }
                if (idx>=ordered.size()) break;
            }
            int violations = reduceAdjacency(allocations);
            if (violations>0) {
                warnings.add("Allocation resulted in " + violations + " adjacency violations.");
                AllocationDAO.saveAdminWarning("Allocation had " + violations + " adjacency violations for " + examSlotId);
            }
            AllocationDAO.clearAllocationsForExam(conn, examSlotId);
            AllocationDAO.saveAllocationBatch(conn, allocations);
            conn.commit();
            return new AllocationResult(true, warnings, allocations);
        } catch (SQLException ex) {
            ex.printStackTrace();
            warnings.add("DB error: " + ex.getMessage());
            return new AllocationResult(false, warnings, allocations);
        }
    }
    private int reduceAdjacency(List<AllocationRecord> allocations) {
        Map<String, List<AllocationRecord>> byRoom = new LinkedHashMap<>();
        for (AllocationRecord a: allocations) byRoom.computeIfAbsent(a.getRoomId(), k->new ArrayList<>()).add(a);
        int violations=0;
        for (List<AllocationRecord> seats: byRoom.values()) {
            for (int i=1;i<seats.size();i++) {
                if (seats.get(i).getBranch().equals(seats.get(i-1).getBranch())) {
                    boolean fixed=false;
                    for (List<AllocationRecord> other: byRoom.values()) {
                        if (other==seats) continue;
                        for (int j=0;j<other.size();j++) {
                            if (!other.get(j).getBranch().equals(seats.get(i).getBranch()) && !other.get(j).getBranch().equals(seats.get(i-1).getBranch())) {
                                AllocationRecord tmp = other.get(j);
                                other.set(j, seats.get(i));
                                seats.set(i, tmp);
                                fixed=true; break;
                            }
                        }
                        if (fixed) break;
                    }
                    if (!fixed) violations++;
                }
            }
        }
        allocations.clear();
        for (Map.Entry<String, List<AllocationRecord>> e: byRoom.entrySet()) {
            List<AllocationRecord> list = e.getValue();
            for (int i=0;i<list.size();i++) {
                AllocationRecord r = list.get(i);
                allocations.add(new AllocationRecord(r.getStudentId(), r.getStudentName(), r.getBranch(), r.getRoomId(), i+1, r.getExamSlotId()));
            }
        }
        return violations;
    }
}
