package businesslayer.observers;

import transferobjects.AlertDTO;

/**
 * SMS alert observer implementation for the PTFMS system.
 * Handles SMS text message notifications for system alerts implementing
 * the Observer pattern. Provides immediate mobile notification capabilities
 * for urgent alerts requiring prompt attention from fleet managers.
 */
public class SMSAlertObserver implements AlertObserver {

  private String phoneNumber;

  /**
   * Constructor
   * 
   * @param phoneNumber the phone number to send SMS to
   */
  public SMSAlertObserver(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public void update(AlertDTO alert) {
    System.out.println("SMS ALERT: Sending SMS to " + phoneNumber);
    System.out.println("Alert: " + alert.getAlertType() + " - " + alert.getMessage());
  }

  @Override
  public String getObserverType() {
    return "SMS";
  }

  /**
   * Get the phone number
   * 
   * @return the phone number
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Set the phone number
   * 
   * @param phoneNumber the phone number to set
   */
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
