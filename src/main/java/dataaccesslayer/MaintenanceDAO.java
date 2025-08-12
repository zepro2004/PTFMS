package dataaccesslayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import transferobjects.MaintenanceDTO;

/**
 * Data Access Object for Maintenance operations in the PTFMS system.
 * Handles all database operations related to vehicle maintenance including
 * scheduling, tracking, cost management, and maintenance history.
 * Supports various maintenance types and strategy implementations.
 */
public class MaintenanceDAO {

  /**
   * Add a new maintenance record
   * 
   * @param maintenance the maintenance record to add
   * @return true if successful
   */
  public boolean addMaintenance(MaintenanceDTO maintenance) {
    String sql = "INSERT INTO maintenance (vehicle_id, service_date, description, cost, status) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, maintenance.getVehicleId());
      stmt.setDate(2, maintenance.getServiceDate());
      stmt.setString(3, maintenance.getDescription());
      stmt.setBigDecimal(4, maintenance.getCost());
      stmt.setString(5, maintenance.getStatus());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Find maintenance record by ID
   * 
   * @param maintenanceId the maintenance ID to search for
   * @return MaintenanceDTO if found, null otherwise
   */
  public MaintenanceDTO findById(int maintenanceId) {
    String sql = "SELECT maintenance_id, vehicle_id, service_date, description, cost, status, created_at FROM maintenance WHERE maintenance_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, maintenanceId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        MaintenanceDTO maintenance = new MaintenanceDTO();
        maintenance.setMaintenanceId(rs.getInt("maintenance_id"));
        maintenance.setVehicleId(rs.getInt("vehicle_id"));
        maintenance.setServiceDate(rs.getDate("service_date"));
        maintenance.setDescription(rs.getString("description"));
        maintenance.setCost(rs.getBigDecimal("cost"));
        maintenance.setStatus(rs.getString("status"));
        maintenance.setCreatedAt(rs.getTimestamp("created_at"));
        return maintenance;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get all maintenance records
   * 
   * @return list of all maintenance records
   */
  public List<MaintenanceDTO> getAllMaintenance() {
    List<MaintenanceDTO> maintenanceList = new ArrayList<>();
    String sql = "SELECT maintenance_id, vehicle_id, service_date, description, cost, status, created_at FROM maintenance";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        MaintenanceDTO maintenance = new MaintenanceDTO();
        maintenance.setMaintenanceId(rs.getInt("maintenance_id"));
        maintenance.setVehicleId(rs.getInt("vehicle_id"));
        maintenance.setServiceDate(rs.getDate("service_date"));
        maintenance.setDescription(rs.getString("description"));
        maintenance.setCost(rs.getBigDecimal("cost"));
        maintenance.setStatus(rs.getString("status"));
        maintenance.setCreatedAt(rs.getTimestamp("created_at"));
        maintenanceList.add(maintenance);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return maintenanceList;
  }

  /**
   * Get maintenance records by vehicle ID
   * 
   * @param vehicleId the vehicle ID to search for
   * @return list of maintenance records for the vehicle
   */
  public List<MaintenanceDTO> getMaintenanceByVehicleId(int vehicleId) {
    List<MaintenanceDTO> maintenanceList = new ArrayList<>();
    String sql = "SELECT maintenance_id, vehicle_id, service_date, description, cost, status, created_at FROM maintenance WHERE vehicle_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, vehicleId);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        MaintenanceDTO maintenance = new MaintenanceDTO();
        maintenance.setMaintenanceId(rs.getInt("maintenance_id"));
        maintenance.setVehicleId(rs.getInt("vehicle_id"));
        maintenance.setServiceDate(rs.getDate("service_date"));
        maintenance.setDescription(rs.getString("description"));
        maintenance.setCost(rs.getBigDecimal("cost"));
        maintenance.setStatus(rs.getString("status"));
        maintenance.setCreatedAt(rs.getTimestamp("created_at"));
        maintenanceList.add(maintenance);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return maintenanceList;
  }

  /**
   * Update maintenance record
   * 
   * @param maintenance the maintenance record to update
   * @return true if successful
   */
  public boolean updateMaintenance(MaintenanceDTO maintenance) {
    String sql = "UPDATE maintenance SET vehicle_id = ?, service_date = ?, description = ?, cost = ?, status = ? WHERE maintenance_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, maintenance.getVehicleId());
      stmt.setDate(2, maintenance.getServiceDate());
      stmt.setString(3, maintenance.getDescription());
      stmt.setBigDecimal(4, maintenance.getCost());
      stmt.setString(5, maintenance.getStatus());
      stmt.setInt(6, maintenance.getMaintenanceId());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Delete maintenance record
   * 
   * @param maintenanceId the maintenance ID to delete
   * @return true if successful
   */
  public boolean deleteMaintenance(int maintenanceId) {
    String sql = "DELETE FROM maintenance WHERE maintenance_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, maintenanceId);
      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Get maintenance records by status
   * 
   * @param status the status to search for
   * @return list of maintenance records with the specified status
   */
  public List<MaintenanceDTO> getMaintenanceByStatus(String status) {
    List<MaintenanceDTO> maintenanceList = new ArrayList<>();
    String sql = "SELECT maintenance_id, vehicle_id, service_date, description, cost, status, created_at FROM maintenance WHERE status = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, status);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        MaintenanceDTO maintenance = new MaintenanceDTO();
        maintenance.setMaintenanceId(rs.getInt("maintenance_id"));
        maintenance.setVehicleId(rs.getInt("vehicle_id"));
        maintenance.setServiceDate(rs.getDate("service_date"));
        maintenance.setDescription(rs.getString("description"));
        maintenance.setCost(rs.getBigDecimal("cost"));
        maintenance.setStatus(rs.getString("status"));
        maintenance.setCreatedAt(rs.getTimestamp("created_at"));
        maintenanceList.add(maintenance);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return maintenanceList;
  }
}
