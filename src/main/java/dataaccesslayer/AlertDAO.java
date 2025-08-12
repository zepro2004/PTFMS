package dataaccesslayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import transferobjects.AlertDTO;

/**
 * Data Access Object for Alert operations in the PTFMS system.
 * Handles all database operations related to alert management including
 * creating, retrieving, updating, and resolving alerts. Supports various
 * query operations for alert monitoring and reporting.
 */
public class AlertDAO {

  /**
   * Add a new alert
   * 
   * @param alert the alert to add
   * @return true if successful
   */
  public boolean addAlert(AlertDTO alert) {
    String sql = "INSERT INTO alerts (vehicle_id, alert_type, message, status) VALUES (?, ?, ?, ?)";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, alert.getVehicleId());
      stmt.setString(2, alert.getAlertType());
      stmt.setString(3, alert.getMessage());
      stmt.setString(4, alert.getStatus());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Find alert by ID
   * 
   * @param alertId the alert ID to search for
   * @return AlertDTO if found, null otherwise
   */
  public AlertDTO findById(int alertId) {
    String sql = "SELECT alert_id, vehicle_id, alert_type, message, created_at, status FROM alerts WHERE alert_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, alertId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        AlertDTO alert = new AlertDTO();
        alert.setAlertId(rs.getInt("alert_id"));
        alert.setVehicleId(rs.getInt("vehicle_id"));
        alert.setAlertType(rs.getString("alert_type"));
        alert.setMessage(rs.getString("message"));
        alert.setCreatedAt(rs.getTimestamp("created_at"));
        alert.setStatus(rs.getString("status"));
        return alert;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get all alerts
   * 
   * @return list of all alerts
   */
  public List<AlertDTO> getAllAlerts() {
    List<AlertDTO> alerts = new ArrayList<>();
    String sql = "SELECT alert_id, vehicle_id, alert_type, message, created_at, status FROM alerts ORDER BY created_at DESC";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        AlertDTO alert = new AlertDTO();
        alert.setAlertId(rs.getInt("alert_id"));
        alert.setVehicleId(rs.getInt("vehicle_id"));
        alert.setAlertType(rs.getString("alert_type"));
        alert.setMessage(rs.getString("message"));
        alert.setCreatedAt(rs.getTimestamp("created_at"));
        alert.setStatus(rs.getString("status"));
        alerts.add(alert);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return alerts;
  }

  /**
   * Get alerts by vehicle ID
   * 
   * @param vehicleId the vehicle ID to search for
   * @return list of alerts for the vehicle
   */
  public List<AlertDTO> getAlertsByVehicleId(int vehicleId) {
    List<AlertDTO> alerts = new ArrayList<>();
    String sql = "SELECT alert_id, vehicle_id, alert_type, message, created_at, status FROM alerts WHERE vehicle_id = ? ORDER BY created_at DESC";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, vehicleId);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        AlertDTO alert = new AlertDTO();
        alert.setAlertId(rs.getInt("alert_id"));
        alert.setVehicleId(rs.getInt("vehicle_id"));
        alert.setAlertType(rs.getString("alert_type"));
        alert.setMessage(rs.getString("message"));
        alert.setCreatedAt(rs.getTimestamp("created_at"));
        alert.setStatus(rs.getString("status"));
        alerts.add(alert);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return alerts;
  }

  /**
   * Get alerts by status
   * 
   * @param status the status to search for
   * @return list of alerts with the specified status
   */
  public List<AlertDTO> getAlertsByStatus(String status) {
    List<AlertDTO> alerts = new ArrayList<>();
    String sql = "SELECT alert_id, vehicle_id, alert_type, message, created_at, status FROM alerts WHERE status = ? ORDER BY created_at DESC";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, status);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        AlertDTO alert = new AlertDTO();
        alert.setAlertId(rs.getInt("alert_id"));
        alert.setVehicleId(rs.getInt("vehicle_id"));
        alert.setAlertType(rs.getString("alert_type"));
        alert.setMessage(rs.getString("message"));
        alert.setCreatedAt(rs.getTimestamp("created_at"));
        alert.setStatus(rs.getString("status"));
        alerts.add(alert);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return alerts;
  }

  /**
   * Get alerts by type
   * 
   * @param alertType the alert type to search for
   * @return list of alerts with the specified type
   */
  public List<AlertDTO> getAlertsByType(String alertType) {
    List<AlertDTO> alerts = new ArrayList<>();
    String sql = "SELECT alert_id, vehicle_id, alert_type, message, created_at, status FROM alerts WHERE alert_type = ? ORDER BY created_at DESC";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, alertType);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        AlertDTO alert = new AlertDTO();
        alert.setAlertId(rs.getInt("alert_id"));
        alert.setVehicleId(rs.getInt("vehicle_id"));
        alert.setAlertType(rs.getString("alert_type"));
        alert.setMessage(rs.getString("message"));
        alert.setCreatedAt(rs.getTimestamp("created_at"));
        alert.setStatus(rs.getString("status"));
        alerts.add(alert);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return alerts;
  }

  /**
   * Get open alerts (status = 'Open')
   * 
   * @return list of open alerts
   */
  public List<AlertDTO> getOpenAlerts() {
    return getAlertsByStatus("Open");
  }

  /**
   * Update alert
   * 
   * @param alert the alert to update
   * @return true if successful
   */
  public boolean updateAlert(AlertDTO alert) {
    String sql = "UPDATE alerts SET vehicle_id = ?, alert_type = ?, message = ?, status = ? WHERE alert_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, alert.getVehicleId());
      stmt.setString(2, alert.getAlertType());
      stmt.setString(3, alert.getMessage());
      stmt.setString(4, alert.getStatus());
      stmt.setInt(5, alert.getAlertId());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Resolve an alert (set status to 'Resolved')
   * 
   * @param alertId the alert ID to resolve
   * @return true if successful
   */
  public boolean resolveAlert(int alertId) {
    String sql = "UPDATE alerts SET status = 'Resolved' WHERE alert_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, alertId);
      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Delete alert
   * 
   * @param alertId the alert ID to delete
   * @return true if successful
   */
  public boolean deleteAlert(int alertId) {
    String sql = "DELETE FROM alerts WHERE alert_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, alertId);
      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
