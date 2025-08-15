package dataaccesslayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private static final String URL = System.getenv("JDBC_URL");
    private static final String USER = System.getenv("MYSQLUSER");
    private static final String PASSWORD = System.getenv("MYSQLPASSWORD");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Explicit load (optional in newer JDBC)
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new IllegalStateException("Database environment variables are not set.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
