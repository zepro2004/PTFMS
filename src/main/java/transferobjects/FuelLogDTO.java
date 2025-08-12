package transferobjects;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Data Transfer Object for Fuel Log entity in the PTFMS system.
 * Represents fuel consumption records for vehicles including amount,
 * cost, distance traveled, and fuel efficiency metrics. Used to
 * transfer fuel log data between layers of the application.
 */
public class FuelLogDTO {
  /** Unique fuel log identifier */
  private int fuelLogId;
  /** Vehicle ID */
  private int vehicleId;
  /** Log date */
  private Date logDate;
  /** Fuel type */
  private String fuelType;
  /** Amount of fuel (Liters or kWh) */
  private BigDecimal amount;
  /** Cost of fuel */
  private BigDecimal cost;
  /** Distance traveled (km) */
  private BigDecimal distance;
  /** Operator ID */
  private Integer operatorId;
  /** Additional notes */
  private String notes;

  /**
   * Get the fuel log ID
   * 
   * @return the fuel log ID
   */
  public int getFuelLogId() {
    return fuelLogId;
  }

  /**
   * Set the fuel log ID
   * 
   * @param fuelLogId the fuel log ID to set
   */
  public void setFuelLogId(int fuelLogId) {
    this.fuelLogId = fuelLogId;
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
   * Get the log date
   * 
   * @return the log date
   */
  public Date getLogDate() {
    return logDate;
  }

  /**
   * Set the log date
   * 
   * @param logDate the log date to set
   */
  public void setLogDate(Date logDate) {
    this.logDate = logDate;
  }

  /**
   * Get the fuel type
   * 
   * @return the fuel type
   */
  public String getFuelType() {
    return fuelType;
  }

  /**
   * Set the fuel type
   * 
   * @param fuelType the fuel type to set
   */
  public void setFuelType(String fuelType) {
    this.fuelType = fuelType;
  }

  /**
   * Get the amount
   * 
   * @return the amount
   */
  public BigDecimal getAmount() {
    return amount;
  }

  /**
   * Set the amount
   * 
   * @param amount the amount to set
   */
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  /**
   * Get the distance
   * 
   * @return the distance
   */
  public BigDecimal getDistance() {
    return distance;
  }

  /**
   * Set the distance
   * 
   * @param distance the distance to set
   */
  public void setDistance(BigDecimal distance) {
    this.distance = distance;
  }

  /**
   * Get the operator ID
   * 
   * @return the operator ID
   */
  public Integer getOperatorId() {
    return operatorId;
  }

  /**
   * Set the operator ID
   * 
   * @param operatorId the operator ID to set
   */
  public void setOperatorId(Integer operatorId) {
    this.operatorId = operatorId;
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
   * Get the notes
   * 
   * @return the notes
   */
  public String getNotes() {
    return notes;
  }

  /**
   * Set the notes
   * 
   * @param notes the notes to set
   */
  public void setNotes(String notes) {
    this.notes = notes;
  }

  /**
   * String representation of the fuel log
   * 
   * @return formatted string with fuel log details
   */
  @Override
  public String toString() {
    return "FuelLog [ID=" + fuelLogId + ", VehicleID=" + vehicleId +
        ", Date=" + logDate + ", FuelType=" + fuelType +
        ", Amount=" + amount + ", Cost=" + cost + ", Distance=" + distance +
        ", OperatorID=" + operatorId + ", Notes=" + notes + "]";
  }
}
