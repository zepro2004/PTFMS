package dataaccesslayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import transferobjects.FuelLogDTO;

/**
 * Data Access Object for Fuel Log operations in the PTFMS system.
 * Handles all database operations related to fuel consumption tracking
 * including CRUD operations, fuel efficiency calculations, cost analysis,
 * and reporting queries for fuel management.
 */
public class FuelLogDAO {

  /**
   * Adds a new fuel log entry to the database.
   * 
   * @param fuelLog the FuelLogDTO object containing fuel consumption data
   * @return true if the fuel log was successfully added, false otherwise
   */
  public boolean addFuelLog(FuelLogDTO fuelLog) {
    String sql = "INSERT INTO fuel_logs (vehicle_id, log_date, fuel_type, amount, cost, distance, operator_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, fuelLog.getVehicleId());
      stmt.setDate(2, fuelLog.getLogDate());
      stmt.setString(3, fuelLog.getFuelType());
      stmt.setBigDecimal(4, fuelLog.getAmount());
      stmt.setBigDecimal(5, fuelLog.getCost());
      stmt.setBigDecimal(6, fuelLog.getDistance());
      if (fuelLog.getOperatorId() != null) {
        stmt.setInt(7, fuelLog.getOperatorId());
      } else {
        stmt.setNull(7, java.sql.Types.INTEGER);
      }

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Find fuel log by ID
   * 
   * @param fuelLogId the fuel log ID to search for
   * @return FuelLogDTO if found, null otherwise
   */
  public FuelLogDTO findById(int fuelLogId) {
    String sql = "SELECT fuel_log_id, vehicle_id, log_date, fuel_type, amount, cost, distance, operator_id FROM fuel_logs WHERE fuel_log_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, fuelLogId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        FuelLogDTO fuelLog = new FuelLogDTO();
        fuelLog.setFuelLogId(rs.getInt("fuel_log_id"));
        fuelLog.setVehicleId(rs.getInt("vehicle_id"));
        fuelLog.setLogDate(rs.getDate("log_date"));
        fuelLog.setFuelType(rs.getString("fuel_type"));
        fuelLog.setAmount(rs.getBigDecimal("amount"));
        fuelLog.setCost(rs.getBigDecimal("cost"));
        fuelLog.setDistance(rs.getBigDecimal("distance"));
        int operatorId = rs.getInt("operator_id");
        if (!rs.wasNull()) {
          fuelLog.setOperatorId(operatorId);
        }
        return fuelLog;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get all fuel logs
   * 
   * @return list of all fuel logs
   */
  public List<FuelLogDTO> getAllFuelLogs() {
    List<FuelLogDTO> fuelLogs = new ArrayList<>();
    String sql = "SELECT fuel_log_id, vehicle_id, log_date, fuel_type, amount, cost, distance, operator_id FROM fuel_logs";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        FuelLogDTO fuelLog = new FuelLogDTO();
        fuelLog.setFuelLogId(rs.getInt("fuel_log_id"));
        fuelLog.setVehicleId(rs.getInt("vehicle_id"));
        fuelLog.setLogDate(rs.getDate("log_date"));
        fuelLog.setFuelType(rs.getString("fuel_type"));
        fuelLog.setAmount(rs.getBigDecimal("amount"));
        fuelLog.setCost(rs.getBigDecimal("cost"));
        fuelLog.setDistance(rs.getBigDecimal("distance"));
        int operatorId = rs.getInt("operator_id");
        if (!rs.wasNull()) {
          fuelLog.setOperatorId(operatorId);
        }
        fuelLogs.add(fuelLog);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return fuelLogs;
  }

  /**
   * Get fuel logs by vehicle ID
   * 
   * @param vehicleId the vehicle ID to search for
   * @return list of fuel logs for the vehicle
   */
  public List<FuelLogDTO> getFuelLogsByVehicleId(int vehicleId) {
    List<FuelLogDTO> fuelLogs = new ArrayList<>();
    String sql = "SELECT fuel_log_id, vehicle_id, log_date, fuel_type, amount, cost, distance, operator_id FROM fuel_logs WHERE vehicle_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, vehicleId);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        FuelLogDTO fuelLog = new FuelLogDTO();
        fuelLog.setFuelLogId(rs.getInt("fuel_log_id"));
        fuelLog.setVehicleId(rs.getInt("vehicle_id"));
        fuelLog.setLogDate(rs.getDate("log_date"));
        fuelLog.setFuelType(rs.getString("fuel_type"));
        fuelLog.setAmount(rs.getBigDecimal("amount"));
        fuelLog.setCost(rs.getBigDecimal("cost"));
        fuelLog.setDistance(rs.getBigDecimal("distance"));
        int operatorId = rs.getInt("operator_id");
        if (!rs.wasNull()) {
          fuelLog.setOperatorId(operatorId);
        }
        fuelLogs.add(fuelLog);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return fuelLogs;
  }

  /**
   * Get fuel logs by operator ID
   * 
   * @param operatorId the operator ID to search for
   * @return list of fuel logs for the operator
   */
  public List<FuelLogDTO> getFuelLogsByOperatorId(int operatorId) {
    List<FuelLogDTO> fuelLogs = new ArrayList<>();
    String sql = "SELECT fuel_log_id, vehicle_id, log_date, fuel_type, amount, cost, distance, operator_id FROM fuel_logs WHERE operator_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, operatorId);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        FuelLogDTO fuelLog = new FuelLogDTO();
        fuelLog.setFuelLogId(rs.getInt("fuel_log_id"));
        fuelLog.setVehicleId(rs.getInt("vehicle_id"));
        fuelLog.setLogDate(rs.getDate("log_date"));
        fuelLog.setFuelType(rs.getString("fuel_type"));
        fuelLog.setAmount(rs.getBigDecimal("amount"));
        fuelLog.setCost(rs.getBigDecimal("cost"));
        fuelLog.setDistance(rs.getBigDecimal("distance"));
        int opId = rs.getInt("operator_id");
        if (!rs.wasNull()) {
          fuelLog.setOperatorId(opId);
        }
        fuelLogs.add(fuelLog);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return fuelLogs;
  }

  /**
   * Update fuel log
   * 
   * @param fuelLog the fuel log to update
   * @return true if successful
   */
  public boolean updateFuelLog(FuelLogDTO fuelLog) {
    String sql = "UPDATE fuel_logs SET vehicle_id = ?, log_date = ?, fuel_type = ?, amount = ?, cost = ?, distance = ?, operator_id = ? WHERE fuel_log_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, fuelLog.getVehicleId());
      stmt.setDate(2, fuelLog.getLogDate());
      stmt.setString(3, fuelLog.getFuelType());
      stmt.setBigDecimal(4, fuelLog.getAmount());
      stmt.setBigDecimal(5, fuelLog.getCost());
      stmt.setBigDecimal(6, fuelLog.getDistance());
      if (fuelLog.getOperatorId() != null) {
        stmt.setInt(7, fuelLog.getOperatorId());
      } else {
        stmt.setNull(7, java.sql.Types.INTEGER);
      }
      stmt.setInt(8, fuelLog.getFuelLogId());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Delete fuel log
   * 
   * @param fuelLogId the fuel log ID to delete
   * @return true if successful
   */
  public boolean deleteFuelLog(int fuelLogId) {
    String sql = "DELETE FROM fuel_logs WHERE fuel_log_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, fuelLogId);
      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}
