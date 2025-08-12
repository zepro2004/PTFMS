package businesslayer.strategies;

import transferobjects.MaintenanceDTO;
import transferobjects.VehicleDTO;

/**
 * Usage-based maintenance strategy implementation for the PTFMS system.
 * Schedules maintenance based on vehicle usage patterns including
 * mileage, operating hours, and utilization rates. Optimizes maintenance
 * schedules based on actual vehicle wear and operational demands.
 */
public class UsageBasedMaintenanceStrategy implements MaintenanceStrategy {

  private static final int DEFAULT_INTERVAL_DAYS = 120; // 4 months for low usage

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

  @Override
  public int getMaintenanceInterval(VehicleDTO vehicle) {
    // Adjust interval based on vehicle status (usage indicator)
    String status = vehicle.getStatus();
    if (status != null) {
      return switch (status.toLowerCase()) {
        case "active", "in-service" -> 30;
        case "maintenance" -> 180;
        case "available" -> 90;
        default -> DEFAULT_INTERVAL_DAYS;
      };
    }
    return DEFAULT_INTERVAL_DAYS;
  }

  @Override
  public String getStrategyType() {
    return "Usage-Based";
  }

  @Override
  public boolean isMaintenanceDue(VehicleDTO vehicle, MaintenanceDTO lastMaintenance) {
    long currentTime = System.currentTimeMillis();
    long nextMaintenanceDate = calculateNextMaintenanceDate(vehicle, lastMaintenance);

    // More aggressive scheduling for high-usage vehicles
    String status = vehicle.getStatus();
    long bufferTime;
    if ("active".equalsIgnoreCase(status) || "in-service".equalsIgnoreCase(status)) {
      bufferTime = 3 * 24 * 60 * 60 * 1000L; // 3 days buffer for active vehicles
    } else {
      bufferTime = 7 * 24 * 60 * 60 * 1000L; // 7 days buffer for others
    }

    return currentTime >= (nextMaintenanceDate - bufferTime);
  }
}
