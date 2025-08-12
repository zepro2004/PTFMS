package dataaccesslayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import transferobjects.VehicleComponentDTO;

/**
 * Data Access Object for Vehicle Component operations in the PTFMS system.
 * Handles all database operations related to vehicle parts and components
 * including inventory management, component tracking, replacement history,
 * and maintenance-related component operations.
 */
public class VehicleComponentDAO {

  /**
   * Add a new vehicle component
   * 
   * @param component the vehicle component to add
   * @return true if successful
   */
  public boolean addVehicleComponent(VehicleComponentDTO component) {
    String sql = "INSERT INTO vehicle_components (vehicle_id, component_name, usage_hours, max_hours, status) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, component.getVehicleId());
      stmt.setString(2, component.getComponentName());
      stmt.setBigDecimal(3, component.getUsageHours());
      stmt.setBigDecimal(4, component.getMaxHours());
      stmt.setString(5, component.getStatus());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Find vehicle component by ID
   * 
   * @param componentId the component ID to search for
   * @return VehicleComponentDTO if found, null otherwise
   */
  public VehicleComponentDTO findById(int componentId) {
    String sql = "SELECT component_id, vehicle_id, component_name, usage_hours, max_hours, status FROM vehicle_components WHERE component_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, componentId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        VehicleComponentDTO component = new VehicleComponentDTO();
        component.setComponentId(rs.getInt("component_id"));
        component.setVehicleId(rs.getInt("vehicle_id"));
        component.setComponentName(rs.getString("component_name"));
        component.setUsageHours(rs.getBigDecimal("usage_hours"));
        component.setMaxHours(rs.getBigDecimal("max_hours"));
        component.setStatus(rs.getString("status"));
        return component;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get vehicle components by vehicle ID
   * 
   * @param vehicleId the vehicle ID to search for
   * @return list of vehicle components for the vehicle
   */
  public List<VehicleComponentDTO> getComponentsByVehicleId(int vehicleId) {
    List<VehicleComponentDTO> components = new ArrayList<>();
    String sql = "SELECT component_id, vehicle_id, component_name, usage_hours, max_hours, status FROM vehicle_components WHERE vehicle_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, vehicleId);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        VehicleComponentDTO component = new VehicleComponentDTO();
        component.setComponentId(rs.getInt("component_id"));
        component.setVehicleId(rs.getInt("vehicle_id"));
        component.setComponentName(rs.getString("component_name"));
        component.setUsageHours(rs.getBigDecimal("usage_hours"));
        component.setMaxHours(rs.getBigDecimal("max_hours"));
        component.setStatus(rs.getString("status"));
        components.add(component);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return components;
  }

  /**
   * Get components by status
   * 
   * @param status the status to search for
   * @return list of vehicle components with the specified status
   */
  public List<VehicleComponentDTO> getComponentsByStatus(String status) {
    List<VehicleComponentDTO> components = new ArrayList<>();
    String sql = "SELECT component_id, vehicle_id, component_name, usage_hours, max_hours, status FROM vehicle_components WHERE status = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, status);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        VehicleComponentDTO component = new VehicleComponentDTO();
        component.setComponentId(rs.getInt("component_id"));
        component.setVehicleId(rs.getInt("vehicle_id"));
        component.setComponentName(rs.getString("component_name"));
        component.setUsageHours(rs.getBigDecimal("usage_hours"));
        component.setMaxHours(rs.getBigDecimal("max_hours"));
        component.setStatus(rs.getString("status"));
        components.add(component);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return components;
  }

  /**
   * Get components that need maintenance (usage_hours >= max_hours * threshold)
   * 
   * @param threshold percentage threshold (e.g., 0.8 for 80%)
   * @return list of vehicle components that need maintenance
   */
  public List<VehicleComponentDTO> getComponentsNeedingMaintenance(double threshold) {
    List<VehicleComponentDTO> components = new ArrayList<>();
    String sql = "SELECT component_id, vehicle_id, component_name, usage_hours, max_hours, status FROM vehicle_components WHERE usage_hours >= (max_hours * ?) AND max_hours IS NOT NULL";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setDouble(1, threshold);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        VehicleComponentDTO component = new VehicleComponentDTO();
        component.setComponentId(rs.getInt("component_id"));
        component.setVehicleId(rs.getInt("vehicle_id"));
        component.setComponentName(rs.getString("component_name"));
        component.setUsageHours(rs.getBigDecimal("usage_hours"));
        component.setMaxHours(rs.getBigDecimal("max_hours"));
        component.setStatus(rs.getString("status"));
        components.add(component);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return components;
  }

  /**
   * Update vehicle component
   * 
   * @param component the vehicle component to update
   * @return true if successful
   */
  public boolean updateVehicleComponent(VehicleComponentDTO component) {
    String sql = "UPDATE vehicle_components SET vehicle_id = ?, component_name = ?, usage_hours = ?, max_hours = ?, status = ? WHERE component_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, component.getVehicleId());
      stmt.setString(2, component.getComponentName());
      stmt.setBigDecimal(3, component.getUsageHours());
      stmt.setBigDecimal(4, component.getMaxHours());
      stmt.setString(5, component.getStatus());
      stmt.setInt(6, component.getComponentId());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Update component usage hours
   * 
   * @param componentId     the component ID
   * @param additionalHours hours to add to current usage
   * @return true if successful
   */
  public boolean updateUsageHours(int componentId, double additionalHours) {
    String sql = "UPDATE vehicle_components SET usage_hours = usage_hours + ? WHERE component_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setDouble(1, additionalHours);
      stmt.setInt(2, componentId);

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Delete vehicle component
   * 
   * @param componentId the component ID to delete
   * @return true if successful
   */
  public boolean deleteVehicleComponent(int componentId) {
    String sql = "DELETE FROM vehicle_components WHERE component_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, componentId);
      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
