package transferobjects;

import java.sql.Timestamp;

/**
 * Data Transfer Object for Alert entity in the PTFMS system.
 * Represents system alerts related to vehicles, maintenance, or operational
 * issues. Provides properties for alert tracking, categorization, and status
 * management throughout the alert lifecycle.
 */
public class AlertDTO {
  /** Unique alert identifier */
  private int alertId;
  /** Vehicle ID */
  private int vehicleId;
  /** Alert type */
  private String alertType;
  /** Alert message */
  private String message;
  /** When the alert was created */
  private Timestamp createdAt;
  /** Alert status */
  private String status;

  /**
   * Get the alert ID
   * 
   * @return the alert ID
   */
  public int getAlertId() {
    return alertId;
  }

  /**
   * Set the alert ID
   * 
   * @param alertId the alert ID to set
   */
  public void setAlertId(int alertId) {
    this.alertId = alertId;
  }

  /**
   * Get the vehicle ID
   * 
   * @return the vehicle ID
   */
  public int getVehicleId() {
    return vehicleId;
  }

  /**
   * Set the vehicle ID
   * 
   * @param vehicleId the vehicle ID to set
   */
  public void setVehicleId(int vehicleId) {
    this.vehicleId = vehicleId;
  }

  /**
   * Get the alert type
   * 
   * @return the alert type
   */
  public String getAlertType() {
    return alertType;
  }

  /**
   * Set the alert type
   * 
   * @param alertType the alert type to set
   */
  public void setAlertType(String alertType) {
    this.alertType = alertType;
  }

  /**
   * Get the message
   * 
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Set the message
   * 
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Get the created timestamp
   * 
   * @return the created timestamp
   */
  public Timestamp getCreatedAt() {
    return createdAt;
  }

  /**
   * Set the created timestamp
   * 
   * @param createdAt the created timestamp to set
   */
  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Get the status
   * 
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Set the status
   * 
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * String representation of the alert record
   * 
   * @return formatted string with alert details
   */
  @Override
  public String toString() {
    return "Alert [ID=" + alertId + ", VehicleID=" + vehicleId +
        ", Type=" + alertType + ", Message=" + message +
        ", CreatedAt=" + createdAt + ", Status=" + status + "]";
  }
}
