package dao;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class LoginDAO {
    public static class LoginCredentials {
        public final String username;
        public final String password;
        public final String studentId;
        
        public LoginCredentials(String username, String password, String studentId) {
            this.username = username;
            this.password = password;
            this.studentId = studentId;
        }
    }
    
    public static Map<String, LoginCredentials> loadCredentialsFromCSV(String csvPath) {
        Map<String, LoginCredentials> credentials = new HashMap<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(csvPath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // Skip header
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String studentId = parts[2].trim();
                    credentials.put(username, new LoginCredentials(username, password, studentId));
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading login credentials: " + ex.getMessage());
        }
        return credentials;
    }
    
    public static void saveCredentialsToDB(Map<String, LoginCredentials> credentials) {
        String sql = "INSERT INTO Login (username, password, student_id) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE password = VALUES(password), student_id = VALUES(student_id)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (LoginCredentials cred : credentials.values()) {
                ps.setString(1, cred.username);
                ps.setString(2, cred.password);
                ps.setString(3, cred.studentId);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            System.err.println("Error saving login credentials: " + ex.getMessage());
        }
    }
    
    public static LoginCredentials validateLogin(String username, String password) {
        String sql = "SELECT username, password, student_id FROM Login WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LoginCredentials(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("student_id")
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error validating login: " + ex.getMessage());
        }
        return null;
    }
}
