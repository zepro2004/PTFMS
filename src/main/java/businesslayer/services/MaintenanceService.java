package businesslayer.services;

import java.sql.Date;
import java.util.List;

import dataaccesslayer.MaintenanceDAO;
import transferobjects.MaintenanceDTO;

/**
 * Business service for Maintenance operations in the PTFMS system.
 * Provides comprehensive maintenance management functionality including
 * scheduling, tracking, cost management, and maintenance strategy
 * implementation. Supports predictive, time-based, and usage-based
 * maintenance approaches.
 */
public class MaintenanceService {
  private final MaintenanceDAO maintenanceDAO;

  /**
   * Initializes the MaintenanceService with a new MaintenanceDAO instance.
   */
  public MaintenanceService() {
    this.maintenanceDAO = new MaintenanceDAO();
  }

  /**
   * Schedule new maintenance
   * 
   * @param maintenance the maintenance to schedule
   * @return true if successful
   */
  public boolean scheduleMaintenance(MaintenanceDTO maintenance) {
    // Validate required fields
    if (maintenance.getVehicleId() <= 0) {
      return false;
    }
    if (maintenance.getServiceDate() == null) {
      return false;
    }
    if (maintenance.getDescription() == null || maintenance.getDescription().trim().isEmpty()) {
      return false;
    }

    // Set default status if not provided
    if (maintenance.getStatus() == null || maintenance.getStatus().trim().isEmpty()) {
      maintenance.setStatus("Scheduled");
    }

    return maintenanceDAO.addMaintenance(maintenance);
  }

  /**
   * Get maintenance by ID
   * 
   * @param maintenanceId the maintenance ID
   * @return MaintenanceDTO if found, null otherwise
   */
  public MaintenanceDTO getMaintenanceById(int maintenanceId) {
    return maintenanceDAO.findById(maintenanceId);
  }

  /**
   * Get all maintenance records
   * 
   * @return list of all maintenance records
   */
  public List<MaintenanceDTO> getAllMaintenance() {
    return maintenanceDAO.getAllMaintenance();
  }

  /**
   * Get maintenance records by vehicle ID
   * 
   * @param vehicleId the vehicle ID
   * @return list of maintenance records for the vehicle
   */
  public List<MaintenanceDTO> getMaintenanceByVehicleId(int vehicleId) {
    return maintenanceDAO.getMaintenanceByVehicleId(vehicleId);
  }

  /**
   * Get maintenance records by status
   * 
   * @param status the maintenance status
   * @return list of maintenance records with the specified status
   */
  public List<MaintenanceDTO> getMaintenanceByStatus(String status) {
    return maintenanceDAO.getMaintenanceByStatus(status);
  }

  /**
   * Update maintenance information
   * 
   * @param maintenance the updated maintenance information
   * @return true if successful
   */
  public boolean updateMaintenance(MaintenanceDTO maintenance) {
    return maintenanceDAO.updateMaintenance(maintenance);
  }

  /**
   * Update maintenance status
   * 
   * @param maintenanceId the maintenance ID
   * @param status        the new status
   * @return true if successful
   */
  public boolean updateMaintenanceStatus(int maintenanceId, String status) {
    MaintenanceDTO maintenance = maintenanceDAO.findById(maintenanceId);
    if (maintenance != null) {
      maintenance.setStatus(status);
      return maintenanceDAO.updateMaintenance(maintenance);
    }
    return false;
  }

  /**
   * Complete maintenance
   * 
   * @param maintenanceId the maintenance ID
   * @return true if successful
   */
  public boolean completeMaintenance(int maintenanceId) {
    return updateMaintenanceStatus(maintenanceId, "Completed");
  }

  /**
   * Cancel maintenance
   * 
   * @param maintenanceId the maintenance ID
   * @return true if successful
   */
  public boolean cancelMaintenance(int maintenanceId) {
    return updateMaintenanceStatus(maintenanceId, "Cancelled");
  }

  /**
   * Delete maintenance record
   * 
   * @param maintenanceId the maintenance ID
   * @return true if successful
   */
  public boolean deleteMaintenance(int maintenanceId) {
    return maintenanceDAO.deleteMaintenance(maintenanceId);
  }

  /**
   * Get scheduled maintenance (not completed or cancelled)
   * 
   * @return list of scheduled maintenance records
   */
  public List<MaintenanceDTO> getScheduledMaintenance() {
    return getAllMaintenance().stream()
        .filter(maintenance -> !"Completed".equalsIgnoreCase(maintenance.getStatus()) &&
            !"Cancelled".equalsIgnoreCase(maintenance.getStatus()))
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Get completed maintenance
   * 
   * @return list of completed maintenance records
   */
  public List<MaintenanceDTO> getCompletedMaintenance() {
    return maintenanceDAO.getMaintenanceByStatus("Completed");
  }

  /**
   * Get maintenance due within specified days
   * 
   * @param days number of days to look ahead
   * @return list of maintenance records due within the specified days
   */
  public List<MaintenanceDTO> getMaintenanceDueWithinDays(int days) {
    Date currentDate = new Date(System.currentTimeMillis());
    Date targetDate = new Date(System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L));

    return getAllMaintenance().stream()
        .filter(maintenance -> {
          Date serviceDate = maintenance.getServiceDate();
          return serviceDate != null &&
              !serviceDate.before(currentDate) &&
              !serviceDate.after(targetDate) &&
              !"Completed".equalsIgnoreCase(maintenance.getStatus()) &&
              !"Cancelled".equalsIgnoreCase(maintenance.getStatus());
        })
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Get overdue maintenance
   * 
   * @return list of overdue maintenance records
   */
  public List<MaintenanceDTO> getOverdueMaintenance() {
    Date currentDate = new Date(System.currentTimeMillis());

    return getAllMaintenance().stream()
        .filter(maintenance -> {
          Date serviceDate = maintenance.getServiceDate();
          return serviceDate != null &&
              serviceDate.before(currentDate) &&
              !"Completed".equalsIgnoreCase(maintenance.getStatus()) &&
              !"Cancelled".equalsIgnoreCase(maintenance.getStatus());
        })
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Get maintenance count by status
   * 
   * @param status the status to count
   * @return number of maintenance records with the specified status
   */
  public long getMaintenanceCountByStatus(String status) {
    return maintenanceDAO.getMaintenanceByStatus(status).size();
  }

  /**
   * Get maintenance count by vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return number of maintenance records for the vehicle
   */
  public long getMaintenanceCountByVehicle(int vehicleId) {
    return maintenanceDAO.getMaintenanceByVehicleId(vehicleId).size();
  }
}
