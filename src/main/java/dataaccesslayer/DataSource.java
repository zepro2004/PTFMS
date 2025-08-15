package dataaccesslayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private static final String URL = System.getenv("JDBC_URL"); // Or build manually
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASS");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new IllegalStateException(
                "Database environment variables are not set. " +
                "URL=" + URL + ", USER=" + USER + ", PASS=" + (PASSWORD != null)
            );
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
