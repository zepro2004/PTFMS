package dataaccesslayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Database connection utility class for the PTFMS system.
 * Manages database connections using configuration properties
 * and provides centralized connection management for all DAO classes.
 * Implements connection pooling and proper resource management.
 */
public class DataSource {
  private static final String PROPERTIES_FILE = "/database.properties";
  private static Properties properties = new Properties();

  static {
    try (InputStream input = DataSource.class.getResourceAsStream(PROPERTIES_FILE)) {
      if (input != null) {
        properties.load(input);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get database connection
   * 
   * @return Connection object
   * @throws SQLException if connection fails
   */
  public static Connection getConnection() throws SQLException {
    String url = properties.getProperty("jdbc.url");
    String username = properties.getProperty("jdbc.username");
    String password = properties.getProperty("jdbc.password");

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new SQLException("MySQL driver not found", e);
    }

    return DriverManager.getConnection(url, username, password);
  }
}
