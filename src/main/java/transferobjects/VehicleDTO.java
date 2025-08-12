package transferobjects;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Data Transfer Object for Vehicle entity in the PTFMS system.
 * Represents a vehicle with all its properties and provides
 * getter and setter methods for data transfer between layers.
 */
public class VehicleDTO {
  /** Unique vehicle identifier */
  private int vehicleId;
  /** Vehicle Identification Number */
  private String vin;
  /** Vehicle number */
  private String vehicleNumber;
  /** Vehicle type */
  private String vehicleType;
  /** Vehicle make */
  private String make;
  /** Vehicle model */
  private String model;
  /** Vehicle year */
  private int year;
  /** Fuel type */
  private String fuelType;
  /** Consumption rate (L/100km or kWh/km) */
  private BigDecimal consumptionRate;
  /** Maximum passengers */
  private int maxPassengers;
  /** Current route */
  private String currentRoute;
  /** Vehicle status */
  private String status;
  /** When the vehicle was created */
  private Timestamp createdAt;

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
   * Get the VIN
   * 
   * @return the VIN
   */
  public String getVin() {
    return vin;
  }

  /**
   * Set the VIN
   * 
   * @param vin the VIN to set
   */
  public void setVin(String vin) {
    this.vin = vin;
  }

  /**
   * Get the vehicle number
   * 
   * @return the vehicle number
   */
  public String getVehicleNumber() {
    return vehicleNumber;
  }

  /**
   * Set the vehicle number
   * 
   * @param vehicleNumber the vehicle number to set
   */
  public void setVehicleNumber(String vehicleNumber) {
    this.vehicleNumber = vehicleNumber;
  }

  /**
   * Get the vehicle type
   * 
   * @return the vehicle type
   */
  public String getVehicleType() {
    return vehicleType;
  }

  /**
   * Set the vehicle type
   * 
   * @param vehicleType the vehicle type to set
   */
  public void setVehicleType(String vehicleType) {
    this.vehicleType = vehicleType;
  }

  /**
   * Get the make
   * 
   * @return the make
   */
  public String getMake() {
    return make;
  }

  /**
   * Set the make
   * 
   * @param make the make to set
   */
  public void setMake(String make) {
    this.make = make;
  }

  /**
   * Get the model
   * 
   * @return the model
   */
  public String getModel() {
    return model;
  }

  /**
   * Set the model
   * 
   * @param model the model to set
   */
  public void setModel(String model) {
    this.model = model;
  }

  /**
   * Get the year
   * 
   * @return the year
   */
  public int getYear() {
    return year;
  }

  /**
   * Set the year
   * 
   * @param year the year to set
   */
  public void setYear(int year) {
    this.year = year;
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
   * Get the consumption rate
   * 
   * @return the consumption rate
   */
  public BigDecimal getConsumptionRate() {
    return consumptionRate;
  }

  /**
   * Set the consumption rate
   * 
   * @param consumptionRate the consumption rate to set
   */
  public void setConsumptionRate(BigDecimal consumptionRate) {
    this.consumptionRate = consumptionRate;
  }

  /**
   * Get the maximum passengers
   * 
   * @return the maximum passengers
   */
  public int getMaxPassengers() {
    return maxPassengers;
  }

  /**
   * Set the maximum passengers
   * 
   * @param maxPassengers the maximum passengers to set
   */
  public void setMaxPassengers(int maxPassengers) {
    this.maxPassengers = maxPassengers;
  }

  /**
   * Get the current route
   * 
   * @return the current route
   */
  public String getCurrentRoute() {
    return currentRoute;
  }

  /**
   * Set the current route
   * 
   * @param currentRoute the current route to set
   */
  public void setCurrentRoute(String currentRoute) {
    this.currentRoute = currentRoute;
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
   * String representation of the vehicle
   * 
   * @return formatted string with vehicle details
   */
  @Override
  public String toString() {
    return "Vehicle [ID=" + vehicleId + ", VIN=" + vin + ", Number=" + vehicleNumber +
        ", Type=" + vehicleType + ", Make=" + make + ", Model=" + model +
        ", Year=" + year + ", Status=" + status + "]";
  }
}
