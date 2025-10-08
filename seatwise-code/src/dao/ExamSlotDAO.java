package dao;
import model.ExamSlot;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ExamSlotDAO {
    public void insertExamSlot(ExamSlot slot) throws SQLException {
        String sql = "INSERT INTO ExamSlot (exam_slot_id, subject, date) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, slot.getExamSlotId());
            ps.setString(2, slot.getSubject());
            ps.setString(3, slot.getDate());
            ps.executeUpdate();
        }
    }
    public List<ExamSlot> getAllExamSlots() throws SQLException {
        List<ExamSlot> slots = new ArrayList<>();
        String sql = "SELECT exam_slot_id, subject, date FROM ExamSlot";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                slots.add(new ExamSlot(rs.getString("exam_slot_id"), rs.getString("subject"), rs.getString("date")));
            }
        }
        return slots;
    }
}
