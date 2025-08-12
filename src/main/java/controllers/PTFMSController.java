package controllers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import businesslayer.PTFMSBusinessController;
import businesslayer.services.GPSTrackingService;
import businesslayer.services.ReportService;
import businesslayer.services.VehicleComponentService;
import transferobjects.AlertDTO;
import transferobjects.FuelLogDTO;
import transferobjects.GPSTrackingDTO;
import transferobjects.MaintenanceDTO;
import transferobjects.ReportDTO;
import transferobjects.UserDTO;
import transferobjects.VehicleComponentDTO;
import transferobjects.VehicleDTO;

/**
 * Modern controller class implementing proper 3-tier architecture for the PTFMS
 * system.
 * Serves as the presentation layer entry point and delegates all business logic
 * to PTFMSBusinessController. This controller coordinates between the
 * presentation
 * layer (servlets) and the business layer services.
 * 
 * Key Features:
 * - Complete user management with proper registration/authentication
 * - Comprehensive vehicle management with Builder/Command patterns
 * - Advanced GPS tracking with station events
 * - Predictive maintenance management
 * - Fuel efficiency monitoring
 * - Real-time alert system with Observer pattern
 * - Business intelligence reporting
 */
public class PTFMSController {
  private final PTFMSBusinessController businessController;
  private final GPSTrackingService gpsTrackingService;
  private final ReportService reportService;
  private final VehicleComponentService vehicleComponentService;

  /**
   * Initializes the controller by creating instances of all required business
   * layer services.
   * Sets up the business controller and specialized services for GPS tracking,
   * reporting,
   * and vehicle component management.
   */
  public PTFMSController() {
    this.businessController = new PTFMSBusinessController();
    this.gpsTrackingService = new GPSTrackingService();
    this.reportService = new ReportService();
    this.vehicleComponentService = new VehicleComponentService();
  }

  // ========== USER MANAGEMENT ==========

  /**
   * Register a new user with complete information
   * 
   * @param username the username
   * @param password the password
   * @param email    the email address
   * @param name     the full name
   * @param role     the user role (Manager/Operator)
   * @return true if registration successful
   */
  public boolean registerUser(String username, String password, String email, String name, String role) {
    return businessController.registerUser(username, password, email, name, role);
  }

  /**
   * Simplified user registration for backward compatibility
   * 
   * @param username the username
   * @param password the password
   * @param role     the role
   * @return true if successful
   */
  public boolean registerUser(String username, String password, String role) {
    // Generate default email and use username as name
    String email = username.toLowerCase() + "@ptfms.com";
    String name = username;
    return businessController.registerUser(username, password, email, name, role);
  }

  /**
   * Authenticate user login
   * 
   * @param username the username
   * @param password the password
   * @return UserDTO if successful, null otherwise
   */
  public UserDTO loginUser(String username, String password) {
    return businessController.authenticateUser(username, password);
  }

  /**
   * Get user by ID
   * 
   * @param userId the user ID
   * @return UserDTO if found
   */
  public UserDTO getUserById(int userId) {
    return businessController.getUserById(userId);
  }

  /**
   * Get all users
   * 
   * @return list of all users
   */
  public List<UserDTO> getAllUsers() {
    return businessController.getAllUsers();
  }

  // ========== VEHICLE MANAGEMENT ==========

  /**
   * Add a new vehicle with complete details
   * 
   * @param vin           the VIN
   * @param vehicleNumber the vehicle number
   * @param vehicleType   the vehicle type (Bus/Train/Trolley)
   * @param make          the make
   * @param model         the model
   * @param year          the year
   * @return true if successful
   */
  public boolean addVehicle(String vin, String vehicleNumber, String vehicleType,
      String make, String model, int year) {
    return businessController.addVehicle(vin, vehicleNumber, vehicleType, make, model, year);
  }

  /**
   * Simplified vehicle addition for backward compatibility
   * 
   * @param vin   the VIN
   * @param make  the make
   * @param model the model
   * @param year  the year
   * @return true if successful
   */
  public boolean addVehicle(String vin, String make, String model, int year) {
    // Generate default vehicle number and type
    String vehicleNumber = "V" + System.currentTimeMillis();
    String vehicleType = "Bus"; // Default type
    return businessController.addVehicle(vin, vehicleNumber, vehicleType, make, model, year);
  }

  /**
   * Get all vehicles
   * 
   * @return list of vehicles
   */
  public List<VehicleDTO> getAllVehicles() {
    return businessController.getAllVehicles();
  }

  /**
   * Get vehicle by ID
   * 
   * @param vehicleId the vehicle ID
   * @return VehicleDTO if found, null otherwise
   */
  public VehicleDTO getVehicleById(int vehicleId) {
    return businessController.getVehicleById(vehicleId);
  }

  /**
   * Update vehicle status
   * 
   * @param vehicleId the vehicle ID
   * @param status    the new status
   * @return true if successful
   */
  public boolean updateVehicleStatus(int vehicleId, String status) {
    return businessController.updateVehicleStatus(vehicleId, status);
  }

  /**
   * Update vehicle details
   * 
   * @param vehicleId the vehicle ID
   * @param vin       the vehicle identification number
   * @param make      the vehicle make
   * @param model     the vehicle model
   * @param year      the manufacturing year
   * @return true if successful
   */
  public boolean updateVehicle(int vehicleId, String vin, String make, String model, int year) {
    // First get the existing vehicle to preserve other fields
    VehicleDTO existingVehicle = businessController.getVehicleById(vehicleId);
    if (existingVehicle == null) {
      return false;
    }

    // Update only the specified fields
    existingVehicle.setVin(vin);
    existingVehicle.setMake(make);
    existingVehicle.setModel(model);
    existingVehicle.setYear(year);

    return businessController.updateVehicle(existingVehicle);
  }

  /**
   * Update vehicle details including vehicle type
   * 
   * @param vehicleId   the vehicle ID
   * @param vin         the vehicle identification number
   * @param vehicleType the vehicle type
   * @param make        the vehicle make
   * @param model       the vehicle model
   * @param year        the manufacturing year
   * @return true if successful
   */
  public boolean updateVehicle(int vehicleId, String vin, String vehicleType, String make, String model, int year) {
    // First get the existing vehicle to preserve other fields
    VehicleDTO existingVehicle = businessController.getVehicleById(vehicleId);
    if (existingVehicle == null) {
      return false;
    }

    // Update the specified fields
    existingVehicle.setVin(vin);
    existingVehicle.setVehicleType(vehicleType);
    existingVehicle.setMake(make);
    existingVehicle.setModel(model);
    existingVehicle.setYear(year);

    // Update fuel type based on vehicle type if needed
    String currentFuelType = existingVehicle.getFuelType();
    if (currentFuelType == null || currentFuelType.isEmpty()) {
      String fuelType = determineFuelTypeFromVehicleType(vehicleType);
      existingVehicle.setFuelType(fuelType);
    }

    return businessController.updateVehicle(existingVehicle);
  }

  /**
   * Helper method to determine fuel type from vehicle type
   */
  private String determineFuelTypeFromVehicleType(String vehicleType) {
    if (vehicleType == null)
      return "Diesel";

    switch (vehicleType.toLowerCase()) {
      case "electric bus":
      case "electric light rail":
        return "Electric";
      case "cng bus":
        return "CNG";
      case "diesel bus":
      case "diesel-electric train":
      default:
        return "Diesel";
    }
  }

  // ========== GPS TRACKING ==========

  /**
   * Add GPS tracking entry with location data
   * 
   * @param vehicleId  the vehicle ID
   * @param latitude   the latitude
   * @param longitude  the longitude
   * @param operatorId the operator ID (optional)
   * @return true if successful
   */
  public boolean addGPSTracking(int vehicleId, BigDecimal latitude, BigDecimal longitude, Integer operatorId) {
    GPSTrackingDTO gpsTracking = new GPSTrackingDTO();
    gpsTracking.setVehicleId(vehicleId);
    gpsTracking.setLatitude(latitude);
    gpsTracking.setLongitude(longitude);
    gpsTracking.setOperatorId(operatorId);
    gpsTracking.setTimestamp(new Timestamp(System.currentTimeMillis()));
    gpsTracking.setEventType("LOCATION");

    return gpsTrackingService.addGPSTracking(gpsTracking);
  }

  /**
   * Add GPS tracking entry with full DTO
   * 
   * @param gpsTracking the GPS tracking entry
   * @return true if successful
   */
  public boolean addGPSTracking(GPSTrackingDTO gpsTracking) {
    return gpsTrackingService.addGPSTracking(gpsTracking);
  }

  /**
   * Get GPS tracking by vehicle ID
   * 
   * @param vehicleId the vehicle ID
   * @return list of GPS tracking entries
   */
  public List<GPSTrackingDTO> getGPSTrackingByVehicleId(int vehicleId) {
    return gpsTrackingService.getGPSTrackingByVehicleId(vehicleId);
  }

  /**
   * Get latest GPS position for a vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return latest GPSTrackingDTO if found
   */
  public GPSTrackingDTO getLatestPosition(int vehicleId) {
    return gpsTrackingService.getLatestPositionByVehicleId(vehicleId);
  }

  /**
   * Log station arrival event
   * 
   * @param vehicleId  the vehicle ID
   * @param stationId  the station ID
   * @param latitude   the latitude
   * @param longitude  the longitude
   * @param operatorId the operator ID
   * @return true if successful
   */
  public boolean logStationArrival(int vehicleId, String stationId,
      BigDecimal latitude, BigDecimal longitude, Integer operatorId) {
    return gpsTrackingService.logStationArrival(vehicleId, stationId, latitude, longitude, operatorId);
  }

  /**
   * Log station departure event
   * 
   * @param vehicleId  the vehicle ID
   * @param stationId  the station ID
   * @param latitude   the latitude
   * @param longitude  the longitude
   * @param operatorId the operator ID
   * @return true if successful
   */
  public boolean logStationDeparture(int vehicleId, String stationId,
      BigDecimal latitude, BigDecimal longitude, Integer operatorId) {
    return gpsTrackingService.logStationDeparture(vehicleId, stationId, latitude, longitude, operatorId);
  }

  /**
   * Get station events for a vehicle
   * 
   * @param vehicleId the vehicle ID
   * @param stationId the station ID (null for all stations)
   * @return list of station events
   */
  public List<GPSTrackingDTO> getStationEvents(int vehicleId, String stationId) {
    return gpsTrackingService.getStationEvents(vehicleId, stationId);
  }

  /**
   * Generate arrival/departure report for a vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return formatted report string
   */
  public String generateArrivalDepartureReport(int vehicleId) {
    return gpsTrackingService.generateArrivalDepartureReport(vehicleId);
  }

  // ========== MAINTENANCE MANAGEMENT ==========

  /**
   * Schedule maintenance
   * 
   * @param maintenance the maintenance to schedule
   * @return true if successful
   */
  public boolean scheduleMaintenance(MaintenanceDTO maintenance) {
    return businessController.scheduleMaintenance(maintenance);
  }

  /**
   * Get maintenance records by vehicle ID
   * 
   * @param vehicleId the vehicle ID
   * @return list of maintenance records
   */
  public List<MaintenanceDTO> getMaintenanceByVehicleId(int vehicleId) {
    return businessController.getMaintenanceByVehicleId(vehicleId);
  }

  /**
   * Check if vehicle needs maintenance using current strategy
   * 
   * @param vehicleId the vehicle ID
   * @return true if maintenance is due
   */
  public boolean isMaintenanceDue(int vehicleId) {
    return businessController.isMaintenanceDue(vehicleId);
  }

  /**
   * Set maintenance strategy
   * 
   * @param strategyType the strategy type ("time", "usage", "predictive")
   * @return true if strategy was set successfully
   */
  public boolean setMaintenanceStrategy(String strategyType) {
    return businessController.setMaintenanceStrategy(strategyType);
  }

  /**
   * Update maintenance record
   * 
   * @param maintenance the maintenance record to update
   * @return true if successful
   */
  public boolean updateMaintenance(MaintenanceDTO maintenance) {
    return businessController.updateMaintenance(maintenance);
  }

  /**
   * Delete maintenance record
   * 
   * @param maintenanceId the maintenance ID to delete
   * @return true if successful
   */
  public boolean deleteMaintenance(int maintenanceId) {
    return businessController.deleteMaintenance(maintenanceId);
  }

  /**
   * Update maintenance status
   * 
   * @param maintenanceId the maintenance ID
   * @param status        the new status
   * @return true if successful
   */
  public boolean updateMaintenanceStatus(int maintenanceId, String status) {
    return businessController.updateMaintenanceStatus(maintenanceId, status);
  }

  /**
   * Get all maintenance records from the database
   * 
   * @return List of all MaintenanceDTO objects
   */
  public List<MaintenanceDTO> getAllMaintenanceRecords() {
    return businessController.getAllMaintenanceRecords();
  }

  // ========== FUEL LOG MANAGEMENT ==========

  /**
   * Add fuel log entry
   * 
   * @param fuelLog the fuel log to add
   * @return true if successful
   */
  public boolean addFuelLog(FuelLogDTO fuelLog) {
    return businessController.addFuelLog(fuelLog);
  }

  /**
   * Get fuel logs by vehicle ID
   * 
   * @param vehicleId the vehicle ID
   * @return list of fuel logs
   */
  public List<FuelLogDTO> getFuelLogsByVehicleId(int vehicleId) {
    return businessController.getFuelLogsByVehicleId(vehicleId);
  }

  /**
   * Get total fuel cost for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return total fuel cost
   */
  public double getTotalFuelCost(int vehicleId) {
    return businessController.getTotalFuelCost(vehicleId);
  }

  /**
   * Get all fuel logs
   * 
   * @return list of all fuel logs
   */
  public List<FuelLogDTO> getAllFuelLogs() {
    return businessController.getAllFuelLogs();
  }

  /**
   * Get total fuel consumption for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return total fuel consumption
   */
  public double getTotalFuelConsumption(int vehicleId) {
    return businessController.getTotalFuelConsumption(vehicleId);
  }

  /**
   * Get average fuel consumption for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return average fuel consumption
   */
  public double getAverageFuelConsumption(int vehicleId) {
    return businessController.getAverageFuelConsumption(vehicleId);
  }

  /**
   * Get fuel log by ID
   * 
   * @param fuelLogId the fuel log ID
   * @return fuel log DTO if found, null otherwise
   */
  public FuelLogDTO getFuelLogById(int fuelLogId) {
    return businessController.getFuelLogById(fuelLogId);
  }

  /**
   * Update fuel log
   * 
   * @param fuelLog the fuel log to update
   * @return true if successful
   */
  public boolean updateFuelLog(FuelLogDTO fuelLog) {
    return businessController.updateFuelLog(fuelLog);
  }

  /**
   * Delete fuel log
   * 
   * @param fuelLogId the fuel log ID to delete
   * @return true if successful
   */
  public boolean deleteFuelLog(int fuelLogId) {
    return businessController.deleteFuelLog(fuelLogId);
  }

  // ========== VEHICLE COMPONENT MANAGEMENT ==========

  /**
   * Add vehicle component
   * 
   * @param component the component to add
   * @return true if successful
   */
  public boolean addVehicleComponent(VehicleComponentDTO component) {
    return vehicleComponentService.addVehicleComponent(component);
  }

  /**
   * Get components by vehicle ID
   * 
   * @param vehicleId the vehicle ID
   * @return list of components
   */
  public List<VehicleComponentDTO> getComponentsByVehicleId(int vehicleId) {
    return vehicleComponentService.getComponentsByVehicleId(vehicleId);
  }

  /**
   * Update component condition
   * 
   * @param componentId the component ID
   * @param condition   the new condition
   * @return true if successful
   */
  public boolean updateComponentCondition(int componentId, String condition) {
    // Get the component first, update status, then save
    VehicleComponentDTO component = vehicleComponentService.getVehicleComponentById(componentId);
    if (component != null) {
      component.setStatus(condition);
      return vehicleComponentService.updateVehicleComponent(component);
    }
    return false;
  }

  // ========== ALERT MANAGEMENT ==========

  /**
   * Create and send alert using Observer pattern
   * 
   * @param vehicleId the vehicle ID
   * @param alertType the alert type
   * @param message   the alert message
   * @return true if successful
   */
  public boolean createAlert(int vehicleId, String alertType, String message) {
    return businessController.createAlert(vehicleId, alertType, message);
  }

  /**
   * Create a new alert
   * 
   * @param alert the alert to create
   * @return true if successful
   */
  public boolean createAlert(AlertDTO alert) {
    return businessController.createAlert(alert);
  }

  /**
   * Get all alerts
   * 
   * @return list of all alerts
   */
  public List<AlertDTO> getAllAlerts() {
    return businessController.getAllAlerts();
  }

  /**
   * Get alert by ID
   * 
   * @param alertId the alert ID
   * @return the alert or null if not found
   */
  public AlertDTO getAlertById(int alertId) {
    return businessController.getAlertById(alertId);
  }

  /**
   * Update alert
   * 
   * @param alert the alert to update
   * @return true if successful
   */
  public boolean updateAlert(AlertDTO alert) {
    return businessController.updateAlert(alert);
  }

  /**
   * Delete alert
   * 
   * @param alertId the alert ID
   * @return true if successful
   */
  public boolean deleteAlert(int alertId) {
    return businessController.deleteAlert(alertId);
  }

  /**
   * Resolve alert (set status to Resolved)
   * 
   * @param alertId the alert ID
   * @return true if successful
   */
  public boolean resolveAlert(int alertId) {
    return businessController.resolveAlert(alertId);
  }

  // ========== REPORTING & BUSINESS INTELLIGENCE ==========

  /**
   * Get comprehensive fleet summary statistics
   * 
   * @return map of statistics
   */
  public Map<String, Object> getFleetSummary() {
    return businessController.getFleetSummary();
  }

  /**
   * Get vehicles needing maintenance
   * 
   * @return list of vehicle IDs needing maintenance
   */
  public List<Integer> getVehiclesNeedingMaintenance() {
    return businessController.getVehiclesNeedingMaintenance();
  }

  /**
   * Generate fleet utilization report with default parameters
   * 
   * @return ReportDTO with fleet utilization data
   */
  public ReportDTO generateFleetUtilizationReport() {
    // Default to last 30 days and system user (ID 1)
    Timestamp endDate = new Timestamp(System.currentTimeMillis());
    Timestamp startDate = new Timestamp(endDate.getTime() - (30L * 24 * 60 * 60 * 1000)); // 30 days ago
    return reportService.generateFleetUtilizationReport(startDate, endDate, 1);
  }

  /**
   * Generate fleet utilization report with custom parameters
   * 
   * @param startDate   start date for the report period
   * @param endDate     end date for the report period
   * @param generatedBy user ID generating the report
   * @return ReportDTO with fleet utilization data
   */
  public ReportDTO generateFleetUtilizationReport(Timestamp startDate, Timestamp endDate, int generatedBy) {
    return reportService.generateFleetUtilizationReport(startDate, endDate, generatedBy);
  }

  /**
   * Generate fuel efficiency report with default parameters
   * 
   * @return ReportDTO with fuel efficiency data
   */
  public ReportDTO generateFuelEfficiencyReport() {
    // Default to last 30 days and system user (ID 1)
    Timestamp endDate = new Timestamp(System.currentTimeMillis());
    Timestamp startDate = new Timestamp(endDate.getTime() - (30L * 24 * 60 * 60 * 1000)); // 30 days ago
    return reportService.generateFuelEfficiencyReport(startDate, endDate, 1);
  }

  /**
   * Generate fuel efficiency report with custom parameters
   * 
   * @param startDate   start date for the report period
   * @param endDate     end date for the report period
   * @param generatedBy user ID generating the report
   * @return ReportDTO with fuel efficiency data
   */
  public ReportDTO generateFuelEfficiencyReport(Timestamp startDate, Timestamp endDate, int generatedBy) {
    return reportService.generateFuelEfficiencyReport(startDate, endDate, generatedBy);
  }

  /**
   * Generate maintenance cost report with default parameters
   * 
   * @return ReportDTO with maintenance cost analysis
   */
  public ReportDTO generateMaintenanceCostReport() {
    // Default to last 30 days and system user (ID 1)
    Timestamp endDate = new Timestamp(System.currentTimeMillis());
    Timestamp startDate = new Timestamp(endDate.getTime() - (30L * 24 * 60 * 60 * 1000)); // 30 days ago
    return reportService.generateMaintenanceCostReport(startDate, endDate, 1);
  }

  /**
   * Generate maintenance cost report with custom parameters
   * 
   * @param startDate   start date for the report period
   * @param endDate     end date for the report period
   * @param generatedBy user ID generating the report
   * @return ReportDTO with maintenance cost analysis
   */
  public ReportDTO generateMaintenanceCostReport(Timestamp startDate, Timestamp endDate, int generatedBy) {
    return reportService.generateMaintenanceCostReport(startDate, endDate, generatedBy);
  }

  /**
   * Generate comprehensive dashboard report with default parameters
   * 
   * @return ReportDTO with dashboard data
   */
  public ReportDTO generateDashboardReport() {
    // Default to last 30 days and system user (ID 1)
    Timestamp endDate = new Timestamp(System.currentTimeMillis());
    Timestamp startDate = new Timestamp(endDate.getTime() - (30L * 24 * 60 * 60 * 1000)); // 30 days ago
    return reportService.generateDashboardReport(startDate, endDate, 1);
  }

  /**
   * Generate comprehensive dashboard report with custom parameters
   * 
   * @param startDate   start date for the report period
   * @param endDate     end date for the report period
   * @param generatedBy user ID generating the report
   * @return ReportDTO with dashboard data
   */
  public ReportDTO generateDashboardReport(Timestamp startDate, Timestamp endDate, int generatedBy) {
    return reportService.generateDashboardReport(startDate, endDate, generatedBy);
  }

  /**
   * Get all reports from the database
   * 
   * @return List of all ReportDTO objects
   */
  public List<ReportDTO> getAllReports() {
    return businessController.getAllReports();
  }

  /**
   * Get a specific report by ID
   * 
   * @param reportId the report ID
   * @return ReportDTO object or null if not found
   */
  public ReportDTO getReportById(int reportId) {
    return businessController.getReportById(reportId);
  }

  /**
   * Get reports by type
   * 
   * @param reportType the type of reports to retrieve
   * @return List of ReportDTO objects of the specified type
   */
  public List<ReportDTO> getReportsByType(String reportType) {
    return businessController.getReportsByType(reportType);
  }

  // ========== UTILITY METHODS ==========

  /**
   * Check if tracking data exists for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return true if vehicle has tracking data
   */
  public boolean hasTrackingData(int vehicleId) {
    return gpsTrackingService.hasTrackingData(vehicleId);
  }

  /**
   * Get tracking data count for a vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return number of tracking entries
   */
  public int getTrackingDataCount(int vehicleId) {
    return gpsTrackingService.getTrackingDataCount(vehicleId);
  }

  /**
   * Delete old GPS tracking entries
   * 
   * @param daysOld delete entries older than this many days
   * @return number of entries deleted
   */
  public int deleteOldTrackingEntries(int daysOld) {
    return gpsTrackingService.deleteOldTrackingEntries(daysOld);
  }
}
