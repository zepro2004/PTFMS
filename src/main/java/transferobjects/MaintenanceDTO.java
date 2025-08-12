package transferobjects;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Data Transfer Object for Maintenance entity in the PTFMS system.
 * Represents maintenance records including scheduled maintenance,
 * repairs, inspections, and associated costs. Used to transfer
 * maintenance data between application layers and track vehicle
 * maintenance history and schedules.
 */
public class MaintenanceDTO {
  /** Unique maintenance identifier */
  private int maintenanceId;
  /** Vehicle ID */
  private int vehicleId;
  /** Service date */
  private Date serviceDate;
  /** Description of maintenance */
  private String description;
  /** Cost of maintenance */
  private BigDecimal cost;
  /** Maintenance status */
  private String status;
  /** When the maintenance record was created */
  private Timestamp createdAt;

  /**
   * Get the maintenance ID
   * 
   * @return the maintenance ID
   */
  public int getMaintenanceId() {
    return maintenanceId;
  }

  /**
   * Set the maintenance ID
   * 
   * @param maintenanceId the maintenance ID to set
   */
  public void setMaintenanceId(int maintenanceId) {
    this.maintenanceId = maintenanceId;
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
   * Get the service date
   * 
   * @return the service date
   */
  public Date getServiceDate() {
    return serviceDate;
  }

  /**
   * Set the service date
   * 
   * @param serviceDate the service date to set
   */
  public void setServiceDate(Date serviceDate) {
    this.serviceDate = serviceDate;
  }

  /**
   * Get the description
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set the description
   * 
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get the cost
   * 
   * @return the cost
   */
  public BigDecimal getCost() {
    return cost;
  }

  /**
   * Set the cost
   * 
   * @param cost the cost to set
   */
  public void setCost(BigDecimal cost) {
    this.cost = cost;
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
   * String representation of the maintenance record
   * 
   * @return formatted string with maintenance details
   */
  @Override
  public String toString() {
    return "Maintenance [ID=" + maintenanceId + ", VehicleID=" + vehicleId +
        ", ServiceDate=" + serviceDate + ", Description=" + description +
        ", Cost=" + cost + ", Status=" + status + "]";
  }
}
