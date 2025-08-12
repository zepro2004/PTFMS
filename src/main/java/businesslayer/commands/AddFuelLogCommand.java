package businesslayer.commands;

import dataaccesslayer.FuelLogDAO;
import transferobjects.FuelLogDTO;

/**
 * Command implementation for adding fuel logs to the PTFMS system.
 * Encapsulates fuel log creation operations as command objects, enabling
 * execution tracking and potential rollback of fuel consumption entries.
 * Supports the Command pattern for undoable fuel log management operations.
 */
public class AddFuelLogCommand implements Command {
  private final FuelLogDAO fuelLogDAO;
  private final FuelLogDTO fuelLog;
  private int fuelLogId;

  /**
   * Constructor
   * 
   * @param fuelLog the fuel log to add
   */
  public AddFuelLogCommand(FuelLogDTO fuelLog) {
    this.fuelLogDAO = new FuelLogDAO();
    this.fuelLog = fuelLog;
  }

  /**
   * Execute the add fuel log command
   * 
   * @return true if successful
   */
  @Override
  public boolean execute() {
    boolean result = fuelLogDAO.addFuelLog(fuelLog);
    if (result) {
      fuelLogId = fuelLog.getFuelLogId();
    }
    return result;
  }

  /**
   * Undo the add fuel log command
   * 
   * @return true if successful
   */
  @Override
  public boolean undo() {
    if (fuelLogId > 0) {
      return fuelLogDAO.deleteFuelLog(fuelLogId);
    }
    return false;
  }
}
