package transferobjects;

import java.sql.Timestamp;

/**
 * Data Transfer Object for User entity in the PTFMS system.
 * Represents a system user with authentication details, role information,
 * and status tracking. Provides getter and setter methods for secure
 * data transfer between application layers.
 */
public class UserDTO {
  /** Unique user identifier */
  private int userId;
  /** User's full name */
  private String name;
  /** User's email address */
  private String email;
  /** Username for login */
  private String username;
  /** Hashed password */
  private String passwordHash;
  /** User role (Manager or Operator) */
  private String role;
  /** User status (On Duty, Off Duty, Break) */
  private String status;
  /** When the user was created */
  private Timestamp createdAt;

  /**
   * Get the user ID
   * 
   * @return the user ID
   */
  public int getUserId() {
    return userId;
  }

  /**
   * Set the user ID
   * 
   * @param userId the user ID to set
   */
  public void setUserId(int userId) {
    this.userId = userId;
  }

  /**
   * Get the user's name
   * 
   * @return the user's name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the user's name
   * 
   * @param name the user's name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the user's email
   * 
   * @return the user's email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Set the user's email
   * 
   * @param email the user's email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Get the username
   * 
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Set the username
   * 
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Get the password hash
   * 
   * @return the password hash
   */
  public String getPasswordHash() {
    return passwordHash;
  }

  /**
   * Set the password hash
   * 
   * @param passwordHash the password hash to set
   */
  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  /**
   * Get the role
   * 
   * @return the role
   */
  public String getRole() {
    return role;
  }

  /**
   * Set the role
   * 
   * @param role the role to set
   */
  public void setRole(String role) {
    this.role = role;
  }

  /**
   * Get the status
   * 
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Set the status
   * 
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Get the created timestamp
   * 
   * @return the created timestamp
   */
  public Timestamp getCreatedAt() {
    return createdAt;
  }

  /**
   * Set the created timestamp
   * 
   * @param createdAt the created timestamp to set
   */
  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Check if user is a manager
   * 
   * @return true if user is a manager
   */
  public boolean isManager() {
    return "Manager".equals(role);
  }

  /**
   * String representation of the user
   * 
   * @return formatted string with user details
   */
  @Override
  public String toString() {
    return "User [ID=" + userId + ", Name=" + name + ", Email=" + email +
        ", Username=" + username + ", Role=" + role + "]";
  }
}
