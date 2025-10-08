package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
public class DatabaseConnection {
    private static String DB_HOST = "localhost";
    private static String DB_PORT = "3306";
    private static String DB_NAME = "smartexam";
    private static String DB_USER = "root";
    private static String DB_PASSWORD = "";
    private static String URL;
    static {
        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                DB_HOST = p.getProperty("db.host", DB_HOST);
                DB_PORT = p.getProperty("db.port", DB_PORT);
                DB_NAME = p.getProperty("db.name", DB_NAME);
                DB_USER = p.getProperty("db.user", DB_USER);
                DB_PASSWORD = p.getProperty("db.password", DB_PASSWORD);
            }
        } catch (Exception ex) {
            System.err.println("db.properties not found or failed to load - using defaults.");
        }
        URL = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                DB_HOST, DB_PORT, DB_NAME);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add mysql-connector-java to classpath.");
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}
