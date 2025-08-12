package dataaccesslayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import transferobjects.ReportDTO;

/**
 * Data Access Object for Reports in the PTFMS system.
 * Handles report data persistence and comprehensive analytics including
 * performance metrics, financial summaries, operational reports, and
 * business intelligence data aggregation and storage.
 */
public class ReportDAO {

  /**
   * Constructor
   */
  public ReportDAO() {
  }

  /**
   * Save a report to the database
   * 
   * @param report the ReportDTO to save
   * @return true if successful, false otherwise
   */
  public boolean saveReport(ReportDTO report) {
    String sql = "INSERT INTO reports (title, report_type, data_json, generated_by, " +
        "period_start, period_end, format, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, report.getTitle());
      pstmt.setString(2, report.getReportType());
      pstmt.setString(3, convertMapToJson(report.getData()));
      pstmt.setInt(4, report.getGeneratedBy());
      pstmt.setTimestamp(5, report.getPeriodStart());
      pstmt.setTimestamp(6, report.getPeriodEnd());
      pstmt.setString(7, report.getFormat());
      pstmt.setString(8, report.getStatus());

      int rowsAffected = pstmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      System.err.println("Error saving report: " + e.getMessage());
      return false;
    }
  }

  /**
   * Get all reports from the database
   * 
   * @return list of ReportDTO objects
   */
  public List<ReportDTO> getAllReports() {
    List<ReportDTO> reports = new ArrayList<>();
    String sql = "SELECT * FROM reports ORDER BY generated_at DESC";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {

      while (rs.next()) {
        reports.add(mapResultSetToReport(rs));
      }
    } catch (SQLException e) {
      System.err.println("Error retrieving reports: " + e.getMessage());
    }

    return reports;
  }

  /**
   * Get reports by type
   * 
   * @param reportType the type of reports to retrieve
   * @return list of ReportDTO objects
   */
  public List<ReportDTO> getReportsByType(String reportType) {
    List<ReportDTO> reports = new ArrayList<>();
    String sql = "SELECT * FROM reports WHERE report_type = ? ORDER BY generated_at DESC";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, reportType);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        reports.add(mapResultSetToReport(rs));
      }
    } catch (SQLException e) {
      System.err.println("Error retrieving reports by type: " + e.getMessage());
    }

    return reports;
  }

  /**
   * Generate fleet utilization report data
   * 
   * @param startDate start date for the report
   * @param endDate   end date for the report
   * @return map containing fleet utilization metrics
   */
  public Map<String, Object> generateFleetUtilizationData(Timestamp startDate, Timestamp endDate) {
    Map<String, Object> data = new HashMap<>();

    try (Connection conn = DataSource.getConnection()) {
      // Total vehicles
      String totalVehiclesSql = "SELECT COUNT(*) as total_vehicles FROM vehicles WHERE status != 'RETIRED'";
      try (PreparedStatement pstmt = conn.prepareStatement(totalVehiclesSql);
          ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          data.put("totalVehicles", rs.getInt("total_vehicles"));
        }
      }

      // Active vehicles in period
      String activeVehiclesSql = "SELECT COUNT(DISTINCT vehicle_id) as active_vehicles " +
          "FROM gps_tracking WHERE timestamp BETWEEN ? AND ?";
      try (PreparedStatement pstmt = conn.prepareStatement(activeVehiclesSql)) {
        pstmt.setTimestamp(1, startDate);
        pstmt.setTimestamp(2, endDate);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
          data.put("activeVehicles", rs.getInt("active_vehicles"));
        }
      }

      // Vehicle status breakdown
      String statusSql = "SELECT status, COUNT(*) as count FROM vehicles GROUP BY status";
      try (PreparedStatement pstmt = conn.prepareStatement(statusSql);
          ResultSet rs = pstmt.executeQuery()) {
        Map<String, Integer> statusBreakdown = new HashMap<>();
        while (rs.next()) {
          statusBreakdown.put(rs.getString("status"), rs.getInt("count"));
        }
        data.put("statusBreakdown", statusBreakdown);
      }

    } catch (SQLException e) {
      System.err.println("Error generating fleet utilization data: " + e.getMessage());
    }

    return data;
  }

  /**
   * Generate maintenance cost analysis data
   * 
   * @param startDate start date for the report
   * @param endDate   end date for the report
   * @return map containing maintenance cost metrics
   */
  public Map<String, Object> generateMaintenanceCostData(Timestamp startDate, Timestamp endDate) {
    Map<String, Object> data = new HashMap<>();

    try (Connection conn = DataSource.getConnection()) {
      // Total maintenance cost
      String totalCostSql = "SELECT SUM(cost) as total_cost FROM maintenance " +
          "WHERE scheduled_date BETWEEN ? AND ?";
      try (PreparedStatement pstmt = conn.prepareStatement(totalCostSql)) {
        pstmt.setTimestamp(1, startDate);
        pstmt.setTimestamp(2, endDate);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
          data.put("totalCost", rs.getBigDecimal("total_cost"));
        }
      }

      // Cost by maintenance type
      String costByTypeSql = "SELECT maintenance_type, SUM(cost) as type_cost " +
          "FROM maintenance WHERE scheduled_date BETWEEN ? AND ? " +
          "GROUP BY maintenance_type";
      try (PreparedStatement pstmt = conn.prepareStatement(costByTypeSql)) {
        pstmt.setTimestamp(1, startDate);
        pstmt.setTimestamp(2, endDate);
        ResultSet rs = pstmt.executeQuery();
        Map<String, Object> costByType = new HashMap<>();
        while (rs.next()) {
          costByType.put(rs.getString("maintenance_type"), rs.getBigDecimal("type_cost"));
        }
        data.put("costByType", costByType);
      }

      // Average cost per vehicle
      String avgCostSql = "SELECT AVG(cost) as avg_cost FROM maintenance " +
          "WHERE scheduled_date BETWEEN ? AND ?";
      try (PreparedStatement pstmt = conn.prepareStatement(avgCostSql)) {
        pstmt.setTimestamp(1, startDate);
        pstmt.setTimestamp(2, endDate);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
          data.put("averageCost", rs.getBigDecimal("avg_cost"));
        }
      }

    } catch (SQLException e) {
      System.err.println("Error generating maintenance cost data: " + e.getMessage());
    }

    return data;
  }

  /**
   * Generate fuel efficiency analysis data
   * 
   * @param startDate start date for the report
   * @param endDate   end date for the report
   * @return map containing fuel efficiency metrics
   */
  public Map<String, Object> generateFuelEfficiencyData(Timestamp startDate, Timestamp endDate) {
    Map<String, Object> data = new HashMap<>();

    try (Connection conn = DataSource.getConnection()) {
      // Total fuel consumed
      String totalFuelSql = "SELECT SUM(fuel_amount) as total_fuel FROM fuel_logs " +
          "WHERE log_date BETWEEN ? AND ?";
      try (PreparedStatement pstmt = conn.prepareStatement(totalFuelSql)) {
        pstmt.setTimestamp(1, startDate);
        pstmt.setTimestamp(2, endDate);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
          data.put("totalFuelConsumed", rs.getBigDecimal("total_fuel"));
        }
      }

      // Fuel efficiency by vehicle
      String efficiencySql = "SELECT v.license_plate, v.vehicle_id, " +
          "SUM(f.fuel_amount) as fuel_used, " +
          "COUNT(DISTINCT DATE(f.log_date)) as days_active " +
          "FROM vehicles v " +
          "LEFT JOIN fuel_logs f ON v.vehicle_id = f.vehicle_id " +
          "WHERE f.log_date BETWEEN ? AND ? " +
          "GROUP BY v.vehicle_id, v.license_plate";
      try (PreparedStatement pstmt = conn.prepareStatement(efficiencySql)) {
        pstmt.setTimestamp(1, startDate);
        pstmt.setTimestamp(2, endDate);
        ResultSet rs = pstmt.executeQuery();
        List<Map<String, Object>> vehicleEfficiency = new ArrayList<>();
        while (rs.next()) {
          Map<String, Object> vehicleData = new HashMap<>();
          vehicleData.put("licensePlate", rs.getString("license_plate"));
          vehicleData.put("vehicleId", rs.getInt("vehicle_id"));
          vehicleData.put("fuelUsed", rs.getBigDecimal("fuel_used"));
          vehicleData.put("daysActive", rs.getInt("days_active"));
          vehicleEfficiency.add(vehicleData);
        }
        data.put("vehicleEfficiency", vehicleEfficiency);
      }

    } catch (SQLException e) {
      System.err.println("Error generating fuel efficiency data: " + e.getMessage());
    }

    return data;
  }

  /**
   * Generate operator performance data
   * 
   * @param startDate start date for the report
   * @param endDate   end date for the report
   * @return map containing operator performance metrics
   */
  public Map<String, Object> generateOperatorPerformanceData(Timestamp startDate, Timestamp endDate) {
    Map<String, Object> data = new HashMap<>();

    try (Connection conn = DataSource.getConnection()) {
      // Operator activity summary
      String operatorSql = "SELECT u.user_id, u.username, u.full_name, " +
          "COUNT(DISTINCT g.vehicle_id) as vehicles_operated, " +
          "COUNT(g.tracking_id) as gps_logs " +
          "FROM users u " +
          "LEFT JOIN gps_tracking g ON u.user_id = g.vehicle_id " +
          "WHERE u.role = 'OPERATOR' AND g.timestamp BETWEEN ? AND ? " +
          "GROUP BY u.user_id, u.username, u.full_name";
      try (PreparedStatement pstmt = conn.prepareStatement(operatorSql)) {
        pstmt.setTimestamp(1, startDate);
        pstmt.setTimestamp(2, endDate);
        ResultSet rs = pstmt.executeQuery();
        List<Map<String, Object>> operatorMetrics = new ArrayList<>();
        while (rs.next()) {
          Map<String, Object> operatorData = new HashMap<>();
          operatorData.put("userId", rs.getInt("user_id"));
          operatorData.put("username", rs.getString("username"));
          operatorData.put("fullName", rs.getString("full_name"));
          operatorData.put("vehiclesOperated", rs.getInt("vehicles_operated"));
          operatorData.put("gpsLogs", rs.getInt("gps_logs"));
          operatorMetrics.add(operatorData);
        }
        data.put("operatorMetrics", operatorMetrics);
      }

    } catch (SQLException e) {
      System.err.println("Error generating operator performance data: " + e.getMessage());
    }

    return data;
  }

  /**
   * Helper method to map ResultSet to ReportDTO
   * 
   * @param rs the ResultSet
   * @return ReportDTO object
   * @throws SQLException if database access error occurs
   */
  private ReportDTO mapResultSetToReport(ResultSet rs) throws SQLException {
    ReportDTO report = new ReportDTO();
    report.setReportId(rs.getInt("report_id"));
    report.setTitle(rs.getString("title"));
    report.setReportType(rs.getString("report_type"));
    report.setData(convertJsonToMap(rs.getString("data_json")));
    report.setGeneratedBy(rs.getInt("generated_by"));
    report.setGeneratedAt(rs.getTimestamp("generated_at"));
    report.setPeriodStart(rs.getTimestamp("period_start"));
    report.setPeriodEnd(rs.getTimestamp("period_end"));
    report.setFormat(rs.getString("format"));
    report.setStatus(rs.getString("status"));
    return report;
  }

  /**
   * Convert Map to JSON string (simplified implementation)
   * 
   * @param data the map to convert
   * @return JSON string
   */
  private String convertMapToJson(Map<String, Object> data) {
    if (data == null)
      return "{}";
    // Simplified JSON conversion - in production, use Jackson or Gson
    StringBuilder json = new StringBuilder("{");
    boolean first = true;
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      if (!first)
        json.append(",");
      json.append("\"").append(entry.getKey()).append("\":\"")
          .append(entry.getValue()).append("\"");
      first = false;
    }
    json.append("}");
    return json.toString();
  }

  /**
   * Convert JSON string to Map (simplified implementation)
   * 
   * @param json the JSON string to convert
   * @return Map object
   */
  private Map<String, Object> convertJsonToMap(String json) {
    Map<String, Object> data = new HashMap<>();
    // Simplified JSON parsing - in production, use Jackson or Gson
    if (json == null || json.equals("{}"))
      return data;

    // Basic parsing for simple key-value pairs
    json = json.replace("{", "").replace("}", "");
    String[] pairs = json.split(",");
    for (String pair : pairs) {
      String[] keyValue = pair.split(":");
      if (keyValue.length == 2) {
        String key = keyValue[0].replace("\"", "").trim();
        String value = keyValue[1].replace("\"", "").trim();
        data.put(key, value);
      }
    }
    return data;
  }
}
