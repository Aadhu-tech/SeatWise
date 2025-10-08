package dao;
import model.AllocationRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class AllocationDAO {
    public static void clearAllocationsForExam(Connection conn, String examSlotId) throws SQLException {
        String sql = "DELETE FROM Allocation WHERE exam_slot_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, examSlotId);
            ps.executeUpdate();
        }
    }
    public static void saveAllocationBatch(Connection conn, List<AllocationRecord> allocations) throws SQLException {
        String sql = "INSERT INTO Allocation (student_id, room_id, seat_no, exam_slot_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (AllocationRecord r : allocations) {
                ps.setString(1, r.getStudentId());
                ps.setString(2, r.getRoomId());
                ps.setInt(3, r.getSeatNo());
                ps.setString(4, r.getExamSlotId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    public static List<AllocationRecord> fetchAllAllocations() {
        List<AllocationRecord> list = new ArrayList<>();
        String sql = "SELECT a.student_id, s.name, s.branch, a.room_id, a.seat_no, a.exam_slot_id "
                   + "FROM Allocation a LEFT JOIN Student s ON a.student_id = s.student_id "
                   + "ORDER BY a.room_id, a.seat_no";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AllocationRecord r = new AllocationRecord(
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("branch"),
                        rs.getString("room_id"),
                        rs.getInt("seat_no"),
                        rs.getString("exam_slot_id")
                );
                list.add(r);
            }
        } catch (SQLException ex) {
            System.err.println("Error in fetchAllAllocations: " + ex.getMessage());
        }
        return list;
    }
    public static AllocationRecord fetchAllocationForStudent(String studentId) {
        String sql = "SELECT a.student_id, s.name, s.branch, a.room_id, a.seat_no, a.exam_slot_id "
                   + "FROM Allocation a LEFT JOIN Student s ON a.student_id = s.student_id "
                   + "WHERE a.student_id = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AllocationRecord(
                            rs.getString("student_id"),
                            rs.getString("name"),
                            rs.getString("branch"),
                            rs.getString("room_id"),
                            rs.getInt("seat_no"),
                            rs.getString("exam_slot_id")
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error in fetchAllocationForStudent: " + ex.getMessage());
        }
        return null;
    }
    public static void saveAdminWarning(String text) {
        String sql = "INSERT INTO AdminWarning (warning_text) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, text);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error saving admin warning: " + ex.getMessage());
        }
    }
}
