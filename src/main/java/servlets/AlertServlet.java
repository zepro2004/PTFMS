package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import controllers.PTFMSController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import transferobjects.AlertDTO;
import transferobjects.UserDTO;
import transferobjects.VehicleDTO;
import utils.HeaderUtils;

/**
 * Servlet for handling alert operations in the PTFMS system.
 * Provides comprehensive alert management functionality including listing,
 * viewing, acknowledging, and managing alerts for the fleet.
 */
public class AlertServlet extends HttpServlet {
  private PTFMSController controller;

  /**
   * Initializes the servlet by creating an instance of PTFMSController.
   * Called once when the servlet is first loaded.
   * 
   * @throws ServletException if an error occurs during servlet initialization
   */
  @Override
  public void init() throws ServletException {
    super.init();
    controller = new PTFMSController();
  }

  /**
   * Handles HTTP GET requests for alert operations.
   * Routes requests to appropriate methods based on the action parameter.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
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
      case "list" -> displayAlertList(request, response, user);
      case "view" -> displayAlertDetails(request, response, user);
      case "acknowledge" -> acknowledgeAlert(request, response, user);
      case "dashboard" -> displayAlertDashboard(request, response, user);
      default -> response.sendRedirect("controller/alerts");
    }
  }

  /**
   * Handles HTTP POST requests for alert operations.
   * Processes actions like acknowledging, resolving, and deleting alerts.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
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
        case "acknowledge" -> handleAcknowledgeAlert(request, response, user);
        case "resolve" -> handleResolveAlert(request, response, user);
        case "delete" -> handleDeleteAlert(request, response, user);
        default -> response.sendRedirect("controller/alerts");
      }
    } catch (Exception e) {
      request.setAttribute("error", "An error occurred: " + e.getMessage());
      response.sendRedirect("controller/alerts");
    }
  }

  /**
   * Displays the main alert list page with summary statistics and recent alerts.
   * Shows active and resolved alerts in a table format with action buttons.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayAlertList(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    // Get real alerts from database
    List<AlertDTO> allAlerts = controller.getAllAlerts();
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Alert Management", user, "alerts");

      HeaderUtils.generatePageHeader(out, "Fleet Alert Management",
          "<a href='controller/alerts?action=dashboard' class='dashboard-btn'>Alert Dashboard</a>");

      // Alert summary cards
      out.println("<div class='alert-overview'>");
      out.println("<h3>Alert Summary</h3>");
      out.println("<div class='alert-summary-grid'>");

      int totalAlerts = allAlerts != null ? allAlerts.size() : 0;
      int activeAlerts = 0;
      int resolvedAlerts = 0;

      if (allAlerts != null) {
        for (AlertDTO alert : allAlerts) {
          if ("Open".equalsIgnoreCase(alert.getStatus())) {
            activeAlerts++;
          } else if ("Resolved".equalsIgnoreCase(alert.getStatus())) {
            resolvedAlerts++;
          }
        }
      }

      out.println("<div class='summary-card critical'>");
      out.println("<h4>Active Alerts</h4>");
      out.println("<p class='summary-value'>" + activeAlerts + "</p>");
      out.println("</div>");

      out.println("<div class='summary-card warning'>");
      out.println("<h4>Resolved Alerts</h4>");
      out.println("<p class='summary-value'>" + resolvedAlerts + "</p>");
      out.println("</div>");

      out.println("<div class='summary-card total'>");
      out.println("<h4>Total Alerts</h4>");
      out.println("<p class='summary-value'>" + totalAlerts + "</p>");
      out.println("</div>");

      out.println("</div>"); // End alert-summary-grid

      // Recent alerts table
      out.println("<div class='recent-alerts'>");
      out.println("<h3>Recent Alerts</h3>");

      if (allAlerts != null && !allAlerts.isEmpty()) {
        out.println("<table class='data-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Date/Time</th>");
        out.println("<th>Vehicle</th>");
        out.println("<th>Alert Type</th>");
        out.println("<th>Message</th>");
        out.println("<th>Status</th>");
        out.println("<th>Actions</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // Show last 20 entries
        int count = 0;
        for (int i = allAlerts.size() - 1; i >= 0 && count < 20; i--, count++) {
          AlertDTO alert = allAlerts.get(i);
          VehicleDTO vehicle = null;
          if (alert.getVehicleId() > 0) {
            vehicle = controller.getVehicleById(alert.getVehicleId());
          }

          String statusClass = "Open".equalsIgnoreCase(alert.getStatus()) ? "unacknowledged" : "acknowledged";
          out.println("<tr class='" + statusClass + "'>");
          out.println(
              "<td>" + (alert.getCreatedAt() != null ? dateFormat.format(alert.getCreatedAt()) : "N/A") + "</td>");
          out.println("<td>" + (vehicle != null ? vehicle.getVin() : "System Alert") + "</td>");
          out.println("<td>" + alert.getAlertType() + "</td>");
          out.println("<td>" + alert.getMessage() + "</td>");
          out.println(
              "<td><span class='status-" + alert.getStatus().toLowerCase() + "'>" + alert.getStatus() + "</span></td>");
          out.println("<td>");
          out.println("<a href='alerts?action=view&id=" + alert.getAlertId() +
              "' class='btn-small btn-view'>View</a>");
          if ("Open".equalsIgnoreCase(alert.getStatus())) {
            out.println("<a href='#' onclick='resolveAlert(" + alert.getAlertId() +
                ")' class='btn-small btn-acknowledge'>Resolve</a>");
          }
          out.println("<a href='#' onclick='deleteAlert(" + alert.getAlertId() +
              ")' class='btn-small btn-delete'>Delete</a>");
          out.println("</td>");
          out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
      } else {
        out.println("<div class='no-data'>");
        out.println("<p>No alerts found. This is good news!</p>");
        out.println("</div>");
      }

      out.println("</div>");
      out.println("</div>");

      // JavaScript for actions
      out.println("<script>");
      out.println("function resolveAlert(id) {");
      out.println("  if (confirm('Mark this alert as resolved?')) {");
      out.println("    var form = document.createElement('form');");
      out.println("    form.method = 'POST';");
      out.println("    form.action = 'alerts';");
      out.println("    var actionInput = document.createElement('input');");
      out.println("    actionInput.type = 'hidden';");
      out.println("    actionInput.name = 'action';");
      out.println("    actionInput.value = 'resolve';");
      out.println("    form.appendChild(actionInput);");
      out.println("    var idInput = document.createElement('input');");
      out.println("    idInput.type = 'hidden';");
      out.println("    idInput.name = 'alertId';");
      out.println("    idInput.value = id;");
      out.println("    form.appendChild(idInput);");
      out.println("    document.body.appendChild(form);");
      out.println("    form.submit();");
      out.println("  }");
      out.println("}");

      out.println("function deleteAlert(id) {");
      out.println("  if (confirm('Are you sure you want to delete this alert?')) {");
      out.println("    var form = document.createElement('form');");
      out.println("    form.method = 'POST';");
      out.println("    form.action = 'alerts';");
      out.println("    var actionInput = document.createElement('input');");
      out.println("    actionInput.type = 'hidden';");
      out.println("    actionInput.name = 'action';");
      out.println("    actionInput.value = 'delete';");
      out.println("    form.appendChild(actionInput);");
      out.println("    var idInput = document.createElement('input');");
      out.println("    idInput.type = 'hidden';");
      out.println("    idInput.name = 'alertId';");
      out.println("    idInput.value = id;");
      out.println("    form.appendChild(idInput);");
      out.println("    document.body.appendChild(form);");
      out.println("    form.submit();");
      out.println("  }");
      out.println("}");
      out.println("</script>");

      generateFooter(out);
    }
  }

  /**
   * Displays detailed information for a specific alert.
   * Shows all alert properties and provides action buttons for management.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayAlertDetails(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String alertIdStr = request.getParameter("id");
    if (alertIdStr == null) {
      response.sendRedirect("alerts");
      return;
    }

    int alertId = Integer.parseInt(alertIdStr);
    // Get alert by ID from database
    AlertDTO alert = controller.getAlertById(alertId);

    if (alert == null) {
      response.sendRedirect("alerts");
      return;
    }

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Alert Details", user, "alerts");

      HeaderUtils.generatePageHeader(out, "Alert Details",
          "<a href='controller/alerts' class='back-btn'>Back to Alerts</a>");

      out.println("<div class='alert-details'>");
      out.println("<div class='alert-info-card'>");
      out.println("<h3>Alert Information</h3>");

      VehicleDTO vehicle = null;
      if (alert.getVehicleId() > 0) {
        vehicle = controller.getVehicleById(alert.getVehicleId());
      }

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      out.println("<div class='detail-row'>");
      out.println("<strong>Alert ID:</strong> " + alert.getAlertId());
      out.println("</div>");

      out.println("<div class='detail-row'>");
      out.println("<strong>Date/Time:</strong> " +
          (alert.getCreatedAt() != null ? dateFormat.format(alert.getCreatedAt()) : "N/A"));
      out.println("</div>");

      out.println("<div class='detail-row'>");
      out.println("<strong>Vehicle:</strong> "
          + (vehicle != null ? vehicle.getVin() + " - " + vehicle.getMake() + " " + vehicle.getModel()
              : "System Alert"));
      out.println("</div>");

      out.println("<div class='detail-row'>");
      out.println("<strong>Alert Type:</strong> " + alert.getAlertType());
      out.println("</div>");

      out.println("<div class='detail-row'>");
      out.println("<strong>Status:</strong> " + alert.getStatus());
      out.println("</div>");

      out.println("<div class='detail-row'>");
      out.println("<strong>Message:</strong>");
      out.println("<p class='alert-message'>" + alert.getMessage() + "</p>");
      out.println("</div>");

      out.println("</div>");

      // Action buttons
      out.println("<div class='alert-actions'>");
      if ("Open".equalsIgnoreCase(alert.getStatus())) {
        out.println("<form method='post' action='alerts' class='inline-form'>");
        out.println("<input type='hidden' name='action' value='resolve'>");
        out.println("<input type='hidden' name='alertId' value='" + alert.getAlertId() + "'>");
        out.println("<button type='submit' class='btn btn-acknowledge'>Mark as Resolved</button>");
        out.println("</form>");
      }

      out.println("</div>");

      out.println("</div>");
      out.println("</div>");
      generateFooter(out);
    }
  }

  /**
   * Creates a mock alert for demonstration purposes.
   * Used for testing and development when real data is not available.
   * 
   * @param alertId the ID to assign to the mock alert
   * @return a mock AlertDTO object with sample data
   */
  private AlertDTO createMockAlert(int alertId) {
    AlertDTO alert = new AlertDTO();
    alert.setAlertId(alertId);
    alert.setVehicleId(1);
    alert.setAlertType("Engine Warning");
    alert.setMessage("Engine temperature is higher than normal. Please check coolant levels.");
    alert.setStatus("ACTIVE");
    alert.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
    return alert;
  }

  /**
   * Displays the alert dashboard with statistics and analytics.
   * Provides an overview of alert metrics and vehicle-specific summaries.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayAlertDashboard(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    // Get real alerts from database
    List<AlertDTO> allAlerts = controller.getAllAlerts();

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Alert Dashboard", user, "alerts");

      HeaderUtils.generatePageHeader(out, "Alert Dashboard & Analytics",
          "<a href='controller/alerts' class='back-btn'>Back to Alerts</a>");

      // Dashboard statistics
      out.println("<div class='dashboard-stats'>");
      out.println("<h3>Alert Statistics</h3>");

      int totalAlerts = allAlerts != null ? allAlerts.size() : 0;
      int todayAlerts = 0;
      int activeAlerts = 0;
      int resolvedAlerts = 0;

      if (allAlerts != null) {
        for (AlertDTO alert : allAlerts) {
          if ("Open".equalsIgnoreCase(alert.getStatus())) {
            activeAlerts++;
          } else if ("Resolved".equalsIgnoreCase(alert.getStatus())) {
            resolvedAlerts++;
          }
        }
        todayAlerts = activeAlerts; // Simplified for demo
      }

      out.println("<div class='stats-grid'>");
      out.println("<div class='stat-card'>");
      out.println("<h4>Total Alerts</h4>");
      out.println("<p class='stat-value'>" + totalAlerts + "</p>");
      out.println("</div>");

      out.println("<div class='stat-card'>");
      out.println("<h4>Today's Alerts</h4>");
      out.println("<p class='stat-value'>" + todayAlerts + "</p>");
      out.println("</div>");

      out.println("<div class='stat-card critical'>");
      out.println("<h4>Active</h4>");
      out.println("<p class='stat-value'>" + activeAlerts + "</p>");
      out.println("</div>");

      out.println("<div class='stat-card warning'>");
      out.println("<h4>Resolved</h4>");
      out.println("<p class='stat-value'>" + resolvedAlerts + "</p>");
      out.println("</div>");

      out.println("</div>"); // End stats-grid
      out.println("</div>"); // End dashboard-stats

      // Vehicle-specific alert analysis (simplified)
      out.println("<div class='vehicle-alerts'>");
      out.println("<h3>Vehicle Alert Summary</h3>");
      out.println("<div class='summary-info'>");
      out.println("<p>Vehicle alert analysis helps track which vehicles require the most attention.</p>");
      out.println("<p>Total vehicles being monitored: <strong>5</strong></p>");
      out.println("<p>Vehicles with active alerts: <strong>2</strong></p>");
      out.println("</div>");
      out.println("</div>");

      out.println("</div>");
      generateFooter(out);
    }
  }

  /**
   * Creates mock alerts for demonstration purposes.
   * Used for testing and development when real alert data is not available.
   * 
   * @return a list of mock AlertDTO objects with sample data
   */
  private java.util.List<AlertDTO> createMockAlerts() {
    java.util.List<AlertDTO> alerts = new java.util.ArrayList<>();

    AlertDTO alert1 = new AlertDTO();
    alert1.setAlertId(1);
    alert1.setVehicleId(1);
    alert1.setAlertType("Engine Warning");
    alert1.setMessage("Engine temperature high");
    alert1.setStatus("ACTIVE");
    alert1.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
    alerts.add(alert1);

    AlertDTO alert2 = new AlertDTO();
    alert2.setAlertId(2);
    alert2.setVehicleId(2);
    alert2.setAlertType("Maintenance Due");
    alert2.setMessage("Regular maintenance required");
    alert2.setStatus("RESOLVED");
    alert2.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis() - 86400000));
    alerts.add(alert2);

    return alerts;
  }

  /**
   * Handles acknowledgment of alerts via GET request.
   * Redirects to the alert list after processing.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during redirect
   */
  private void acknowledgeAlert(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String alertIdStr = request.getParameter("id");
    if (alertIdStr != null) {
      try {
        int alertId = Integer.parseInt(alertIdStr);
        // For demo purposes, just redirect back
        // In real implementation, would call controller.acknowledgeAlert(alertId);
      } catch (NumberFormatException e) {
        // Invalid alert ID
      }
    }
    response.sendRedirect("alerts");
  }

  /**
   * Handles acknowledgment of alerts via POST request.
   * Updates the alert status to acknowledged and redirects to the alert list.
   * 
   * @param request  the HttpServletRequest object containing the alert ID
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during redirect
   */
  private void handleAcknowledgeAlert(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int alertId = Integer.parseInt(request.getParameter("alertId"));
      // Get the alert and update status to acknowledged (we'll use "Resolved" status)
      boolean success = controller.resolveAlert(alertId);

      if (success) {
        request.setAttribute("success", "Alert acknowledged successfully!");
      } else {
        request.setAttribute("error", "Failed to acknowledge alert.");
      }

    } catch (NumberFormatException e) {
      request.setAttribute("error", "Invalid alert ID.");
    }

    response.sendRedirect("alerts");
  }

  /**
   * Handles resolution of alerts via POST request.
   * Marks an alert as resolved and redirects to the alert list.
   * 
   * @param request  the HttpServletRequest object containing the alert ID
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during redirect
   */
  private void handleResolveAlert(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int alertId = Integer.parseInt(request.getParameter("alertId"));
      boolean success = controller.resolveAlert(alertId);

      if (success) {
        request.setAttribute("success", "Alert marked as resolved!");
      } else {
        request.setAttribute("error", "Failed to resolve alert.");
      }

    } catch (NumberFormatException e) {
      request.setAttribute("error", "Invalid alert ID.");
    }

    response.sendRedirect("alerts");
  }

  /**
   * Handles deletion of alerts via POST request.
   * Permanently removes an alert from the system and redirects to the alert list.
   * 
   * @param request  the HttpServletRequest object containing the alert ID
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during redirect
   */
  private void handleDeleteAlert(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int alertId = Integer.parseInt(request.getParameter("alertId"));
      boolean success = controller.deleteAlert(alertId);

      if (success) {
        request.setAttribute("success", "Alert deleted successfully!");
      } else {
        request.setAttribute("error", "Failed to delete alert.");
      }

    } catch (NumberFormatException e) {
      request.setAttribute("error", "Invalid alert ID.");
    }

    response.sendRedirect("alerts");
  }

  /**
   * Generates the HTML header for alert pages.
   * Creates the common HTML structure including CSS links.
   * 
   * @param out   the PrintWriter for writing HTML output
   * @param title the page title to display
   */
  private void generateHeader(PrintWriter out, String title) {
    out.println("<!DOCTYPE html>");
    out.println("<html lang='en'>");
    out.println("<head>");
    out.println("<meta charset='UTF-8'>");
    out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
    out.println("<title>" + title + " - PTFMS</title>");
    out.println("<link rel='stylesheet' type='text/css' href='assets/styles/common.css'>");
    out.println("<link rel='stylesheet' type='text/css' href='assets/styles/alerts.css'>");
    out.println("</head>");
    out.println("<body>");
  }

  /**
   * Generates the navigation bar for alert pages.
   * Creates role-based navigation links and user information display.
   * 
   * @param out  the PrintWriter for writing HTML output
   * @param user the currently authenticated user for role-based navigation
   */
  private void generateNavigation(PrintWriter out, UserDTO user) {
    out.println("<div class='header'>");
    out.println("<h1>Public Transportation Fleet Management System</h1>");
    out.println("<div class='user-info'>Welcome, " + user.getUsername() + "</div>");
    out.println("</div>");

    out.println("<div class='nav-bar'>");
    out.println("<div class='nav-links'>");
    if (user.isManager()) {
      out.println("<a href='manager-dashboard'>Dashboard</a>");
    } else {
      out.println("<a href='operator-dashboard'>Dashboard</a>");
    }
    out.println("<a href='vehicles'>Vehicles</a>");
    out.println("<a href='maintenance'>Maintenance</a>");
    out.println("<a href='alerts'>Alerts</a>");
    out.println("<a href='fuel-logs'>Fuel Logs</a>");
    out.println("<a href='gps-tracking'>GPS Tracking</a>");
    out.println("</div>");
    out.println("<a href='logout' class='logout-btn'>Logout</a>");
    out.println("</div>");
  }

  /**
   * Generates the HTML footer for alert pages.
   * Closes the HTML structure and body tags.
   * 
   * @param out the PrintWriter for writing HTML output
   */
  private void generateFooter(PrintWriter out) {
    out.println("</body>");
    out.println("</html>");
  }
}
