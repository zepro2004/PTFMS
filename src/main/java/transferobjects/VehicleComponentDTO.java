package transferobjects;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Vehicle Components entity in the PTFMS system.
 * Represents vehicle parts and components including specifications,
 * condition, replacement history, and cost information. Used to
 * transfer component data between application layers for inventory
 * management and maintenance planning.
 */
public class VehicleComponentDTO {
  /** Unique component identifier */
  private int componentId;
  /** Vehicle ID */
  private int vehicleId;
  /** Component name */
  private String componentName;
  /** Usage hours */
  private BigDecimal usageHours;
  /** Maximum hours threshold */
  private BigDecimal maxHours;
  /** Component status */
  private String status;

  /**
   * Get the component ID
   * 
   * @return the component ID
   */
  public int getComponentId() {
    return componentId;
  }

  /**
   * Set the component ID
   * 
   * @param componentId the component ID to set
   */
  public void setComponentId(int componentId) {
    this.componentId = componentId;
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
   * Get the component name
   * 
   * @return the component name
   */
  public String getComponentName() {
    return componentName;
  }

  /**
   * Set the component name
   * 
   * @param componentName the component name to set
   */
  public void setComponentName(String componentName) {
    this.componentName = componentName;
  }

  /**
   * Get the usage hours
   * 
   * @return the usage hours
   */
  public BigDecimal getUsageHours() {
    return usageHours;
  }

  /**
   * Set the usage hours
   * 
   * @param usageHours the usage hours to set
   */
  public void setUsageHours(BigDecimal usageHours) {
    this.usageHours = usageHours;
  }

  /**
   * Get the maximum hours
   * 
   * @return the maximum hours
   */
  public BigDecimal getMaxHours() {
    return maxHours;
  }

  /**
   * Set the maximum hours
   * 
   * @param maxHours the maximum hours to set
   */
  public void setMaxHours(BigDecimal maxHours) {
    this.maxHours = maxHours;
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
   * String representation of the vehicle component record
   * 
   * @return formatted string with vehicle component details
   */
  @Override
  public String toString() {
    return "VehicleComponent [ID=" + componentId + ", VehicleID=" + vehicleId +
        ", Name=" + componentName + ", UsageHours=" + usageHours +
        ", MaxHours=" + maxHours + ", Status=" + status + "]";
  }
}
