package dataaccesslayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import transferobjects.VehicleDTO;

/**
 * Data Access Object for Vehicle operations in the PTFMS system.
 * Handles all database operations related to vehicles including
 * CRUD operations and specialized queries.
 */
public class VehicleDAO {

  /**
   * Adds a new vehicle to the database.
   * 
   * @param vehicle the VehicleDTO object containing vehicle information
   * @return true if the vehicle was successfully added, false otherwise
   */
  public boolean addVehicle(VehicleDTO vehicle) {
    String sql = "INSERT INTO vehicles (vin, vehicle_number, vehicle_type, make, model, year, fuel_type, consumption_rate, max_passengers, current_route, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, vehicle.getVin());
      stmt.setString(2, vehicle.getVehicleNumber());
      stmt.setString(3, vehicle.getVehicleType());
      stmt.setString(4, vehicle.getMake());
      stmt.setString(5, vehicle.getModel());
      stmt.setInt(6, vehicle.getYear());
      stmt.setString(7, vehicle.getFuelType());
      stmt.setBigDecimal(8, vehicle.getConsumptionRate());
      stmt.setInt(9, vehicle.getMaxPassengers());
      stmt.setString(10, vehicle.getCurrentRoute());
      stmt.setString(11, vehicle.getStatus());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Finds a vehicle by its unique database ID.
   * 
   * @param vehicleId the unique database ID of the vehicle to search for
   * @return VehicleDTO object if found, null otherwise
   */
  public VehicleDTO findById(int vehicleId) {
    String sql = "SELECT vehicle_id, vin, vehicle_number, vehicle_type, make, model, year, fuel_type, consumption_rate, max_passengers, current_route, status, created_at FROM vehicles WHERE vehicle_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, vehicleId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setVehicleId(rs.getInt("vehicle_id"));
        vehicle.setVin(rs.getString("vin"));
        vehicle.setVehicleNumber(rs.getString("vehicle_number"));
        vehicle.setVehicleType(rs.getString("vehicle_type"));
        vehicle.setMake(rs.getString("make"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("year"));
        vehicle.setFuelType(rs.getString("fuel_type"));
        vehicle.setConsumptionRate(rs.getBigDecimal("consumption_rate"));
        vehicle.setMaxPassengers(rs.getInt("max_passengers"));
        vehicle.setCurrentRoute(rs.getString("current_route"));
        vehicle.setStatus(rs.getString("status"));
        vehicle.setCreatedAt(rs.getTimestamp("created_at"));
        return vehicle;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Retrieves all vehicles from the database.
   * 
   * @return List of VehicleDTO objects representing all vehicles, empty list if
   *         none found
   */
  public List<VehicleDTO> getAllVehicles() {
    List<VehicleDTO> vehicles = new ArrayList<>();
    String sql = "SELECT vehicle_id, vin, vehicle_number, vehicle_type, make, model, year, fuel_type, consumption_rate, max_passengers, current_route, status, created_at FROM vehicles";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setVehicleId(rs.getInt("vehicle_id"));
        vehicle.setVin(rs.getString("vin"));
        vehicle.setVehicleNumber(rs.getString("vehicle_number"));
        vehicle.setVehicleType(rs.getString("vehicle_type"));
        vehicle.setMake(rs.getString("make"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("year"));
        vehicle.setFuelType(rs.getString("fuel_type"));
        vehicle.setConsumptionRate(rs.getBigDecimal("consumption_rate"));
        vehicle.setMaxPassengers(rs.getInt("max_passengers"));
        vehicle.setCurrentRoute(rs.getString("current_route"));
        vehicle.setStatus(rs.getString("status"));
        vehicle.setCreatedAt(rs.getTimestamp("created_at"));
        vehicles.add(vehicle);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return vehicles;
  }

  /**
   * Updates an existing vehicle's information in the database.
   * All vehicle fields will be updated with the values from the provided DTO.
   * 
   * @param vehicle the VehicleDTO object containing updated vehicle information
   * @return true if the vehicle was successfully updated, false otherwise
   */
  public boolean updateVehicle(VehicleDTO vehicle) {
    String sql = "UPDATE vehicles SET vin = ?, vehicle_number = ?, vehicle_type = ?, make = ?, model = ?, year = ?, fuel_type = ?, consumption_rate = ?, max_passengers = ?, current_route = ?, status = ? WHERE vehicle_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, vehicle.getVin());
      stmt.setString(2, vehicle.getVehicleNumber());
      stmt.setString(3, vehicle.getVehicleType());
      stmt.setString(4, vehicle.getMake());
      stmt.setString(5, vehicle.getModel());
      stmt.setInt(6, vehicle.getYear());
      stmt.setString(7, vehicle.getFuelType());
      stmt.setBigDecimal(8, vehicle.getConsumptionRate());
      stmt.setInt(9, vehicle.getMaxPassengers());
      stmt.setString(10, vehicle.getCurrentRoute());
      stmt.setString(11, vehicle.getStatus());
      stmt.setInt(12, vehicle.getVehicleId());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Deletes a vehicle from the database permanently.
   * This operation cannot be undone. Consider using status updates instead.
   * 
   * @param vehicleId the unique database ID of the vehicle to delete
   * @return true if the vehicle was successfully deleted, false otherwise
   */
  public boolean deleteVehicle(int vehicleId) {
    String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, vehicleId);
      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Finds a vehicle by its Vehicle Identification Number (VIN).
   * VIN is a unique identifier for each vehicle.
   * 
   * @param vin the Vehicle Identification Number to search for
   * @return VehicleDTO object if found, null otherwise
   */
  public VehicleDTO findByVin(String vin) {
    String sql = "SELECT vehicle_id, vin, vehicle_number, vehicle_type, make, model, year, fuel_type, consumption_rate, max_passengers, current_route, status, created_at FROM vehicles WHERE vin = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, vin);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setVehicleId(rs.getInt("vehicle_id"));
        vehicle.setVin(rs.getString("vin"));
        vehicle.setVehicleNumber(rs.getString("vehicle_number"));
        vehicle.setVehicleType(rs.getString("vehicle_type"));
        vehicle.setMake(rs.getString("make"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("year"));
        vehicle.setFuelType(rs.getString("fuel_type"));
        vehicle.setConsumptionRate(rs.getBigDecimal("consumption_rate"));
        vehicle.setMaxPassengers(rs.getInt("max_passengers"));
        vehicle.setCurrentRoute(rs.getString("current_route"));
        vehicle.setStatus(rs.getString("status"));
        vehicle.setCreatedAt(rs.getTimestamp("created_at"));
        return vehicle;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Finds all vehicles with a specific operational status.
   * Common statuses include ACTIVE, INACTIVE, MAINTENANCE, OUT_OF_SERVICE.
   * 
   * @param status the status value to search for
   * @return list of VehicleDTO objects with the specified status, empty list if
   *         none found
   */
  public List<VehicleDTO> findByStatus(String status) {
    List<VehicleDTO> vehicles = new ArrayList<>();
    String sql = "SELECT vehicle_id, vin, vehicle_number, vehicle_type, make, model, year, fuel_type, consumption_rate, max_passengers, current_route, status, created_at FROM vehicles WHERE status = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, status);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setVehicleId(rs.getInt("vehicle_id"));
        vehicle.setVin(rs.getString("vin"));
        vehicle.setVehicleNumber(rs.getString("vehicle_number"));
        vehicle.setVehicleType(rs.getString("vehicle_type"));
        vehicle.setMake(rs.getString("make"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("year"));
        vehicle.setFuelType(rs.getString("fuel_type"));
        vehicle.setConsumptionRate(rs.getBigDecimal("consumption_rate"));
        vehicle.setMaxPassengers(rs.getInt("max_passengers"));
        vehicle.setCurrentRoute(rs.getString("current_route"));
        vehicle.setStatus(rs.getString("status"));
        vehicle.setCreatedAt(rs.getTimestamp("created_at"));
        vehicles.add(vehicle);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return vehicles;
  }
}
