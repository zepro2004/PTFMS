package businesslayer;

import java.math.BigDecimal;

import transferobjects.VehicleDTO;

/**
 * Builder pattern implementation for creating VehicleDTO objects in the PTFMS
 * system.
 * Provides a fluent interface for constructing vehicle objects with various
 * properties in a step-by-step manner. Ensures proper vehicle creation with
 * validation and default values where appropriate.
 */
public class VehicleBuilder {
  private VehicleDTO vehicle;

  /**
   * Initializes a new VehicleBuilder with a fresh VehicleDTO instance.
   */
  public VehicleBuilder() {
    this.vehicle = new VehicleDTO();
  }

  /**
   * Set VIN
   * 
   * @param vin the VIN
   * @return this builder
   */
  public VehicleBuilder setVin(String vin) {
    this.vehicle.setVin(vin);
    return this;
  }

  /**
   * Set vehicle number
   * 
   * @param vehicleNumber the vehicle number
   * @return this builder
   */
  public VehicleBuilder setVehicleNumber(String vehicleNumber) {
    this.vehicle.setVehicleNumber(vehicleNumber);
    return this;
  }

  /**
   * Set vehicle type
   * 
   * @param vehicleType the vehicle type
   * @return this builder
   */
  public VehicleBuilder setVehicleType(String vehicleType) {
    this.vehicle.setVehicleType(vehicleType);
    return this;
  }

  /**
   * Set make
   * 
   * @param make the make
   * @return this builder
   */
  public VehicleBuilder setMake(String make) {
    this.vehicle.setMake(make);
    return this;
  }

  /**
   * Set model
   * 
   * @param model the model
   * @return this builder
   */
  public VehicleBuilder setModel(String model) {
    this.vehicle.setModel(model);
    return this;
  }

  /**
   * Set year
   * 
   * @param year the year
   * @return this builder
   */
  public VehicleBuilder setYear(int year) {
    this.vehicle.setYear(year);
    return this;
  }

  /**
   * Set fuel type
   * 
   * @param fuelType the fuel type
   * @return this builder
   */
  public VehicleBuilder setFuelType(String fuelType) {
    this.vehicle.setFuelType(fuelType);
    return this;
  }

  /**
   * Set consumption rate
   * 
   * @param consumptionRate the consumption rate
   * @return this builder
   */
  public VehicleBuilder setConsumptionRate(BigDecimal consumptionRate) {
    this.vehicle.setConsumptionRate(consumptionRate);
    return this;
  }

  /**
   * Set maximum passengers
   * 
   * @param maxPassengers the maximum passengers
   * @return this builder
   */
  public VehicleBuilder setMaxPassengers(int maxPassengers) {
    this.vehicle.setMaxPassengers(maxPassengers);
    return this;
  }

  /**
   * Set current route
   * 
   * @param currentRoute the current route
   * @return this builder
   */
  public VehicleBuilder setCurrentRoute(String currentRoute) {
    this.vehicle.setCurrentRoute(currentRoute);
    return this;
  }

  /**
   * Set status
   * 
   * @param status the status
   * @return this builder
   */
  public VehicleBuilder setStatus(String status) {
    this.vehicle.setStatus(status);
    return this;
  }

  /**
   * Build the vehicle
   * 
   * @return the completed VehicleDTO
   */
  public VehicleDTO build() {
    return this.vehicle;
  }

  /**
   * Reset the builder
   * 
   * @return this builder
   */
  public VehicleBuilder reset() {
    this.vehicle = new VehicleDTO();
    return this;
  }
}
