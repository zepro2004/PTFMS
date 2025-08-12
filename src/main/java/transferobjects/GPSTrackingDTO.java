package transferobjects;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Data Transfer Object for GPS Tracking entity in the PTFMS system.
 * Represents GPS location data including coordinates, timestamps,
 * speed, route information, and station events. Used to transfer
 * real-time and historical location data between application layers
 * for tracking and monitoring purposes.
 */
public class GPSTrackingDTO {
  /** Unique tracking identifier */
  private int trackingId;
  /** Vehicle ID */
  private int vehicleId;
  /** Latitude coordinate */
  private BigDecimal latitude;
  /** Longitude coordinate */
  private BigDecimal longitude;
  /** Timestamp of the GPS reading */
  private Timestamp timestamp;
  /** Operator ID */
  private Integer operatorId;
  /** Station ID for transit station events */
  private String stationId;
  /** Event type: ARRIVAL, DEPARTURE, or LOCATION */
  private String eventType;
  /** Speed in km/h */
  private BigDecimal speed;
  /** Additional notes or status */
  private String notes;

  /**
   * Get the tracking ID
   * 
   * @return the tracking ID
   */
  public int getTrackingId() {
    return trackingId;
  }

  /**
   * Set the tracking ID
   * 
   * @param trackingId the tracking ID to set
   */
  public void setTrackingId(int trackingId) {
    this.trackingId = trackingId;
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
   * Get the latitude
   * 
   * @return the latitude
   */
  public BigDecimal getLatitude() {
    return latitude;
  }

  /**
   * Set the latitude
   * 
   * @param latitude the latitude to set
   */
  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  /**
   * Get the longitude
   * 
   * @return the longitude
   */
  public BigDecimal getLongitude() {
    return longitude;
  }

  /**
   * Set the longitude
   * 
   * @param longitude the longitude to set
   */
  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  /**
   * Get the timestamp
   * 
   * @return the timestamp
   */
  public Timestamp getTimestamp() {
    return timestamp;
  }

  /**
   * Set the timestamp
   * 
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
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
   * Get the station ID
   * 
   * @return the station ID
   */
  public String getStationId() {
    return stationId;
  }

  /**
   * Set the station ID
   * 
   * @param stationId the station ID to set
   */
  public void setStationId(String stationId) {
    this.stationId = stationId;
  }

  /**
   * Get the event type
   * 
   * @return the event type
   */
  public String getEventType() {
    return eventType;
  }

  /**
   * Set the event type
   * 
   * @param eventType the event type to set (ARRIVAL, DEPARTURE, LOCATION)
   */
  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  /**
   * Get the speed
   * 
   * @return the speed in km/h
   */
  public BigDecimal getSpeed() {
    return speed;
  }

  /**
   * Set the speed
   * 
   * @param speed the speed in km/h to set
   */
  public void setSpeed(BigDecimal speed) {
    this.speed = speed;
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
   * String representation of the GPS tracking record
   * 
   * @return formatted string with GPS tracking details
   */
  @Override
  public String toString() {
    return "GPSTracking [ID=" + trackingId + ", VehicleID=" + vehicleId +
        ", Latitude=" + latitude + ", Longitude=" + longitude +
        ", Timestamp=" + timestamp + ", OperatorID=" + operatorId +
        ", StationID=" + stationId + ", EventType=" + eventType +
        ", Speed=" + speed + "]";
  }
}
