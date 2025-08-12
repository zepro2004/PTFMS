package businesslayer.services;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import dataaccesslayer.UserDAO;
import transferobjects.UserDTO;

/**
 * Business service for User operations in the PTFMS system.
 * Provides comprehensive user management functionality including
 * authentication, registration, user profile management, and role-based
 * access control. Coordinates between the presentation layer and data
 * access layer for all user-related operations.
 */
public class UserService {
  private final UserDAO userDAO;

  /**
   * Constructor
   */
  public UserService() {
    this.userDAO = new UserDAO();
  }

  /**
   * Register a new user
   * 
   * @param user the user to register
   * @return true if registration successful
   */
  public boolean registerUser(UserDTO user) {
    // Check if username already exists
    if (userDAO.findByUsername(user.getUsername()) != null) {
      return false;
    }

    // Check if email already exists
    if (userDAO.findByEmail(user.getEmail()) != null) {
      return false;
    }

    return userDAO.addUser(user);
  }

  /**
   * Register a new user with username and password
   * 
   * @param username the username
   * @param password the plain text password
   * @param email    the email address
   * @param name     the full name
   * @param role     the user role
   * @return true if registration successful
   */
  public boolean registerUser(String username, String password, String email, String name, String role) {
    // Check if username already exists
    if (userDAO.findByUsername(username) != null) {
      return false;
    }

    // Check if email already exists
    if (userDAO.findByEmail(email) != null) {
      return false;
    }

    // Hash the password
    String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

    // Create user DTO
    UserDTO user = new UserDTO();
    user.setUsername(username);
    user.setPasswordHash(passwordHash);
    user.setEmail(email);
    user.setName(name);
    user.setRole(role);

    return userDAO.addUser(user);
  }

  /**
   * Authenticate user login
   * 
   * @param username the username
   * @param password the plain text password
   * @return UserDTO if authentication successful, null otherwise
   */
  public UserDTO authenticateUser(String username, String password) {
    UserDTO user = userDAO.findByUsername(username);

    if (user != null && BCrypt.checkpw(password, user.getPasswordHash())) {
      return user;
    }

    return null;
  }

  /**
   * Get user by username
   * 
   * @param username the username
   * @return UserDTO if found, null otherwise
   */
  public UserDTO getUserByUsername(String username) {
    return userDAO.findByUsername(username);
  }

  /**
   * Get user by email
   * 
   * @param email the email address
   * @return UserDTO if found, null otherwise
   */
  public UserDTO getUserByEmail(String email) {
    return userDAO.findByEmail(email);
  }

  /**
   * Get user by ID
   * 
   * @param userId the user ID
   * @return UserDTO if found, null otherwise
   */
  public UserDTO getUserById(int userId) {
    // Note: UserDAO doesn't have findById, we'll need to get all users and filter
    // This is a limitation that should be addressed in UserDAO
    List<UserDTO> users = userDAO.getAllUsers();
    return users.stream()
        .filter(user -> user.getUserId() == userId)
        .findFirst()
        .orElse(null);
  }

  /**
   * Get all users
   * 
   * @return list of all users
   */
  public List<UserDTO> getAllUsers() {
    return userDAO.getAllUsers();
  }

  /**
   * Update user information
   * 
   * @param user the updated user information
   * @return true if successful
   */
  public boolean updateUser(UserDTO user) {
    return userDAO.updateUser(user);
  }

  /**
   * Update user password
   * 
   * @param username    the username
   * @param newPassword the new password
   * @return true if successful
   */
  public boolean updatePassword(String username, String newPassword) {
    UserDTO user = userDAO.findByUsername(username);
    if (user != null) {
      user.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
      return userDAO.updateUser(user);
    }
    return false;
  }

  /**
   * Update user password by ID
   * 
   * @param userId      the user ID
   * @param newPassword the new password
   * @return true if successful
   */
  public boolean updatePassword(int userId, String newPassword) {
    UserDTO user = getUserById(userId);
    if (user != null) {
      user.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
      return userDAO.updateUser(user);
    }
    return false;
  }

  /**
   * Delete user
   * 
   * @param userId the user ID
   * @return true if successful
   */
  public boolean deleteUser(int userId) {
    return userDAO.deleteUser(userId);
  }

  /**
   * Check if user is manager
   * 
   * @param userId the user ID
   * @return true if user is manager
   */
  public boolean isManager(int userId) {
    UserDTO user = getUserById(userId);
    return user != null && "Manager".equalsIgnoreCase(user.getRole());
  }

  /**
   * Check if user is operator
   * 
   * @param userId the user ID
   * @return true if user is operator
   */
  public boolean isOperator(int userId) {
    UserDTO user = getUserById(userId);
    return user != null && "Operator".equalsIgnoreCase(user.getRole());
  }

  /**
   * Get all managers
   * 
   * @return list of manager users
   */
  public List<UserDTO> getAllManagers() {
    return userDAO.getAllUsers().stream()
        .filter(user -> "Manager".equalsIgnoreCase(user.getRole()))
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Get all operators
   * 
   * @return list of operator users
   */
  public List<UserDTO> getAllOperators() {
    return userDAO.getAllUsers().stream()
        .filter(user -> "Operator".equalsIgnoreCase(user.getRole()))
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Update operator status (for simple break tracking)
   * 
   * @param userId the user ID
   * @param status the new status (On Duty, Off Duty, Break)
   * @return true if successful
   */
  public boolean updateOperatorStatus(int userId, String status) {
    UserDTO user = getUserById(userId);
    if (user != null && "Operator".equalsIgnoreCase(user.getRole())) {
      user.setStatus(status);
      return userDAO.updateUser(user);
    }
    return false;
  }

  /**
   * Get operators by status
   * 
   * @param status the status to filter by
   * @return list of operators with the specified status
   */
  public List<UserDTO> getOperatorsByStatus(String status) {
    return userDAO.getAllUsers().stream()
        .filter(user -> "Operator".equalsIgnoreCase(user.getRole()))
        .filter(user -> status.equalsIgnoreCase(user.getStatus()))
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Get available operators (On Duty status)
   * 
   * @return list of available operators
   */
  public List<UserDTO> getAvailableOperators() {
    return getOperatorsByStatus("On Duty");
  }

  /**
   * Check if operator is available
   * 
   * @param userId the user ID
   * @return true if operator is on duty
   */
  public boolean isOperatorAvailable(int userId) {
    UserDTO user = getUserById(userId);
    return user != null && "Operator".equalsIgnoreCase(user.getRole())
        && "On Duty".equalsIgnoreCase(user.getStatus());
  }
}
