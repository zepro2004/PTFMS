package businesslayer.commands;

import dataaccesslayer.MaintenanceDAO;
import transferobjects.MaintenanceDTO;

/**
 * Command implementation for scheduling maintenance operations in the PTFMS
 * system.
 * Encapsulates maintenance scheduling as a command object, providing execution
 * control and rollback capabilities for maintenance planning operations.
 * Implements the Command pattern to support undoable maintenance scheduling.
 */
public class ScheduleMaintenanceCommand implements Command {
  private final MaintenanceDAO maintenanceDAO;
  private final MaintenanceDTO maintenance;
  private int maintenanceId;

  /**
   * Constructor
   * 
   * @param maintenance the maintenance to schedule
   */
  public ScheduleMaintenanceCommand(MaintenanceDTO maintenance) {
    this.maintenanceDAO = new MaintenanceDAO();
    this.maintenance = maintenance;
  }

  /**
   * Execute the schedule maintenance command
   * 
   * @return true if successful
   */
  @Override
  public boolean execute() {
    boolean result = maintenanceDAO.addMaintenance(maintenance);
    if (result) {
      maintenanceId = maintenance.getMaintenanceId();
    }
    return result;
  }

  /**
   * Undo the schedule maintenance command
   * 
   * @return true if successful
   */
  @Override
  public boolean undo() {
    if (maintenanceId > 0) {
      return maintenanceDAO.deleteMaintenance(maintenanceId);
    }
    return false;
  }
}
