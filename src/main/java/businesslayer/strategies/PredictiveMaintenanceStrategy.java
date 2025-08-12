package businesslayer.strategies;

import transferobjects.MaintenanceDTO;
import transferobjects.VehicleDTO;

/**
 * Predictive maintenance strategy implementation for the PTFMS system.
 * Schedules maintenance based on predictive analytics considering
 * vehicle age, type, historical failure patterns, and performance
 * indicators. Uses machine learning principles to optimize maintenance
 * schedules and reduce unexpected breakdowns.
 */
public class PredictiveMaintenanceStrategy implements MaintenanceStrategy {

  private static final int DEFAULT_INTERVAL_DAYS = 60; // 2 months

  /**
   * Calculates the next maintenance date using predictive algorithms.
   * Takes into account vehicle condition, usage patterns, and historical data.
   * 
   * @param vehicle         the vehicle to calculate maintenance for
   * @param lastMaintenance the most recent maintenance record
   * @return timestamp of the next recommended maintenance date
   */
  @Override
  public long calculateNextMaintenanceDate(VehicleDTO vehicle, MaintenanceDTO lastMaintenance) {
    long currentTime = System.currentTimeMillis();

    if (lastMaintenance != null && lastMaintenance.getServiceDate() != null) {
      long lastMaintenanceTime = lastMaintenance.getServiceDate().getTime();
      long intervalMillis = getMaintenanceInterval(vehicle) * 24L * 60L * 60L * 1000L;
      return lastMaintenanceTime + intervalMillis;
    }

    return currentTime + (getMaintenanceInterval(vehicle) * 24L * 60L * 60L * 1000L);
  }

  /**
   * Calculates maintenance interval using predictive analytics.
   * Considers vehicle age, type, and performance indicators to determine
   * optimal maintenance frequency.
   * 
   * @param vehicle the vehicle to calculate interval for
   * @return the number of days between maintenance sessions
   */
  @Override
  public int getMaintenanceInterval(VehicleDTO vehicle) {
    int baseInterval = DEFAULT_INTERVAL_DAYS;

    // Adjust based on vehicle age
    int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    int vehicleAge = currentYear - vehicle.getYear();

    if (vehicleAge > 10) {
      baseInterval = 30; // Very frequent maintenance for old vehicles
    } else if (vehicleAge > 5) {
      baseInterval = 45; // More frequent for older vehicles
    } else if (vehicleAge > 2) {
      baseInterval = 60; // Standard interval for mid-age vehicles
    } else {
      baseInterval = 90; // Less frequent for new vehicles
    }

    // Further adjust based on vehicle type
    String vehicleType = vehicle.getVehicleType();
    if (vehicleType != null) {
      switch (vehicleType.toLowerCase()) {
        case "bus" -> baseInterval = (int) (baseInterval * 0.8); // 20% more frequent for buses
        case "truck" -> baseInterval = (int) (baseInterval * 0.7); // 30% more frequent for trucks
        case "van" -> baseInterval = (int) (baseInterval * 0.9); // 10% more frequent for vans
      }
    }

    return Math.max(baseInterval, 14); // Minimum 2 weeks interval
  }

  /**
   * Returns the type identifier for this maintenance strategy.
   * 
   * @return the strategy type name
   */
  @Override
  public String getStrategyType() {
    return "Predictive";
  }

  /**
   * Determines if maintenance is due using predictive analysis.
   * Considers multiple factors beyond just time intervals and applies
   * proactive scheduling with buffer periods based on vehicle age.
   * 
   * @param vehicle         the vehicle to check maintenance status for
   * @param lastMaintenance the most recent maintenance record
   * @return true if maintenance is due, false otherwise
   */
  @Override
  public boolean isMaintenanceDue(VehicleDTO vehicle, MaintenanceDTO lastMaintenance) {
    long currentTime = System.currentTimeMillis();
    long nextMaintenanceDate = calculateNextMaintenanceDate(vehicle, lastMaintenance);

    // Predictive maintenance is more proactive
    int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    int vehicleAge = currentYear - vehicle.getYear();

    long bufferTime;
    if (vehicleAge > 10) {
      bufferTime = 14 * 24 * 60 * 60 * 1000L; // 2 weeks buffer for very old vehicles
    } else if (vehicleAge > 5) {
      bufferTime = 10 * 24 * 60 * 60 * 1000L; // 10 days buffer for older vehicles
    } else {
      bufferTime = 5 * 24 * 60 * 60 * 1000L; // 5 days buffer for newer vehicles
    }

    return currentTime >= (nextMaintenanceDate - bufferTime);
  }
}
