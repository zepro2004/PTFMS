package dataaccesslayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import transferobjects.UserDTO;

/**
 * Data Access Object for User operations in the PTFMS system.
 * Handles all database operations related to user management including
 * authentication, registration, user information retrieval, and user
 * status management.
 */
public class UserDAO {

  /**
   * Add a new user
   * 
   * @param user the user to add
   * @return true if successful
   */
  public boolean addUser(UserDTO user) {
    String sql = "INSERT INTO users (name, email, username, password_hash, role, status) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, user.getName());
      stmt.setString(2, user.getEmail());
      stmt.setString(3, user.getUsername());
      stmt.setString(4, user.getPasswordHash());
      stmt.setString(5, user.getRole());
      stmt.setString(6, user.getStatus() != null ? user.getStatus() : "On Duty");

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Find user by username
   * 
   * @param username the username to search for
   * @return UserDTO if found, null otherwise
   */
  public UserDTO findByUsername(String username) {
    String sql = "SELECT user_id, name, email, username, password_hash, role, status, created_at FROM users WHERE username = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        UserDTO user = new UserDTO();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Find user by email
   * 
   * @param email the email to search for
   * @return UserDTO if found, null otherwise
   */
  public UserDTO findByEmail(String email) {
    String sql = "SELECT user_id, name, email, username, password_hash, role, status, created_at FROM users WHERE email = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, email);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        UserDTO user = new UserDTO();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get all users
   * 
   * @return list of all users
   */
  public List<UserDTO> getAllUsers() {
    List<UserDTO> users = new ArrayList<>();
    String sql = "SELECT user_id, name, email, username, password_hash, role, status, created_at FROM users";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        UserDTO user = new UserDTO();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        users.add(user);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return users;
  }

  /**
   * Update user
   * 
   * @param user the user to update
   * @return true if successful
   */
  public boolean updateUser(UserDTO user) {
    String sql = "UPDATE users SET name = ?, email = ?, username = ?, password_hash = ?, role = ?, status = ? WHERE user_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, user.getName());
      stmt.setString(2, user.getEmail());
      stmt.setString(3, user.getUsername());
      stmt.setString(4, user.getPasswordHash());
      stmt.setString(5, user.getRole());
      stmt.setString(6, user.getStatus() != null ? user.getStatus() : "On Duty");
      stmt.setInt(7, user.getUserId());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Delete user
   * 
   * @param userId the user ID to delete
   * @return true if successful
   */
  public boolean deleteUser(int userId) {
    String sql = "DELETE FROM users WHERE user_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, userId);
      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
