package businesslayer.strategies;

import transferobjects.MaintenanceDTO;
import transferobjects.VehicleDTO;

/**
 * Time-based maintenance strategy implementation for the PTFMS system.
 * Schedules maintenance based on fixed time intervals regardless of
 * vehicle usage patterns. Provides predictable maintenance schedules
 * suitable for regulatory compliance and routine upkeep.
 */
public class TimeBasedMaintenanceStrategy implements MaintenanceStrategy {

  private static final int DEFAULT_INTERVAL_DAYS = 90; // 3 months

  @Override
  public long calculateNextMaintenanceDate(VehicleDTO vehicle, MaintenanceDTO lastMaintenance) {
    long currentTime = System.currentTimeMillis();

    if (lastMaintenance != null && lastMaintenance.getServiceDate() != null) {
      long lastMaintenanceTime = lastMaintenance.getServiceDate().getTime();
      long intervalMillis = getMaintenanceInterval(vehicle) * 24L * 60L * 60L * 1000L;
      return lastMaintenanceTime + intervalMillis;
    }

    // If no previous maintenance, schedule for next interval from now
    return currentTime + (getMaintenanceInterval(vehicle) * 24L * 60L * 60L * 1000L);
  }

  /**
   * Determines the maintenance interval in days based on vehicle type.
   * Different vehicle types require different maintenance frequencies.
   * 
   * @param vehicle the vehicle to determine maintenance interval for
   * @return the number of days between maintenance sessions
   */
  @Override
  public int getMaintenanceInterval(VehicleDTO vehicle) {
    // Different intervals based on vehicle type
    String vehicleType = vehicle.getVehicleType();
    if (vehicleType != null) {
      return switch (vehicleType.toLowerCase()) {
        case "bus" -> 60;
        case "van" -> 90;
        case "truck" -> 45;
        default -> DEFAULT_INTERVAL_DAYS;
      };
    }
    return DEFAULT_INTERVAL_DAYS;
  }

  /**
   * Returns the type identifier for this maintenance strategy.
   * 
   * @return the strategy type name
   */
  @Override
  public String getStrategyType() {
    return "Time-Based";
  }

  /**
   * Determines if maintenance is currently due for the specified vehicle.
   * Compares the current time against the calculated next maintenance date.
   * 
   * @param vehicle         the vehicle to check maintenance status for
   * @param lastMaintenance the most recent maintenance record for the vehicle
   * @return true if maintenance is due, false otherwise
   */
  @Override
  public boolean isMaintenanceDue(VehicleDTO vehicle, MaintenanceDTO lastMaintenance) {
    long currentTime = System.currentTimeMillis();
    long nextMaintenanceDate = calculateNextMaintenanceDate(vehicle, lastMaintenance);

    // Consider maintenance due if we're within 7 days of the scheduled date
    long bufferTime = 7 * 24 * 60 * 60 * 1000L; // 7 days in milliseconds
    return currentTime >= (nextMaintenanceDate - bufferTime);
  }
}
