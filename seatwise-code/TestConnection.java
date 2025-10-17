import dao.DatabaseConnection;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection con = DatabaseConnection.getConnection()) {
            System.out.println("✅ Successfully connected to MySQL database!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

