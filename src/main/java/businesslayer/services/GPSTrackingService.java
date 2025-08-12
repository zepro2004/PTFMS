package businesslayer.services;

import java.util.List;

import dataaccesslayer.GPSTrackingDAO;
import transferobjects.GPSTrackingDTO;

/**
 * Business service for GPS Tracking operations in the PTFMS system.
 * Manages real-time vehicle location tracking, route monitoring,
 * geofencing capabilities, and location history. Provides business
 * logic for GPS data processing and location-based services.
 */
public class GPSTrackingService {
  private final GPSTrackingDAO gpsTrackingDAO;

  /**
   * Initializes the GPSTrackingService with a new GPSTrackingDAO instance.
   */
  public GPSTrackingService() {
    this.gpsTrackingDAO = new GPSTrackingDAO();
  }

  /**
   * Add a new GPS tracking entry
   * 
   * @param gpsTracking the GPS tracking entry to add
   * @return true if successful
   */
  public boolean addGPSTracking(GPSTrackingDTO gpsTracking) {
    return gpsTrackingDAO.addGPSTracking(gpsTracking);
  }

  /**
   * Get GPS tracking entry by ID
   * 
   * @param trackingId the tracking ID
   * @return GPSTrackingDTO if found, null otherwise
   */
  public GPSTrackingDTO getGPSTrackingById(int trackingId) {
    return gpsTrackingDAO.findById(trackingId);
  }

  /**
   * Get GPS tracking entries by vehicle ID
   * 
   * @param vehicleId the vehicle ID
   * @return list of GPS tracking entries for the vehicle
   */
  public List<GPSTrackingDTO> getGPSTrackingByVehicleId(int vehicleId) {
    return gpsTrackingDAO.getTrackingByVehicleId(vehicleId);
  }

  /**
   * Get the latest GPS position for a vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return latest GPSTrackingDTO if found, null otherwise
   */
  public GPSTrackingDTO getLatestPositionByVehicleId(int vehicleId) {
    return gpsTrackingDAO.getLatestPositionByVehicleId(vehicleId);
  }

  /**
   * Delete a GPS tracking entry
   * 
   * @param trackingId the tracking ID
   * @return true if successful
   */
  public boolean deleteGPSTracking(int trackingId) {
    return gpsTrackingDAO.deleteGPSTracking(trackingId);
  }

  /**
   * Delete old GPS tracking entries
   * 
   * @param daysOld delete entries older than this many days
   * @return number of entries deleted
   */
  public int deleteOldTrackingEntries(int daysOld) {
    return gpsTrackingDAO.deleteOldTrackingEntries(daysOld);
  }

  /**
   * Check if a vehicle has any GPS tracking data
   * 
   * @param vehicleId the vehicle ID
   * @return true if vehicle has tracking data
   */
  public boolean hasTrackingData(int vehicleId) {
    List<GPSTrackingDTO> trackingData = getGPSTrackingByVehicleId(vehicleId);
    return trackingData != null && !trackingData.isEmpty();
  }

  /**
   * Get tracking data count for a vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return number of tracking entries for the vehicle
   */
  public int getTrackingDataCount(int vehicleId) {
    List<GPSTrackingDTO> trackingData = getGPSTrackingByVehicleId(vehicleId);
    return trackingData != null ? trackingData.size() : 0;
  }

  /**
   * Get station arrival/departure events for a vehicle
   * 
   * @param vehicleId the vehicle ID
   * @param stationId the station ID (optional, null for all stations)
   * @return list of station events for the vehicle
   */
  public List<GPSTrackingDTO> getStationEvents(int vehicleId, String stationId) {
    return gpsTrackingDAO.getStationEvents(vehicleId, stationId);
  }

  /**
   * Log a station arrival event
   * 
   * @param vehicleId  the vehicle ID
   * @param stationId  the station ID
   * @param latitude   the latitude coordinate
   * @param longitude  the longitude coordinate
   * @param operatorId the operator ID (optional)
   * @return true if successful
   */
  public boolean logStationArrival(int vehicleId, String stationId,
      java.math.BigDecimal latitude, java.math.BigDecimal longitude, Integer operatorId) {
    return gpsTrackingDAO.logStationEvent(vehicleId, stationId, "ARRIVAL", latitude, longitude, operatorId);
  }

  /**
   * Log a station departure event
   * 
   * @param vehicleId  the vehicle ID
   * @param stationId  the station ID
   * @param latitude   the latitude coordinate
   * @param longitude  the longitude coordinate
   * @param operatorId the operator ID (optional)
   * @return true if successful
   */
  public boolean logStationDeparture(int vehicleId, String stationId,
      java.math.BigDecimal latitude, java.math.BigDecimal longitude, Integer operatorId) {
    return gpsTrackingDAO.logStationEvent(vehicleId, stationId, "DEPARTURE", latitude, longitude, operatorId);
  }

  /**
   * Generate arrival/departure report for a vehicle
   * 
   * @param vehicleId the vehicle ID
   * @return formatted report string
   */
  public String generateArrivalDepartureReport(int vehicleId) {
    List<GPSTrackingDTO> events = getStationEvents(vehicleId, null);
    StringBuilder report = new StringBuilder();

    report.append("=== Vehicle ").append(vehicleId).append(" - Station Arrival/Departure Report ===\n");

    if (events.isEmpty()) {
      report.append("No station events recorded for this vehicle.\n");
    } else {
      for (GPSTrackingDTO event : events) {
        report.append("Station: ").append(event.getStationId())
            .append(" | Event: ").append(event.getEventType())
            .append(" | Time: ").append(event.getTimestamp())
            .append(" | Operator: ").append(event.getOperatorId() != null ? event.getOperatorId() : "System")
            .append("\n");
      }
    }

    return report.toString();
  }
}
