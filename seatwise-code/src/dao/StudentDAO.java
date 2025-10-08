package dao;
import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class StudentDAO {
    public void insertStudent(Student student) throws SQLException {
        String sql = "INSERT INTO Student (student_id, name, branch) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, student.getStudentId());
            ps.setString(2, student.getName());
            ps.setString(3, student.getBranch());
            ps.executeUpdate();
        }
    }
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT student_id, name, branch FROM Student";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new Student(rs.getString("student_id"), rs.getString("name"), rs.getString("branch")));
            }
        }
        return students;
    }
}
