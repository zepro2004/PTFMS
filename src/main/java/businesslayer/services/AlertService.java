package businesslayer.services;

import java.util.List;

import dataaccesslayer.AlertDAO;
import transferobjects.AlertDTO;

/**
 * Business service for Alert operations in the PTFMS system.
 * Manages alert creation, retrieval, and resolution processes.
 * Coordinates with the AlertDAO for data persistence and implements
 * business logic for alert management and notification workflows.
 */
public class AlertService {
  private final AlertDAO alertDAO;

  /**
   * Initializes the AlertService with a new AlertDAO instance.
   */
  public AlertService() {
    this.alertDAO = new AlertDAO();
  }

  /**
   * Adds a new alert to the system.
   * 
   * @param alert the AlertDTO object containing alert information
   * @return true if the alert was successfully added, false otherwise
   */
  public boolean addAlert(AlertDTO alert) {
    return alertDAO.addAlert(alert);
  }

  public AlertDTO getAlertById(int alertId) {
    return alertDAO.findById(alertId);
  }

  public List<AlertDTO> getAllAlerts() {
    return alertDAO.getAllAlerts();
  }

  public boolean updateAlert(AlertDTO alert) {
    return alertDAO.updateAlert(alert);
  }

  public boolean deleteAlert(int alertId) {
    return alertDAO.deleteAlert(alertId);
  }
}
