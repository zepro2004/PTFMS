package dataaccesslayer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import transferobjects.GPSTrackingDTO;

/**
 * Data Access Object for GPS Tracking operations in the PTFMS system.
 * Handles all database operations related to vehicle location tracking
 * including real-time position updates, route history, geofencing,
 * and location-based analytics.
 */
public class GPSTrackingDAO {

  /**
   * Add a new GPS tracking entry
   * 
   * @param gpsTracking the GPS tracking entry to add
   * @return true if successful
   */
  public boolean addGPSTracking(GPSTrackingDTO gpsTracking) {
    String sql = "INSERT INTO gps_tracking (vehicle_id, latitude, longitude, operator_id, station_id, event_type, speed, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, gpsTracking.getVehicleId());
      stmt.setBigDecimal(2, gpsTracking.getLatitude());
      stmt.setBigDecimal(3, gpsTracking.getLongitude());
      if (gpsTracking.getOperatorId() != null) {
        stmt.setInt(4, gpsTracking.getOperatorId());
      } else {
        stmt.setNull(4, java.sql.Types.INTEGER);
      }
      stmt.setString(5, gpsTracking.getStationId());
      stmt.setString(6, gpsTracking.getEventType() != null ? gpsTracking.getEventType() : "LOCATION");
      stmt.setBigDecimal(7, gpsTracking.getSpeed());
      stmt.setString(8, gpsTracking.getNotes());

      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Find GPS tracking by ID
   * 
   * @param trackingId the tracking ID to search for
   * @return GPSTrackingDTO if found, null otherwise
   */
  public GPSTrackingDTO findById(int trackingId) {
    String sql = "SELECT tracking_id, vehicle_id, latitude, longitude, timestamp, operator_id, station_id, event_type, speed, notes FROM gps_tracking WHERE tracking_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, trackingId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        GPSTrackingDTO gpsTracking = new GPSTrackingDTO();
        gpsTracking.setTrackingId(rs.getInt("tracking_id"));
        gpsTracking.setVehicleId(rs.getInt("vehicle_id"));
        gpsTracking.setLatitude(rs.getBigDecimal("latitude"));
        gpsTracking.setLongitude(rs.getBigDecimal("longitude"));
        gpsTracking.setTimestamp(rs.getTimestamp("timestamp"));
        int operatorId = rs.getInt("operator_id");
        if (!rs.wasNull()) {
          gpsTracking.setOperatorId(operatorId);
        }
        gpsTracking.setStationId(rs.getString("station_id"));
        gpsTracking.setEventType(rs.getString("event_type"));
        gpsTracking.setSpeed(rs.getBigDecimal("speed"));
        gpsTracking.setNotes(rs.getString("notes"));

        return gpsTracking;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get GPS tracking by vehicle ID
   * 
   * @param vehicleId the vehicle ID to search for
   * @return list of GPS tracking entries for the vehicle
   */
  public List<GPSTrackingDTO> getTrackingByVehicleId(int vehicleId) {
    List<GPSTrackingDTO> trackingList = new ArrayList<>();
    String sql = "SELECT tracking_id, vehicle_id, latitude, longitude, timestamp, operator_id, station_id, event_type, speed, notes FROM gps_tracking WHERE vehicle_id = ? ORDER BY timestamp DESC";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, vehicleId);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        GPSTrackingDTO gpsTracking = new GPSTrackingDTO();
        gpsTracking.setTrackingId(rs.getInt("tracking_id"));
        gpsTracking.setVehicleId(rs.getInt("vehicle_id"));
        gpsTracking.setLatitude(rs.getBigDecimal("latitude"));
        gpsTracking.setLongitude(rs.getBigDecimal("longitude"));
        gpsTracking.setTimestamp(rs.getTimestamp("timestamp"));
        int operatorId = rs.getInt("operator_id");
        if (!rs.wasNull()) {
          gpsTracking.setOperatorId(operatorId);
        }
        gpsTracking.setStationId(rs.getString("station_id"));
        gpsTracking.setEventType(rs.getString("event_type"));
        gpsTracking.setSpeed(rs.getBigDecimal("speed"));
        gpsTracking.setNotes(rs.getString("notes"));
        trackingList.add(gpsTracking);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return trackingList;
  }

  /**
   * Get latest GPS position for a vehicle
   * 
   * @param vehicleId the vehicle ID to search for
   * @return latest GPSTrackingDTO if found, null otherwise
   */
  public GPSTrackingDTO getLatestPositionByVehicleId(int vehicleId) {
    String sql = "SELECT tracking_id, vehicle_id, latitude, longitude, timestamp, operator_id, station_id, event_type, speed, notes FROM gps_tracking WHERE vehicle_id = ? ORDER BY timestamp DESC LIMIT 1";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, vehicleId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        GPSTrackingDTO gpsTracking = new GPSTrackingDTO();
        gpsTracking.setTrackingId(rs.getInt("tracking_id"));
        gpsTracking.setVehicleId(rs.getInt("vehicle_id"));
        gpsTracking.setLatitude(rs.getBigDecimal("latitude"));
        gpsTracking.setLongitude(rs.getBigDecimal("longitude"));
        gpsTracking.setTimestamp(rs.getTimestamp("timestamp"));
        int operatorId = rs.getInt("operator_id");
        if (!rs.wasNull()) {
          gpsTracking.setOperatorId(operatorId);
        }
        gpsTracking.setStationId(rs.getString("station_id"));
        gpsTracking.setEventType(rs.getString("event_type"));
        gpsTracking.setSpeed(rs.getBigDecimal("speed"));
        gpsTracking.setNotes(rs.getString("notes"));
        return gpsTracking;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Delete GPS tracking entry
   * 
   * @param trackingId the tracking ID to delete
   * @return true if successful
   */
  public boolean deleteGPSTracking(int trackingId) {
    String sql = "DELETE FROM gps_tracking WHERE tracking_id = ?";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, trackingId);
      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Delete old GPS tracking entries (older than specified days)
   * 
   * @param daysOld number of days to keep
   * @return number of entries deleted
   */
  public int deleteOldTrackingEntries(int daysOld) {
    String sql = "DELETE FROM gps_tracking WHERE timestamp < DATE_SUB(NOW(), INTERVAL ? DAY)";

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, daysOld);
      return stmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  /**
   * Get station arrival/departure events for a vehicle
   * 
   * @param vehicleId the vehicle ID
   * @param stationId the station ID (optional, null for all stations)
   * @return list of station events for the vehicle
   */
  public List<GPSTrackingDTO> getStationEvents(int vehicleId, String stationId) {
    List<GPSTrackingDTO> events = new ArrayList<>();
    String sql;

    if (stationId != null) {
      sql = "SELECT tracking_id, vehicle_id, latitude, longitude, timestamp, operator_id, station_id, event_type, speed, notes FROM gps_tracking WHERE vehicle_id = ? AND station_id = ? AND event_type IN ('ARRIVAL', 'DEPARTURE') ORDER BY timestamp DESC";
    } else {
      sql = "SELECT tracking_id, vehicle_id, latitude, longitude, timestamp, operator_id, station_id, event_type, speed, notes FROM gps_tracking WHERE vehicle_id = ? AND event_type IN ('ARRIVAL', 'DEPARTURE') ORDER BY timestamp DESC";
    }

    try (Connection conn = DataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, vehicleId);
      if (stationId != null) {
        stmt.setString(2, stationId);
      }

      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        GPSTrackingDTO gpsTracking = new GPSTrackingDTO();
        gpsTracking.setTrackingId(rs.getInt("tracking_id"));
        gpsTracking.setVehicleId(rs.getInt("vehicle_id"));
        gpsTracking.setLatitude(rs.getBigDecimal("latitude"));
        gpsTracking.setLongitude(rs.getBigDecimal("longitude"));
        gpsTracking.setTimestamp(rs.getTimestamp("timestamp"));
        int operatorId = rs.getInt("operator_id");
        if (!rs.wasNull()) {
          gpsTracking.setOperatorId(operatorId);
        }
        gpsTracking.setStationId(rs.getString("station_id"));
        gpsTracking.setEventType(rs.getString("event_type"));
        gpsTracking.setSpeed(rs.getBigDecimal("speed"));
        gpsTracking.setNotes(rs.getString("notes"));
        events.add(gpsTracking);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return events;
  }

  /**
   * Log a station arrival or departure event
   * 
   * @param vehicleId  the vehicle ID
   * @param stationId  the station ID
   * @param eventType  ARRIVAL or DEPARTURE
   * @param latitude   the latitude coordinate
   * @param longitude  the longitude coordinate
   * @param operatorId the operator ID (optional)
   * @return true if successful
   */
  public boolean logStationEvent(int vehicleId, String stationId, String eventType,
      BigDecimal latitude, BigDecimal longitude, Integer operatorId) {
    GPSTrackingDTO gpsTracking = new GPSTrackingDTO();
    gpsTracking.setVehicleId(vehicleId);
    gpsTracking.setStationId(stationId);
    gpsTracking.setEventType(eventType);
    gpsTracking.setLatitude(latitude);
    gpsTracking.setLongitude(longitude);
    gpsTracking.setOperatorId(operatorId);
    gpsTracking.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));

    return addGPSTracking(gpsTracking);
  }
}
