package businesslayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import businesslayer.observers.AlertObserver;
import businesslayer.observers.AlertSubject;
import businesslayer.observers.EmailAlertObserver;
import businesslayer.observers.SMSAlertObserver;
import businesslayer.services.AlertService;
import businesslayer.services.FuelLogService;
import businesslayer.services.MaintenanceService;
import businesslayer.services.ReportService;
import businesslayer.services.UserService;
import businesslayer.services.VehicleService;
import businesslayer.strategies.MaintenanceStrategy;
import businesslayer.strategies.PredictiveMaintenanceStrategy;
import businesslayer.strategies.TimeBasedMaintenanceStrategy;
import businesslayer.strategies.UsageBasedMaintenanceStrategy;
import transferobjects.AlertDTO;
import transferobjects.FuelLogDTO;
import transferobjects.MaintenanceDTO;
import transferobjects.ReportDTO;
import transferobjects.UserDTO;
import transferobjects.VehicleDTO;

/**
 * Central Controller for the Business Layer in the PTFMS system.
 * Implements 3-tier architecture coordination between Presentation, Business,
 * and Data layers. Integrates multiple design patterns including DAO, Builder,
 * Simple Factory, Strategy, Observer, and Command patterns.
 * 
 * This controller serves as the main entry point for business logic operations
 * and coordinates between various services to provide comprehensive fleet
 * management functionality.
 */
public class PTFMSBusinessController {

  // Service Factory for Simple Factory pattern
  private final ServiceFactory serviceFactory;

  // Services
  private final UserService userService;
  private final VehicleService vehicleService;
  private final MaintenanceService maintenanceService;
  private final FuelLogService fuelLogService;
  private final AlertService alertService;
  private final ReportService reportService;

  // Strategy pattern for maintenance
  private final Map<String, MaintenanceStrategy> maintenanceStrategies;
  private MaintenanceStrategy currentMaintenanceStrategy;

  // Observer pattern for alerts
  private final AlertSubject alertSubject;

  /**
   * Constructor - Initialize all components
   */
  public PTFMSBusinessController() {
    // Initialize Service Factory
    this.serviceFactory = ServiceFactory.getInstance();

    // Initialize Services using Factory
    this.userService = serviceFactory.createUserService();
    this.vehicleService = serviceFactory.createVehicleService();
    this.maintenanceService = serviceFactory.createMaintenanceService();
    this.fuelLogService = serviceFactory.createFuelLogService();
    this.alertService = serviceFactory.createAlertService();
    this.reportService = serviceFactory.createReportService();

    // Initialize Strategy pattern
    this.maintenanceStrategies = new HashMap<>();
    this.maintenanceStrategies.put("time", new TimeBasedMaintenanceStrategy());
    this.maintenanceStrategies.put("usage", new UsageBasedMaintenanceStrategy());
    this.maintenanceStrategies.put("predictive", new PredictiveMaintenanceStrategy());
    this.currentMaintenanceStrategy = maintenanceStrategies.get("time"); // Default strategy

    // Initialize Observer pattern
    this.alertSubject = new AlertSubject();
    setupDefaultObservers();
  }

  /**
   * Sets up default alert observers for the notification system.
   * Configures email and SMS observers to handle system alerts.
   * In a production environment, observer configuration would be loaded from
   * settings.
   */
  private void setupDefaultObservers() {
    // Add default observers (in real implementation, these would be configured)
    alertSubject.addObserver(new EmailAlertObserver("manager@ptfms.com"));
    alertSubject.addObserver(new SMSAlertObserver("+1234567890"));
  }

  // ========== USER MANAGEMENT ==========

  /**
   * Authenticate user
   * 
   * @param username the username
   * @param password the password
   * @return UserDTO if successful, null otherwise
   */
  public UserDTO authenticateUser(String username, String password) {
    return userService.authenticateUser(username, password);
  }

  /**
   * Register new user
   * 
   * @param username the username
   * @param password the password
   * @param email    the email
   * @param name     the full name
   * @param role     the user role
   * @return true if successful
   */
  public boolean registerUser(String username, String password, String email, String name, String role) {
    // Use the 5-parameter version from the services UserService
    return userService.registerUser(username, password, email, name, role);
  }

  /**
   * Get user by ID
   * 
   * @param userId the user ID
   * @return UserDTO if found
   */
  public UserDTO getUserById(int userId) {
    return userService.getUserById(userId);
  }

  /**
   * Get all users
   * 
   * @return list of all users
   */
  public List<UserDTO> getAllUsers() {
    return userService.getAllUsers();
  }

  // ========== VEHICLE MANAGEMENT ==========

  /**
   * Add new vehicle using Builder pattern
   * 
   * @param vin           the VIN
   * @param vehicleNumber the vehicle number
   * @param vehicleType   the vehicle type
   * @param make          the make
   * @param model         the model
   * @param year          the year
   * @return true if successful
   */
  public boolean addVehicle(String vin, String vehicleNumber, String vehicleType,
      String make, String model, int year) {
    // Use Builder pattern
    VehicleBuilder builder = vehicleService.createVehicleBuilder();

    // Set default fuel type based on vehicle type
    String fuelType = determineFuelType(vehicleType);

    VehicleDTO vehicle = builder
        .setVin(vin)
        .setVehicleNumber(vehicleNumber)
        .setVehicleType(vehicleType)
        .setMake(make)
        .setModel(model)
        .setYear(year)
        .setFuelType(fuelType)
        .setStatus("Available")
        .build();

    // Use Command pattern
    businesslayer.commands.AddVehicleCommand command = new businesslayer.commands.AddVehicleCommand(vehicle);
    return command.execute();
  }

  /**
   * Determines the appropriate fuel type based on the vehicle type.
   * Maps different vehicle categories to their corresponding fuel/energy sources.
   * Used during vehicle creation to set default fuel type values.
   * 
   * @param vehicleType the type of vehicle (bus, light rail, etc.)
   * @return the appropriate fuel type (Diesel, Electric, CNG, Gasoline)
   */
  private String determineFuelType(String vehicleType) {
    if (vehicleType == null) {
      return "Diesel"; // Default
    }

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

  /**
   * Get vehicle by ID
   * 
   * @param vehicleId the vehicle ID
   * @return VehicleDTO if found
   */
  public VehicleDTO getVehicleById(int vehicleId) {
    return vehicleService.getVehicleById(vehicleId);
  }

  /**
   * Get all vehicles
   * 
   * @return list of all vehicles
   */
  public List<VehicleDTO> getAllVehicles() {
    return vehicleService.getAllVehicles();
  }

  /**
   * Update vehicle status
   * 
   * @param vehicleId the vehicle ID
   * @param status    the new status
   * @return true if successful
   */
  public boolean updateVehicleStatus(int vehicleId, String status) {
    return vehicleService.updateVehicleStatus(vehicleId, status);
  }

  /**
   * Update vehicle details
   * 
   * @param vehicle the vehicle DTO with updated information
   * @return true if successful
   */
  public boolean updateVehicle(VehicleDTO vehicle) {
    return vehicleService.updateVehicle(vehicle);
  }

  // ========== MAINTENANCE MANAGEMENT ==========

  /**
   * Schedule maintenance using Command pattern
   * 
   * @param maintenance the maintenance to schedule
   * @return true if successful
   */
  public boolean scheduleMaintenance(MaintenanceDTO maintenance) {
    businesslayer.commands.ScheduleMaintenanceCommand command = new businesslayer.commands.ScheduleMaintenanceCommand(
        maintenance);
    return command.execute();
  }

  /**
   * Get maintenance for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return list of maintenance records
   */
  public List<MaintenanceDTO> getMaintenanceByVehicleId(int vehicleId) {
    return maintenanceService.getMaintenanceByVehicleId(vehicleId);
  }

  /**
   * Check if vehicle needs maintenance using Strategy pattern
   * 
   * @param vehicleId the vehicle ID
   * @return true if maintenance is due
   */
  public boolean isMaintenanceDue(int vehicleId) {
    VehicleDTO vehicle = vehicleService.getVehicleById(vehicleId);
    if (vehicle == null) {
      return false;
    }

    List<MaintenanceDTO> maintenanceHistory = maintenanceService.getMaintenanceByVehicleId(vehicleId);
    MaintenanceDTO lastMaintenance = maintenanceHistory.isEmpty() ? null
        : maintenanceHistory.get(maintenanceHistory.size() - 1);

    return currentMaintenanceStrategy.isMaintenanceDue(vehicle, lastMaintenance);
  }

  /**
   * Set maintenance strategy
   * 
   * @param strategyType the strategy type ("time", "usage", "predictive")
   * @return true if strategy was set
   */
  public boolean setMaintenanceStrategy(String strategyType) {
    MaintenanceStrategy strategy = maintenanceStrategies.get(strategyType.toLowerCase());
    if (strategy != null) {
      this.currentMaintenanceStrategy = strategy;
      return true;
    }
    return false;
  }

  /**
   * Update maintenance record
   * 
   * @param maintenance the maintenance record to update
   * @return true if successful
   */
  public boolean updateMaintenance(MaintenanceDTO maintenance) {
    return maintenanceService.updateMaintenance(maintenance);
  }

  /**
   * Delete maintenance record
   * 
   * @param maintenanceId the maintenance ID to delete
   * @return true if successful
   */
  public boolean deleteMaintenance(int maintenanceId) {
    return maintenanceService.deleteMaintenance(maintenanceId);
  }

  /**
   * Update maintenance status
   * 
   * @param maintenanceId the maintenance ID
   * @param status        the new status
   * @return true if successful
   */
  public boolean updateMaintenanceStatus(int maintenanceId, String status) {
    return maintenanceService.updateMaintenanceStatus(maintenanceId, status);
  }

  // ========== FUEL LOG MANAGEMENT ==========

  /**
   * Add fuel log using Command pattern
   * 
   * @param fuelLog the fuel log to add
   * @return true if successful
   */
  public boolean addFuelLog(FuelLogDTO fuelLog) {
    businesslayer.commands.AddFuelLogCommand command = new businesslayer.commands.AddFuelLogCommand(fuelLog);
    return command.execute();
  }

  /**
   * Get fuel logs by vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return list of fuel logs
   */
  public List<FuelLogDTO> getFuelLogsByVehicleId(int vehicleId) {
    return fuelLogService.getFuelLogsByVehicleId(vehicleId);
  }

  /**
   * Get total fuel cost for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return total fuel cost
   */
  public double getTotalFuelCost(int vehicleId) {
    return fuelLogService.getTotalFuelCost(vehicleId);
  }

  /**
   * Get all fuel logs
   * 
   * @return list of all fuel logs
   */
  public List<FuelLogDTO> getAllFuelLogs() {
    return fuelLogService.getAllFuelLogs();
  }

  /**
   * Get total fuel consumption for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return total fuel consumption
   */
  public double getTotalFuelConsumption(int vehicleId) {
    return fuelLogService.getTotalFuelConsumption(vehicleId);
  }

  /**
   * Get average fuel consumption for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return average fuel consumption
   */
  public double getAverageFuelConsumption(int vehicleId) {
    return fuelLogService.getAverageFuelConsumption(vehicleId);
  }

  /**
   * Get fuel log by ID
   * 
   * @param fuelLogId the fuel log ID
   * @return fuel log DTO if found, null otherwise
   */
  public FuelLogDTO getFuelLogById(int fuelLogId) {
    return fuelLogService.getFuelLogById(fuelLogId);
  }

  /**
   * Update fuel log
   * 
   * @param fuelLog the fuel log to update
   * @return true if successful
   */
  public boolean updateFuelLog(FuelLogDTO fuelLog) {
    return fuelLogService.updateFuelLog(fuelLog);
  }

  /**
   * Delete fuel log
   * 
   * @param fuelLogId the fuel log ID to delete
   * @return true if successful
   */
  public boolean deleteFuelLog(int fuelLogId) {
    return fuelLogService.deleteFuelLog(fuelLogId);
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
    AlertDTO alert = new AlertDTO();
    alert.setVehicleId(vehicleId);
    alert.setAlertType(alertType);
    alert.setMessage(message);
    alert.setStatus("Open");
    alert.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

    boolean result = alertService.addAlert(alert);
    if (result) {
      // Notify observers
      alertSubject.notifyObservers(alert);
    }
    return result;
  }

  /**
   * Create a new alert using AlertDTO
   * 
   * @param alert the alert to create
   * @return true if successful
   */
  public boolean createAlert(AlertDTO alert) {
    // Ensure required fields are set
    if (alert.getStatus() == null || alert.getStatus().isEmpty()) {
      alert.setStatus("Open");
    }
    if (alert.getCreatedAt() == null) {
      alert.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
    }

    boolean result = alertService.addAlert(alert);
    if (result) {
      // Notify observers
      alertSubject.notifyObservers(alert);
    }
    return result;
  }

  /**
   * Add alert observer
   * 
   * @param observer the observer to add
   */
  public void addAlertObserver(AlertObserver observer) {
    alertSubject.addObserver(observer);
  }

  /**
   * Remove alert observer
   * 
   * @param observer the observer to remove
   */
  public void removeAlertObserver(AlertObserver observer) {
    alertSubject.removeObserver(observer);
  }

  /**
   * Get all alerts
   * 
   * @return list of all alerts
   */
  public List<AlertDTO> getAllAlerts() {
    return alertService.getAllAlerts();
  }

  /**
   * Get alert by ID
   * 
   * @param alertId the alert ID
   * @return the alert or null if not found
   */
  public AlertDTO getAlertById(int alertId) {
    return alertService.getAlertById(alertId);
  }

  /**
   * Update alert
   * 
   * @param alert the alert to update
   * @return true if successful
   */
  public boolean updateAlert(AlertDTO alert) {
    return alertService.updateAlert(alert);
  }

  /**
   * Delete alert
   * 
   * @param alertId the alert ID
   * @return true if successful
   */
  public boolean deleteAlert(int alertId) {
    return alertService.deleteAlert(alertId);
  }

  /**
   * Resolve alert (set status to Resolved)
   * 
   * @param alertId the alert ID
   * @return true if successful
   */
  public boolean resolveAlert(int alertId) {
    AlertDTO alert = alertService.getAlertById(alertId);
    if (alert != null) {
      alert.setStatus("Resolved");
      return alertService.updateAlert(alert);
    }
    return false;
  }

  // ========== BUSINESS INTELLIGENCE ==========

  /**
   * Get fleet summary statistics
   * 
   * @return map of statistics
   */
  public Map<String, Object> getFleetSummary() {
    Map<String, Object> summary = new HashMap<>();

    List<VehicleDTO> allVehicles = vehicleService.getAllVehicles();
    summary.put("totalVehicles", allVehicles.size());
    summary.put("activeVehicles", vehicleService.getVehicleCountByStatus("Active"));
    summary.put("maintenanceVehicles", vehicleService.getVehicleCountByStatus("Maintenance"));
    summary.put("availableVehicles", vehicleService.getVehicleCountByStatus("Available"));

    summary.put("currentMaintenanceStrategy", currentMaintenanceStrategy.getStrategyType());
    summary.put("alertObservers", alertSubject.getObserverCount());

    return summary;
  }

  /**
   * Get vehicles needing maintenance
   * 
   * @return list of vehicle IDs needing maintenance
   */
  public List<Integer> getVehiclesNeedingMaintenance() {
    return getAllVehicles().stream()
        .filter(vehicle -> isMaintenanceDue(vehicle.getVehicleId()))
        .map(VehicleDTO::getVehicleId)
        .collect(java.util.stream.Collectors.toList());
  }

  // ========== REPORT MANAGEMENT ==========

  /**
   * Get all reports from the database
   * 
   * @return List of all ReportDTO objects
   */
  public List<ReportDTO> getAllReports() {
    return reportService.getAllReports();
  }

  /**
   * Get a specific report by ID
   * 
   * @param reportId the report ID
   * @return ReportDTO object or null if not found
   */
  public ReportDTO getReportById(int reportId) {
    // Since ReportService doesn't have getReportById, filter from getAllReports
    List<ReportDTO> allReports = reportService.getAllReports();
    return allReports.stream()
        .filter(report -> report.getReportId() == reportId)
        .findFirst()
        .orElse(null);
  }

  /**
   * Get reports by type
   * 
   * @param reportType the type of reports to retrieve
   * @return List of ReportDTO objects of the specified type
   */
  public List<ReportDTO> getReportsByType(String reportType) {
    return reportService.getReportsByType(reportType);
  }

  /**
   * Get all maintenance records from the database
   * 
   * @return List of all MaintenanceDTO objects
   */
  public List<MaintenanceDTO> getAllMaintenanceRecords() {
    return maintenanceService.getAllMaintenance();
  }
}
