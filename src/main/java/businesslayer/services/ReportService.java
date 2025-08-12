package businesslayer.services;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataaccesslayer.ReportDAO;
import transferobjects.ReportDTO;

/**
 * Service layer for comprehensive reporting functionality in the PTFMS system.
 * Handles generation of various analytics reports including performance
 * metrics,
 * operational summaries, financial reports, and business intelligence
 * dashboards.
 * Aggregates data from multiple sources to provide meaningful insights.
 */
public class ReportService {
  private final ReportDAO reportDAO;

  /**
   * Constructor
   */
  public ReportService() {
    this.reportDAO = new ReportDAO();
  }

  /**
   * Generate fleet utilization report
   * 
   * @param startDate   start date for the report period
   * @param endDate     end date for the report period
   * @param generatedBy user ID generating the report
   * @return ReportDTO containing fleet utilization data
   */
  public ReportDTO generateFleetUtilizationReport(Timestamp startDate, Timestamp endDate, int generatedBy) {
    ReportDTO report = new ReportDTO();
    report.setTitle("Fleet Utilization Report");
    report.setReportType("FLEET");
    report.setPeriodStart(startDate);
    report.setPeriodEnd(endDate);
    report.setGeneratedBy(generatedBy);
    report.setFormat("JSON");
    report.setStatus("PENDING");

    try {
      Map<String, Object> data = reportDAO.generateFleetUtilizationData(startDate, endDate);

      // Calculate utilization rate
      if (data.containsKey("totalVehicles") && data.containsKey("activeVehicles")) {
        int totalVehicles = (Integer) data.get("totalVehicles");
        int activeVehicles = (Integer) data.get("activeVehicles");
        double utilizationRate = totalVehicles > 0 ? (double) activeVehicles / totalVehicles * 100 : 0;
        data.put("utilizationRate", utilizationRate);
      }

      report.setData(data);
      report.setStatus("COMPLETED");

      // Save report to database
      if (reportDAO.saveReport(report)) {
        System.out.println("Fleet utilization report generated successfully");
      }

    } catch (Exception e) {
      System.err.println("Error generating fleet utilization report: " + e.getMessage());
      report.setStatus("FAILED");
      report.setData(new HashMap<>());
    }

    return report;
  }

  /**
   * Generate maintenance cost analysis report
   * 
   * @param startDate   start date for the report period
   * @param endDate     end date for the report period
   * @param generatedBy user ID generating the report
   * @return ReportDTO containing maintenance cost analysis
   */
  public ReportDTO generateMaintenanceCostReport(Timestamp startDate, Timestamp endDate, int generatedBy) {
    ReportDTO report = new ReportDTO();
    report.setTitle("Maintenance Cost Analysis Report");
    report.setReportType("MAINTENANCE");
    report.setPeriodStart(startDate);
    report.setPeriodEnd(endDate);
    report.setGeneratedBy(generatedBy);
    report.setFormat("JSON");
    report.setStatus("PENDING");

    try {
      Map<String, Object> data = reportDAO.generateMaintenanceCostData(startDate, endDate);
      report.setData(data);
      report.setStatus("COMPLETED");

      // Save report to database
      if (reportDAO.saveReport(report)) {
        System.out.println("Maintenance cost report generated successfully");
      }

    } catch (Exception e) {
      System.err.println("Error generating maintenance cost report: " + e.getMessage());
      report.setStatus("FAILED");
      report.setData(new HashMap<>());
    }

    return report;
  }

  /**
   * Generate fuel efficiency analysis report
   * 
   * @param startDate   start date for the report period
   * @param endDate     end date for the report period
   * @param generatedBy user ID generating the report
   * @return ReportDTO containing fuel efficiency analysis
   */
  public ReportDTO generateFuelEfficiencyReport(Timestamp startDate, Timestamp endDate, int generatedBy) {
    ReportDTO report = new ReportDTO();
    report.setTitle("Fuel Efficiency Analysis Report");
    report.setReportType("FUEL");
    report.setPeriodStart(startDate);
    report.setPeriodEnd(endDate);
    report.setGeneratedBy(generatedBy);
    report.setFormat("JSON");
    report.setStatus("PENDING");

    try {
      Map<String, Object> data = reportDAO.generateFuelEfficiencyData(startDate, endDate);
      report.setData(data);
      report.setStatus("COMPLETED");

      // Save report to database
      if (reportDAO.saveReport(report)) {
        System.out.println("Fuel efficiency report generated successfully");
      }

    } catch (Exception e) {
      System.err.println("Error generating fuel efficiency report: " + e.getMessage());
      report.setStatus("FAILED");
      report.setData(new HashMap<>());
    }

    return report;
  }

  /**
   * Generate operator performance report
   * 
   * @param startDate   start date for the report period
   * @param endDate     end date for the report period
   * @param generatedBy user ID generating the report
   * @return ReportDTO containing operator performance analysis
   */
  public ReportDTO generateOperatorPerformanceReport(Timestamp startDate, Timestamp endDate, int generatedBy) {
    ReportDTO report = new ReportDTO();
    report.setTitle("Operator Performance Report");
    report.setReportType("OPERATOR");
    report.setPeriodStart(startDate);
    report.setPeriodEnd(endDate);
    report.setGeneratedBy(generatedBy);
    report.setFormat("JSON");
    report.setStatus("PENDING");

    try {
      Map<String, Object> data = reportDAO.generateOperatorPerformanceData(startDate, endDate);
      report.setData(data);
      report.setStatus("COMPLETED");

      // Save report to database
      if (reportDAO.saveReport(report)) {
        System.out.println("Operator performance report generated successfully");
      }

    } catch (Exception e) {
      System.err.println("Error generating operator performance report: " + e.getMessage());
      report.setStatus("FAILED");
      report.setData(new HashMap<>());
    }

    return report;
  }

  /**
   * Generate comprehensive dashboard report combining multiple metrics
   * 
   * @param startDate   start date for the report period
   * @param endDate     end date for the report period
   * @param generatedBy user ID generating the report
   * @return ReportDTO containing comprehensive dashboard data
   */
  public ReportDTO generateDashboardReport(Timestamp startDate, Timestamp endDate, int generatedBy) {
    ReportDTO report = new ReportDTO();
    report.setTitle("Comprehensive Dashboard Report");
    report.setReportType("FLEET");
    report.setPeriodStart(startDate);
    report.setPeriodEnd(endDate);
    report.setGeneratedBy(generatedBy);
    report.setFormat("JSON");
    report.setStatus("PENDING");

    try {
      Map<String, Object> dashboardData = new HashMap<>();

      // Collect fleet utilization data
      Map<String, Object> fleetData = reportDAO.generateFleetUtilizationData(startDate, endDate);
      dashboardData.put("fleet", fleetData);

      // Collect maintenance cost data
      Map<String, Object> maintenanceData = reportDAO.generateMaintenanceCostData(startDate, endDate);
      dashboardData.put("maintenance", maintenanceData);

      // Collect fuel efficiency data
      Map<String, Object> fuelData = reportDAO.generateFuelEfficiencyData(startDate, endDate);
      dashboardData.put("fuel", fuelData);

      // Collect operator performance data
      Map<String, Object> operatorData = reportDAO.generateOperatorPerformanceData(startDate, endDate);
      dashboardData.put("operators", operatorData);

      report.setData(dashboardData);
      report.setStatus("COMPLETED");

      // Save comprehensive report to database
      if (reportDAO.saveReport(report)) {
        System.out.println("Comprehensive dashboard report generated successfully");
      }

    } catch (Exception e) {
      System.err.println("Error generating dashboard report: " + e.getMessage());
      report.setStatus("FAILED");
      report.setData(new HashMap<>());
    }

    return report;
  }

  /**
   * Get all reports from the system
   * 
   * @return list of all ReportDTO objects
   */
  public List<ReportDTO> getAllReports() {
    return reportDAO.getAllReports();
  }

  /**
   * Get reports by type
   * 
   * @param reportType the type of reports to retrieve
   * @return list of ReportDTO objects of the specified type
   */
  public List<ReportDTO> getReportsByType(String reportType) {
    return reportDAO.getReportsByType(reportType);
  }

  /**
   * Generate a quick summary report for management overview
   * 
   * @param startDate   start date for the report period
   * @param endDate     end date for the report period
   * @param generatedBy user ID generating the report
   * @return Map containing key performance indicators
   */
  public Map<String, Object> generateQuickSummary(Timestamp startDate, Timestamp endDate, int generatedBy) {
    Map<String, Object> summary = new HashMap<>();

    try {
      // Fleet summary
      Map<String, Object> fleetData = reportDAO.generateFleetUtilizationData(startDate, endDate);
      summary.put("totalVehicles", fleetData.getOrDefault("totalVehicles", 0));
      summary.put("activeVehicles", fleetData.getOrDefault("activeVehicles", 0));

      // Calculate utilization rate
      if (fleetData.containsKey("totalVehicles") && fleetData.containsKey("activeVehicles")) {
        int totalVehicles = (Integer) fleetData.get("totalVehicles");
        int activeVehicles = (Integer) fleetData.get("activeVehicles");
        double utilizationRate = totalVehicles > 0 ? (double) activeVehicles / totalVehicles * 100 : 0;
        summary.put("fleetUtilization", Math.round(utilizationRate * 100.0) / 100.0);
      }

      // Maintenance summary
      Map<String, Object> maintenanceData = reportDAO.generateMaintenanceCostData(startDate, endDate);
      summary.put("totalMaintenanceCost", maintenanceData.getOrDefault("totalCost", 0));

      // Fuel summary
      Map<String, Object> fuelData = reportDAO.generateFuelEfficiencyData(startDate, endDate);
      summary.put("totalFuelConsumed", fuelData.getOrDefault("totalFuelConsumed", 0));

      summary.put("reportGenerated", new Timestamp(System.currentTimeMillis()));
      summary.put("periodStart", startDate);
      summary.put("periodEnd", endDate);

    } catch (Exception e) {
      System.err.println("Error generating quick summary: " + e.getMessage());
      summary.put("error", "Unable to generate summary");
    }

    return summary;
  }
}
