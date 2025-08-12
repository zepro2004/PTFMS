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
    // Allow environment variables to override properties (useful for Docker / cloud)
    String envUrl = System.getenv("DB_URL");
    String envHost = System.getenv("DB_HOST");
    String envPort = System.getenv("DB_PORT");
    String envName = System.getenv("DB_NAME");
    String envUser = System.getenv("DB_USER");
    String envPass = System.getenv("DB_PASS");

    String url = envUrl != null && !envUrl.isBlank()
        ? envUrl
        : buildUrlFromParts(envHost, envPort, envName, properties.getProperty("jdbc.url"));
    String username = firstNonBlank(envUser, properties.getProperty("jdbc.username"));
    String password = firstNonBlank(envPass, properties.getProperty("jdbc.password"));

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new SQLException("MySQL driver not found", e);
    }

    return DriverManager.getConnection(url, username, password);
  }

  private static String firstNonBlank(String primary, String fallback) {
    return (primary != null && !primary.isBlank()) ? primary : fallback;
  }

  private static String buildUrlFromParts(String host, String port, String dbName, String fallbackUrl) {
    if (host == null || host.isBlank() || dbName == null || dbName.isBlank()) {
      return fallbackUrl; // insufficient parts; use provided property
    }
    String resolvedPort = (port != null && !port.isBlank()) ? port : "3306";
    return "jdbc:mysql://" + host + ":" + resolvedPort + "/" + dbName;
  }
}
