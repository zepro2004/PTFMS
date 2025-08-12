package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import controllers.PTFMSController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import transferobjects.GPSTrackingDTO;
import transferobjects.UserDTO;
import transferobjects.VehicleDTO;
import utils.HeaderUtils;

/**
 * Servlet for handling GPS tracking operations in the PTFMS system.
 * Provides comprehensive GPS tracking functionality for vehicles including
 * real-time location monitoring, route tracking, geofencing, and location
 * history management. Supports station events and route optimization.
 */
public class GPSTrackingServlet extends HttpServlet {
  private PTFMSController controller;

  /**
   * Initializes the servlet by creating an instance of PTFMSController.
   * Called once when the servlet is first loaded.
   * 
   * @throws ServletException if an error occurs during servlet initialization
   */
  @Override
  public void init() throws ServletException {
    controller = new PTFMSController();
  }

  /**
   * Handles HTTP GET requests for GPS tracking operations.
   * Routes to appropriate view based on the 'action' parameter.
   * Supports actions: list (default), add, view, station, and report.
   * Requires authenticated user; redirects to login if session is invalid.
   *
   * @param request  the HTTP servlet request
   * @param response the HTTP servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs during response writing
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession();
    UserDTO user = (UserDTO) session.getAttribute("user");

    if (user == null) {
      response.sendRedirect("controller/login");
      return;
    }

    String action = request.getParameter("action");
    if (action == null)
      action = "list";

    response.setContentType("text/html;charset=UTF-8");

    switch (action) {
      case "list":
        displayGPSTrackingList(request, response, user);
        break;
      case "add":
        displayAddGPSTrackingForm(request, response, user);
        break;
      case "view":
        displayVehicleTracking(request, response, user);
        break;
      case "station":
        displayStationEvents(request, response, user);
        break;
      case "report":
        displayTrackingReport(request, response, user);
        break;
      default:
        displayGPSTrackingList(request, response, user);
        break;
    }
  }

  /**
   * Handles HTTP POST requests for GPS tracking state-changing operations.
   * Processes form submissions for adding tracking entries and logging station
   * events.
   * Supports actions: add, station-arrival, and station-departure.
   * Requires authenticated user; redirects to login if session is invalid.
   *
   * @param request  the HTTP servlet request containing form data
   * @param response the HTTP servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs during response processing
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession();
    UserDTO user = (UserDTO) session.getAttribute("user");

    if (user == null) {
      response.sendRedirect("controller/login");
      return;
    }

    String action = request.getParameter("action");

    try {
      switch (action) {
        case "add":
          handleAddGPSTracking(request, response, user);
          break;
        case "station-arrival":
          handleStationArrival(request, response, user);
          break;
        case "station-departure":
          handleStationDeparture(request, response, user);
          break;
        default:
          response.sendRedirect("controller/gps-tracking");
          break;
      }
    } catch (Exception e) {
      request.setAttribute("error", "Error processing GPS tracking: " + e.getMessage());
      doGet(request, response);
    }
  }

  /**
   * Displays the GPS tracking overview page with vehicle location information.
   * Shows current vehicle status, locations, and navigation options.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayGPSTrackingList(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "GPS Tracking Overview", user, "gps-tracking");

      String success = (String) request.getAttribute("success");
      if (success != null) {
        out.println("<div class='success'>" + success + "</div>");
      }

      String error = (String) request.getAttribute("error");
      if (error != null) {
        out.println("<div class='error'>" + error + "</div>");
      }

      // Vehicle tracking status
      out.println("<div class='tracking-grid'>");

      if (vehicles != null && !vehicles.isEmpty()) {
        for (VehicleDTO vehicle : vehicles) {
          out.println("<div class='tracking-card'>");
          out.println("<h3>Vehicle " + vehicle.getVin() + "</h3>");
          out.println("<div class='vehicle-info'>");
          out.println("<p><strong>Type:</strong> " + vehicle.getMake() + " " + vehicle.getModel() + "</p>");
          out.println("<p><strong>Year:</strong> " + vehicle.getYear() + "</p>");

          // Check if vehicle has tracking data
          boolean hasTracking = controller.hasTrackingData(vehicle.getVehicleId());
          int trackingCount = controller.getTrackingDataCount(vehicle.getVehicleId());

          out.println("<p><strong>Tracking Status:</strong> " +
              (hasTracking ? "<span class='status-active'>Active (" + trackingCount + " entries)</span>"
                  : "<span class='status-inactive'>No Data</span>")
              + "</p>");
          out.println("</div>");

          out.println("<div class='tracking-actions'>");
          out.println("<a href='controller/gps-tracking?action=view&vehicleId=" + vehicle.getVehicleId() +
              "' class='btn-small'>View Details</a>");
          out.println("<a href='controller/gps-tracking?action=station&vehicleId=" + vehicle.getVehicleId() +
              "' class='btn-small'>Station Events</a>");
          out.println("<a href='controller/gps-tracking?action=report&vehicleId=" + vehicle.getVehicleId() +
              "' class='btn-small'>Generate Report</a>");
          out.println("</div>");

          out.println("</div>");
        }
      } else {
        out.println("<div class='no-data'>");
        out.println("<p>No vehicles found. <a href='controller/vehicles?action=add'>Add a vehicle first</a></p>");
        out.println("</div>");
      }

      out.println("</div>"); // End tracking-grid
      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Displays the form for adding new GPS tracking data.
   * Provides input fields for location coordinates, timestamps, and vehicle
   * selection.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayAddGPSTrackingForm(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Add GPS Tracking Entry", user, "gps-tracking");

      HeaderUtils.generatePageHeader(out, "Add GPS Tracking Entry",
          "<a href='controller/gps-tracking' class='back-btn'>Back to Tracking</a>");

      out.println("<div class='form-container'>");
      out.println("<form method='post' action='controller/gps-tracking'>");
      out.println("<input type='hidden' name='action' value='add'>");

      out.println("<div class='form-group'>");
      out.println("<label for='vehicleId'>Vehicle:</label>");
      out.println("<select id='vehicleId' name='vehicleId' required>");
      out.println("<option value=''>Select Vehicle</option>");

      if (vehicles != null) {
        for (VehicleDTO vehicle : vehicles) {
          out.println("<option value='" + vehicle.getVehicleId() + "'>" +
              vehicle.getVin() + " - " + vehicle.getMake() + " " + vehicle.getModel() + "</option>");
        }
      }

      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='latitude'>Latitude:</label>");
      out.println("<input type='number' id='latitude' name='latitude' step='0.000001' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='longitude'>Longitude:</label>");
      out.println("<input type='number' id='longitude' name='longitude' step='0.000001' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='eventType'>Event Type:</label>");
      out.println("<select id='eventType' name='eventType' required>");
      out.println("<option value=''>Select Event Type</option>");
      out.println("<option value='LOCATION'>Location Update</option>");
      out.println("<option value='ARRIVAL'>Station Arrival</option>");
      out.println("<option value='DEPARTURE'>Station Departure</option>");
      out.println("<option value='EMERGENCY'>Emergency</option>");
      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='stationId'>Station ID (Optional):</label>");
      out.println("<input type='text' id='stationId' name='stationId' placeholder='e.g., STN-001'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='speed'>Speed (Optional):</label>");
      out.println("<input type='number' id='speed' name='speed' step='0.1' placeholder='km/h'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='notes'>Notes (Optional):</label>");
      out.println("<textarea id='notes' name='notes' rows='3' placeholder='Additional information'></textarea>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<input type='submit' value='Add GPS Entry' class='submit-btn'>");
      out.println("</div>");

      out.println("</form>");
      out.println("</div>");
      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Displays detailed GPS tracking information for a specific vehicle.
   * Shows location history, route data, and real-time tracking information.
   * 
   * @param request  the HttpServletRequest object containing vehicle ID parameter
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayVehicleTracking(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String vehicleIdStr = request.getParameter("vehicleId");
    if (vehicleIdStr == null) {
      response.sendRedirect("controller/gps-tracking");
      return;
    }

    int vehicleId = Integer.parseInt(vehicleIdStr);
    VehicleDTO vehicle = controller.getVehicleById(vehicleId);
    List<GPSTrackingDTO> trackingData = controller.getGPSTrackingByVehicleId(vehicleId);

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Vehicle Tracking Details", user, "gps-tracking");

      HeaderUtils.generatePageHeader(out, "Vehicle Tracking Details",
          "<a href='controller/gps-tracking' class='back-btn'>Back to Overview</a>");

      if (vehicle != null) {
        out.println("<div class='vehicle-summary'>");
        out.println("<h3>Vehicle Information</h3>");
        out.println("<p><strong>VIN:</strong> " + vehicle.getVin() + "</p>");
        out.println("<p><strong>Type:</strong> " + vehicle.getMake() + " " + vehicle.getModel() + "</p>");
        out.println("<p><strong>Year:</strong> " + vehicle.getYear() + "</p>");
        out.println("</div>");
      }

      out.println("<div class='tracking-history'>");
      out.println("<h3>Tracking History</h3>");

      if (trackingData != null && !trackingData.isEmpty()) {
        out.println("<table class='data-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Timestamp</th>");
        out.println("<th>Event Type</th>");
        out.println("<th>Location</th>");
        out.println("<th>Station</th>");
        out.println("<th>Speed</th>");
        out.println("<th>Notes</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (GPSTrackingDTO tracking : trackingData) {
          out.println("<tr>");
          out.println("<td>" + dateFormat.format(tracking.getTimestamp()) + "</td>");
          out.println("<td><span class='event-" + tracking.getEventType().toLowerCase() + "'>" +
              tracking.getEventType() + "</span></td>");
          out.println("<td>" + tracking.getLatitude() + ", " + tracking.getLongitude() + "</td>");
          out.println("<td>" + (tracking.getStationId() != null ? tracking.getStationId() : "N/A") + "</td>");
          out.println("<td>" + (tracking.getSpeed() != null ? tracking.getSpeed() + " km/h" : "N/A") + "</td>");
          out.println("<td>" + (tracking.getNotes() != null ? tracking.getNotes() : "") + "</td>");
          out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
      } else {
        out.println("<div class='no-data'>");
        out.println("<p>No tracking data found for this vehicle.</p>");
        out.println("<a href='controller/gps-tracking?action=add' class='btn'>Add First Entry</a>");
        out.println("</div>");
      }

      out.println("</div>");
      out.println("</div>");
      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Displays station arrival and departure events for a specific vehicle.
   * Shows history of station events and provides quick forms for logging
   * new arrivals and departures. Requires vehicleId parameter.
   *
   * @param request  the HTTP servlet request with vehicleId parameter
   * @param response the HTTP servlet response for rendering the page
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayStationEvents(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String vehicleIdStr = request.getParameter("vehicleId");
    if (vehicleIdStr == null) {
      response.sendRedirect("controller/gps-tracking");
      return;
    }

    int vehicleId = Integer.parseInt(vehicleIdStr);
    VehicleDTO vehicle = controller.getVehicleById(vehicleId);
    List<GPSTrackingDTO> stationEvents = controller.getStationEvents(vehicleId, null);

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Station Events", user, "gps-tracking");

      HeaderUtils.generatePageHeader(out, "Station Arrival/Departure Events",
          "<a href='controller/gps-tracking' class='back-btn'>Back to Overview</a>");

      if (vehicle != null) {
        out.println("<div class='vehicle-summary'>");
        out.println("<h3>Vehicle: " + vehicle.getVin() + "</h3>");
        out.println("<p>" + vehicle.getMake() + " " + vehicle.getModel() + "</p>");
        out.println("</div>");
      }

      // Quick station event forms
      out.println("<div class='station-actions'>");
      out.println("<h3>Quick Station Events</h3>");

      out.println("<div class='station-forms'>");

      // Arrival form
      out.println("<div class='station-form'>");
      out.println("<h4>Log Arrival</h4>");
      out.println("<form method='post' action='controller/gps-tracking' class='inline-form'>");
      out.println("<input type='hidden' name='action' value='station-arrival'>");
      out.println("<input type='hidden' name='vehicleId' value='" + vehicleId + "'>");
      out.println("<input type='text' name='stationId' placeholder='Station ID' required>");
      out.println("<input type='number' name='latitude' placeholder='Latitude' step='0.000001' required>");
      out.println("<input type='number' name='longitude' placeholder='Longitude' step='0.000001' required>");
      out.println("<input type='submit' value='Log Arrival' class='btn btn-arrival'>");
      out.println("</form>");
      out.println("</div>");

      // Departure form
      out.println("<div class='station-form'>");
      out.println("<h4>Log Departure</h4>");
      out.println("<form method='post' action='controller/gps-tracking' class='inline-form'>");
      out.println("<input type='hidden' name='action' value='station-departure'>");
      out.println("<input type='hidden' name='vehicleId' value='" + vehicleId + "'>");
      out.println("<input type='text' name='stationId' placeholder='Station ID' required>");
      out.println("<input type='number' name='latitude' placeholder='Latitude' step='0.000001' required>");
      out.println("<input type='number' name='longitude' placeholder='Longitude' step='0.000001' required>");
      out.println("<input type='submit' value='Log Departure' class='btn btn-departure'>");
      out.println("</form>");
      out.println("</div>");

      out.println("</div>"); // End station-forms
      out.println("</div>"); // End station-actions

      // Station events history
      out.println("<div class='station-events'>");
      out.println("<h3>Station Events History</h3>");

      if (stationEvents != null && !stationEvents.isEmpty()) {
        out.println("<table class='data-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Station ID</th>");
        out.println("<th>Event</th>");
        out.println("<th>Timestamp</th>");
        out.println("<th>Location</th>");
        out.println("<th>Operator</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (GPSTrackingDTO event : stationEvents) {
          out.println("<tr>");
          out.println("<td>" + event.getStationId() + "</td>");
          out.println("<td><span class='event-" + event.getEventType().toLowerCase() + "'>" +
              event.getEventType() + "</span></td>");
          out.println("<td>" + dateFormat.format(event.getTimestamp()) + "</td>");
          out.println("<td>" + event.getLatitude() + ", " + event.getLongitude() + "</td>");
          out.println("<td>" + (event.getOperatorId() != null ? "Op-" + event.getOperatorId() : "System") + "</td>");
          out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
      } else {
        out.println("<div class='no-data'>");
        out.println("<p>No station events recorded for this vehicle.</p>");
        out.println("</div>");
      }

      out.println("</div>");
      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Displays a formatted textual report of arrival and departure events.
   * Presents vehicle information and a preformatted text block with
   * chronological tracking data. Requires vehicleId parameter.
   *
   * @param request  the HTTP servlet request with vehicleId parameter
   * @param response the HTTP servlet response for rendering the page
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayTrackingReport(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String vehicleIdStr = request.getParameter("vehicleId");
    if (vehicleIdStr == null) {
      response.sendRedirect("controller/gps-tracking");
      return;
    }

    int vehicleId = Integer.parseInt(vehicleIdStr);
    VehicleDTO vehicle = controller.getVehicleById(vehicleId);
    String report = controller.generateArrivalDepartureReport(vehicleId);

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Tracking Report", user, "gps-tracking");

      HeaderUtils.generatePageHeader(out, "GPS Tracking Report",
          "<a href='controller/gps-tracking' class='back-btn'>Back to Overview</a>");

      if (vehicle != null) {
        out.println("<div class='vehicle-summary'>");
        out.println("<h3>Vehicle: " + vehicle.getVin() + "</h3>");
        out.println("<p>" + vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getYear() + ")</p>");
        out.println("</div>");
      }

      out.println("<div class='report-content'>");
      out.println("<h3>Arrival/Departure Report</h3>");
      out.println("<div class='report-text'>");
      out.println("<pre>" + report + "</pre>");
      out.println("</div>");
      out.println("</div>");

      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Processes the submission of a new GPS tracking entry from the form.
   * Creates and persists a new tracking record with location coordinates,
   * event type, timestamp, and optional metadata like speed or notes.
   *
   * @param request  the HTTP servlet request containing form data
   * @param response the HTTP servlet response
   * @param user     the currently authenticated user (for operator ID)
   * @throws IOException if an I/O error occurs
   */
  private void handleAddGPSTracking(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
      BigDecimal latitude = new BigDecimal(request.getParameter("latitude"));
      BigDecimal longitude = new BigDecimal(request.getParameter("longitude"));
      String eventType = request.getParameter("eventType");
      String stationId = request.getParameter("stationId");
      String speedStr = request.getParameter("speed");
      String notes = request.getParameter("notes");

      GPSTrackingDTO gpsTracking = new GPSTrackingDTO();
      gpsTracking.setVehicleId(vehicleId);
      gpsTracking.setLatitude(latitude);
      gpsTracking.setLongitude(longitude);
      gpsTracking.setEventType(eventType);
      gpsTracking.setTimestamp(new Timestamp(System.currentTimeMillis()));
      gpsTracking.setOperatorId(user.getUserId());

      if (stationId != null && !stationId.trim().isEmpty()) {
        gpsTracking.setStationId(stationId.trim());
      }

      if (speedStr != null && !speedStr.trim().isEmpty()) {
        gpsTracking.setSpeed(new BigDecimal(speedStr));
      }

      if (notes != null && !notes.trim().isEmpty()) {
        gpsTracking.setNotes(notes.trim());
      }

      boolean success = controller.addGPSTracking(gpsTracking);

      if (success) {
        request.setAttribute("success", "GPS tracking entry added successfully!");
      } else {
        request.setAttribute("error", "Failed to add GPS tracking entry.");
      }

    } catch (Exception e) {
      request.setAttribute("error", "Error adding GPS tracking entry: " + e.getMessage());
    }

    response.sendRedirect("controller/gps-tracking");
  }

  /**
   * Processes a station arrival event for a vehicle.
   * Records when a vehicle arrives at a transit station with location data.
   * Uses condensed form data from the station events quick form.
   *
   * @param request  the HTTP servlet request containing station arrival data
   * @param response the HTTP servlet response
   * @param user     the currently authenticated user (for operator ID)
   * @throws IOException if an I/O error occurs
   */
  private void handleStationArrival(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
      String stationId = request.getParameter("stationId");
      BigDecimal latitude = new BigDecimal(request.getParameter("latitude"));
      BigDecimal longitude = new BigDecimal(request.getParameter("longitude"));

      boolean success = controller.logStationArrival(vehicleId, stationId, latitude, longitude, user.getUserId());

      if (success) {
        request.setAttribute("success", "Station arrival logged successfully!");
      } else {
        request.setAttribute("error", "Failed to log station arrival.");
      }

    } catch (Exception e) {
      request.setAttribute("error", "Error logging station arrival: " + e.getMessage());
    }

    response.sendRedirect("controller/gps-tracking?action=station&vehicleId=" + request.getParameter("vehicleId"));
  }

  /**
   * Processes a station departure event for a vehicle.
   * Records when a vehicle leaves a transit station with location data.
   * Uses condensed form data from the station events quick form.
   *
   * @param request  the HTTP servlet request containing station departure data
   * @param response the HTTP servlet response
   * @param user     the currently authenticated user (for operator ID)
   * @throws IOException if an I/O error occurs
   */
  private void handleStationDeparture(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
      String stationId = request.getParameter("stationId");
      BigDecimal latitude = new BigDecimal(request.getParameter("latitude"));
      BigDecimal longitude = new BigDecimal(request.getParameter("longitude"));

      boolean success = controller.logStationDeparture(vehicleId, stationId, latitude, longitude, user.getUserId());

      if (success) {
        request.setAttribute("success", "Station departure logged successfully!");
      } else {
        request.setAttribute("error", "Failed to log station departure.");
      }

    } catch (Exception e) {
      request.setAttribute("error", "Error logging station departure: " + e.getMessage());
    }

    response.sendRedirect("controller/gps-tracking?action=station&vehicleId=" + request.getParameter("vehicleId"));
  }
}
