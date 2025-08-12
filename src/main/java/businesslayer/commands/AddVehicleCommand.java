package businesslayer.commands;

import dataaccesslayer.VehicleDAO;
import transferobjects.VehicleDTO;

/**
 * Command implementation for adding vehicles to the PTFMS system.
 * Encapsulates the vehicle addition operation as a command object, providing
 * support for execution and rollback of vehicle registration operations.
 * Implements the Command pattern to enable deferred execution and undo
 * capabilities.
 */
public class AddVehicleCommand implements Command {
  private final VehicleDAO vehicleDAO;
  private final VehicleDTO vehicle;
  private int vehicleId;

  /**
   * Constructor
   * 
   * @param vehicle the vehicle to add
   */
  public AddVehicleCommand(VehicleDTO vehicle) {
    this.vehicleDAO = new VehicleDAO();
    this.vehicle = vehicle;
  }

  /**
   * Execute the add vehicle command
   * 
   * @return true if successful
   */
  @Override
  public boolean execute() {
    boolean result = vehicleDAO.addVehicle(vehicle);
    if (result) {
      vehicleId = vehicle.getVehicleId();
    }
    return result;
  }

  /**
   * Undo the add vehicle command
   * 
   * @return true if successful
   */
  @Override
  public boolean undo() {
    if (vehicleId > 0) {
      return vehicleDAO.deleteVehicle(vehicleId);
    }
    return false;
  }
}
