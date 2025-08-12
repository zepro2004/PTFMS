package businesslayer;

import businesslayer.services.AlertService;
import businesslayer.services.FuelLogService;
import businesslayer.services.GPSTrackingService;
import businesslayer.services.MaintenanceService;
import businesslayer.services.ReportService;
import businesslayer.services.UserService;
import businesslayer.services.VehicleComponentService;
import businesslayer.services.VehicleService;

/**
 * Simple Factory pattern implementation for creating business services in the
 * PTFMS system.
 * Centralizes service creation and provides a single point of access for all
 * business
 * layer services. Implements singleton pattern to ensure consistent service
 * management
 * and reduces object creation overhead.
 */
public class ServiceFactory {

  private static ServiceFactory instance;

  // Singleton pattern for factory
  private ServiceFactory() {
  }

  /**
   * Get the single instance of ServiceFactory
   * 
   * @return the ServiceFactory instance
   */
  public static ServiceFactory getInstance() {
    if (instance == null) {
      instance = new ServiceFactory();
    }
    return instance;
  }

  /**
   * Create a service based on service type
   * 
   * @param serviceType the type of service to create
   * @return the requested service
   */
  public Object createService(String serviceType) {
    switch (serviceType.toLowerCase()) {
      case "user" -> {
        return new UserService();
      }
      case "vehicle" -> {
        return new VehicleService();
      }
      case "maintenance" -> {
        return new MaintenanceService();
      }
      case "fuellog" -> {
        return new FuelLogService();
      }
      case "gps" -> {
        return new GPSTrackingService();
      }
      case "alert" -> {
        return new AlertService();
      }
      case "component" -> {
        return new VehicleComponentService();
      }
      case "report" -> {
        return new ReportService();
      }
      default -> throw new IllegalArgumentException("Unknown service type: " + serviceType);
    }
  }

  /**
   * Create UserService
   * 
   * @return new UserService instance
   */
  public UserService createUserService() {
    return new UserService();
  }

  /**
   * Create VehicleService
   * 
   * @return new VehicleService instance
   */
  public VehicleService createVehicleService() {
    return new VehicleService();
  }

  /**
   * Create MaintenanceService
   * 
   * @return new MaintenanceService instance
   */
  public MaintenanceService createMaintenanceService() {
    return new MaintenanceService();
  }

  /**
   * Create FuelLogService
   * 
   * @return new FuelLogService instance
   */
  public FuelLogService createFuelLogService() {
    return new FuelLogService();
  }

  /**
   * Create GPSTrackingService
   * 
   * @return new GPSTrackingService instance
   */
  public GPSTrackingService createGPSTrackingService() {
    return new GPSTrackingService();
  }

  /**
   * Create AlertService
   * 
   * @return new AlertService instance
   */
  public AlertService createAlertService() {
    return new AlertService();
  }

  /**
   * Create VehicleComponentService
   * 
   * @return new VehicleComponentService instance
   */
  public VehicleComponentService createVehicleComponentService() {
    return new VehicleComponentService();
  }

  /**
   * Create ReportService
   * 
   * @return new ReportService instance
   */
  public ReportService createReportService() {
    return new ReportService();
  }
}
