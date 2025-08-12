package businesslayer.services;

import java.sql.Date;
import java.util.List;

import dataaccesslayer.FuelLogDAO;
import transferobjects.FuelLogDTO;

/**
 * Business service for FuelLog operations in the PTFMS system.
 * Provides comprehensive fuel log management functionality including
 * fuel consumption tracking, cost analysis, efficiency calculations,
 * and reporting. Coordinates between presentation and data layers
 * for all fuel-related business operations.
 */
public class FuelLogService {
  private final FuelLogDAO fuelLogDAO;

  /**
   * Initializes the FuelLogService with a new FuelLogDAO instance.
   */
  public FuelLogService() {
    this.fuelLogDAO = new FuelLogDAO();
  }

  /**
   * Add new fuel log entry
   * 
   * @param fuelLog the fuel log to add
   * @return true if successful
   */
  public boolean addFuelLog(FuelLogDTO fuelLog) {
    // Validate required fields
    if (fuelLog.getVehicleId() <= 0) {
      return false;
    }
    if (fuelLog.getLogDate() == null) {
      return false;
    }
    if (fuelLog.getAmount() == null || fuelLog.getAmount().doubleValue() <= 0) {
      return false;
    }
    if (fuelLog.getCost() == null || fuelLog.getCost().doubleValue() < 0) {
      return false;
    }

    return fuelLogDAO.addFuelLog(fuelLog);
  }

  /**
   * Get fuel log by ID
   * 
   * @param fuelLogId the fuel log ID
   * @return FuelLogDTO if found, null otherwise
   */
  public FuelLogDTO getFuelLogById(int fuelLogId) {
    return fuelLogDAO.findById(fuelLogId);
  }

  /**
   * Get all fuel logs
   * 
   * @return list of all fuel logs
   */
  public List<FuelLogDTO> getAllFuelLogs() {
    return fuelLogDAO.getAllFuelLogs();
  }

  /**
   * Get fuel logs by vehicle ID
   * 
   * @param vehicleId the vehicle ID
   * @return list of fuel logs for the vehicle
   */
  public List<FuelLogDTO> getFuelLogsByVehicleId(int vehicleId) {
    return fuelLogDAO.getFuelLogsByVehicleId(vehicleId);
  }

  /**
   * Update fuel log information
   * 
   * @param fuelLog the updated fuel log information
   * @return true if successful
   */
  public boolean updateFuelLog(FuelLogDTO fuelLog) {
    return fuelLogDAO.updateFuelLog(fuelLog);
  }

  /**
   * Delete fuel log
   * 
   * @param fuelLogId the fuel log ID
   * @return true if successful
   */
  public boolean deleteFuelLog(int fuelLogId) {
    return fuelLogDAO.deleteFuelLog(fuelLogId);
  }

  /**
   * Get fuel logs within date range
   * 
   * @param startDate the start date
   * @param endDate   the end date
   * @return list of fuel logs within the date range
   */
  public List<FuelLogDTO> getFuelLogsByDateRange(Date startDate, Date endDate) {
    return getAllFuelLogs().stream()
        .filter(fuelLog -> {
          Date fuelDate = fuelLog.getLogDate();
          return fuelDate != null &&
              !fuelDate.before(startDate) &&
              !fuelDate.after(endDate);
        })
        .collect(java.util.stream.Collectors.toList());
  }

  /**
   * Get fuel logs for current month
   * 
   * @return list of fuel logs for current month
   */
  public List<FuelLogDTO> getCurrentMonthFuelLogs() {
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
    Date monthStart = new Date(cal.getTimeInMillis());

    cal.add(java.util.Calendar.MONTH, 1);
    cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
    Date monthEnd = new Date(cal.getTimeInMillis());

    return getFuelLogsByDateRange(monthStart, monthEnd);
  }

  /**
   * Calculate total fuel consumption for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return total fuel consumption
   */
  public double getTotalFuelConsumption(int vehicleId) {
    return getFuelLogsByVehicleId(vehicleId).stream()
        .mapToDouble(fuelLog -> fuelLog.getAmount().doubleValue())
        .sum();
  }

  /**
   * Calculate total fuel cost for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return total fuel cost
   */
  public double getTotalFuelCost(int vehicleId) {
    return getFuelLogsByVehicleId(vehicleId).stream()
        .mapToDouble(fuelLog -> fuelLog.getCost().doubleValue())
        .sum();
  }

  /**
   * Calculate total fuel consumption for date range
   * 
   * @param startDate the start date
   * @param endDate   the end date
   * @return total fuel consumption
   */
  public double getTotalFuelConsumptionByDateRange(Date startDate, Date endDate) {
    return getFuelLogsByDateRange(startDate, endDate).stream()
        .mapToDouble(fuelLog -> fuelLog.getAmount().doubleValue())
        .sum();
  }

  /**
   * Calculate total fuel cost for date range
   * 
   * @param startDate the start date
   * @param endDate   the end date
   * @return total fuel cost
   */
  public double getTotalFuelCostByDateRange(Date startDate, Date endDate) {
    return getFuelLogsByDateRange(startDate, endDate).stream()
        .mapToDouble(fuelLog -> fuelLog.getCost().doubleValue())
        .sum();
  }

  /**
   * Get average fuel consumption per refuel for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return average fuel consumption per refuel
   */
  public double getAverageFuelConsumption(int vehicleId) {
    List<FuelLogDTO> fuelLogs = getFuelLogsByVehicleId(vehicleId);
    if (fuelLogs.isEmpty()) {
      return 0.0;
    }

    double total = fuelLogs.stream()
        .mapToDouble(fuelLog -> fuelLog.getAmount().doubleValue())
        .sum();

    return total / fuelLogs.size();
  }

  /**
   * Get average fuel cost per liter for vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return average fuel cost per liter
   */
  public double getAverageFuelCostPerLiter(int vehicleId) {
    List<FuelLogDTO> fuelLogs = getFuelLogsByVehicleId(vehicleId);

    double totalCost = fuelLogs.stream()
        .mapToDouble(fuelLog -> fuelLog.getCost().doubleValue())
        .sum();

    double totalQuantity = fuelLogs.stream()
        .mapToDouble(fuelLog -> fuelLog.getAmount().doubleValue())
        .sum();

    return totalQuantity > 0 ? totalCost / totalQuantity : 0.0;
  }

  /**
   * Get fuel efficiency for vehicle (if mileage is tracked)
   * Note: This method assumes mileage/odometer reading is stored in notes or
   * similar field
   * 
   * @param vehicleId the vehicle ID
   * @return fuel efficiency metrics
   */
  public double getFuelEfficiency(int vehicleId) {
    // This is a placeholder implementation
    // In a real system, you'd need odometer readings to calculate efficiency
    return getAverageFuelConsumption(vehicleId);
  }
}
