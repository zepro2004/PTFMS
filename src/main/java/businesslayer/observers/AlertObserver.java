package businesslayer.observers;

import transferobjects.AlertDTO;

/**
 * Observer interface for alert notifications in the PTFMS system.
 * Implements Observer pattern for flexible alert handling supporting
 * multiple notification channels including email, SMS, and system
 * notifications. Enables decoupled alert processing and distribution.
 */
public interface AlertObserver {

  /**
   * Update method called when an alert is triggered
   * 
   * @param alert the alert that was triggered
   */
  void update(AlertDTO alert);

  /**
   * Get the observer type/name
   * 
   * @return observer type
   */
  String getObserverType();
}
