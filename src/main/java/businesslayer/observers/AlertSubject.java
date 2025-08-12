package businesslayer.observers;

import java.util.ArrayList;
import java.util.List;

import transferobjects.AlertDTO;

/**
 * Subject class for alert notifications in the PTFMS system.
 * Manages alert observers and notifies them of system alerts using
 * the Observer pattern. Coordinates alert distribution across multiple
 * notification channels and maintains observer registration.
 */
public class AlertSubject {

  private final List<AlertObserver> observers;

  /**
   * Constructor
   */
  public AlertSubject() {
    this.observers = new ArrayList<>();
  }

  /**
   * Add an observer
   * 
   * @param observer the observer to add
   */
  public void addObserver(AlertObserver observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  /**
   * Remove an observer
   * 
   * @param observer the observer to remove
   */
  public void removeObserver(AlertObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notify all observers of an alert
   * 
   * @param alert the alert to notify about
   */
  public void notifyObservers(AlertDTO alert) {
    for (AlertObserver observer : observers) {
      observer.update(alert);
    }
  }

  /**
   * Get all registered observers
   * 
   * @return list of observers
   */
  public List<AlertObserver> getObservers() {
    return new ArrayList<>(observers);
  }

  /**
   * Get observer count
   * 
   * @return number of registered observers
   */
  public int getObserverCount() {
    return observers.size();
  }
}
