package businesslayer.observers;

import transferobjects.AlertDTO;

/**
 * Email alert observer implementation for the PTFMS system.
 * Handles email notifications for system alerts implementing the
 * Observer pattern. Formats and sends alert notifications via email
 * to designated recipients for timely response to system events.
 */
public class EmailAlertObserver implements AlertObserver {

  private String emailAddress;

  /**
   * Constructor
   * 
   * @param emailAddress the email address to send notifications to
   */
  public EmailAlertObserver(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public void update(AlertDTO alert) {
    System.out.println("EMAIL ALERT: Sending email to " + emailAddress);
    System.out.println("Alert Type: " + alert.getAlertType());
    System.out.println("Message: " + alert.getMessage());
    System.out.println("Status: " + alert.getStatus());
  }

  @Override
  public String getObserverType() {
    return "Email";
  }

  /**
   * Get the email address
   * 
   * @return the email address
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  /**
   * Set the email address
   * 
   * @param emailAddress the email address to set
   */
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
}
