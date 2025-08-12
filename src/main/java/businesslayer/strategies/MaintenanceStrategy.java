package businesslayer.strategies;

import transferobjects.MaintenanceDTO;
import transferobjects.VehicleDTO;

/**
 * Strategy interface for different maintenance approaches in the PTFMS system.
 * Implements Strategy pattern for flexible maintenance scheduling supporting
 * various maintenance methodologies including time-based, usage-based,
 * and predictive maintenance strategies.
 */
public interface MaintenanceStrategy {

  /**
   * Calculate the next maintenance date based on strategy
   * 
   * @param vehicle         the vehicle
   * @param lastMaintenance the last maintenance record
   * @return next maintenance date as timestamp
   */
  long calculateNextMaintenanceDate(VehicleDTO vehicle, MaintenanceDTO lastMaintenance);

  /**
   * Get the maintenance interval for this strategy
   * 
   * @param vehicle the vehicle
   * @return interval in days
   */
  int getMaintenanceInterval(VehicleDTO vehicle);

  /**
   * Get the strategy type name
   * 
   * @return strategy name
   */
  String getStrategyType();

  /**
   * Check if maintenance is due based on this strategy
   * 
   * @param vehicle         the vehicle
   * @param lastMaintenance the last maintenance record
   * @return true if maintenance is due
   */
  boolean isMaintenanceDue(VehicleDTO vehicle, MaintenanceDTO lastMaintenance);
}
