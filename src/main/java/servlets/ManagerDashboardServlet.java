package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import controllers.PTFMSController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import transferobjects.UserDTO;
import transferobjects.VehicleDTO;

/**
 * Servlet for handling manager dashboard in the PTFMS system.
 * Provides comprehensive manager dashboard with fleet overview, management
 * capabilities, analytics, and administrative functions. Displays key
 * performance indicators, alerts, and operational summaries for managers.
 */
public class ManagerDashboardServlet extends HttpServlet {
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
   * Handles HTTP GET requests for displaying the manager dashboard.
   * Verifies user is authenticated as a manager, collects fleet data,
   * and renders the dashboard with statistics and vehicle information.
   * 
   * @param request  the HTTP servlet request
   * @param response the HTTP servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession();
    UserDTO user = (UserDTO) session.getAttribute("user");

    // Check if user is logged in and is a manager
    if (user == null || !user.isManager()) {
      response.sendRedirect("controller/login");
      return;
    }

    // Get fleet data for dashboard
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    // Calculate fleet statistics
    int totalVehicles = vehicles.size();
    int totalBuses = (int) vehicles.stream().filter(v -> "Bus".equalsIgnoreCase(v.getVehicleType())).count();
    int totalTrains = (int) vehicles.stream().filter(v -> "Train".equalsIgnoreCase(v.getVehicleType())).count();
    int totalLightRail = (int) vehicles.stream().filter(v -> "Light Rail".equalsIgnoreCase(v.getVehicleType())).count();

    // Generate HTML dashboard
    response.setContentType("text/html;charset=UTF-8");
    try (PrintWriter out = response.getWriter()) {
      generateHeader(out);
      generateNavigation(out);
      generateManagerDashboard(out, user, totalVehicles, totalBuses, totalTrains, totalLightRail, vehicles);
      generateFooter(out);
    }
  }

  /**
   * Generates the main manager dashboard HTML content.
   * Creates dashboard cards with fleet statistics, maintenance status,
   * quick action links, and a table of recently added vehicles.
   * 
   * @param out            the PrintWriter for HTML output
   * @param user           the authenticated manager user
   * @param totalVehicles  count of all vehicles in the fleet
   * @param totalBuses     count of buses in the fleet
   * @param totalTrains    count of trains in the fleet
   * @param totalLightRail count of light rail vehicles in the fleet
   * @param vehicles       the list of vehicle data to display
   */
  private void generateManagerDashboard(PrintWriter out, UserDTO user, int totalVehicles,
      int totalBuses, int totalTrains, int totalLightRail, List<VehicleDTO> vehicles) {

    String currentDate = new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date());

    out.println("<div class='container'>");

    // Page header with consistent styling
    out.println("<div class='page-header'>");
    out.println("<h2>Manager Dashboard</h2>");
    out.println("<div class='date-display'>Today's date: " + currentDate + "</div>");
    out.println("</div>");

    // Dashboard grid
    out.println("<div class='dashboard-grid'>");

    // Fleet Overview
    out.println("<div class='dashboard-card'>");
    out.println("<h3>Fleet Overview</h3>");
    out.println("<div class='stat-item'>");
    out.println("<span>Total Vehicles:</span>");
    out.println("<span class='stat-value'>" + totalVehicles + "</span>");
    out.println("</div>");
    out.println("<div class='stat-item'>");
    out.println("<span>Buses:</span>");
    out.println("<span class='stat-value'>" + totalBuses + "</span>");
    out.println("</div>");
    out.println("<div class='stat-item'>");
    out.println("<span>Light Rail:</span>");
    out.println("<span class='stat-value'>" + totalLightRail + "</span>");
    out.println("</div>");
    out.println("<div class='stat-item'>");
    out.println("<span>Trains:</span>");
    out.println("<span class='stat-value'>" + totalTrains + "</span>");
    out.println("</div>");
    out.println("</div>");

    // Maintenance Status
    out.println("<div class='dashboard-card'>");
    out.println("<h3>Maintenance Status</h3>");
    out.println("<div class='stat-item'>");
    out.println("<span>Total Records:</span>");
    out.println("<span class='stat-value'>99</span>");
    out.println("</div>");
    out.println("<div class='stat-item'>");
    out.println("<span>Critical:</span>");
    out.println("<span class='stat-value critical'>5</span>");
    out.println("</div>");
    out.println("<div class='stat-item'>");
    out.println("<span>Due for Service:</span>");
    out.println("<span class='stat-value warning'>12</span>");
    out.println("</div>");
    out.println("</div>");

    // Quick Actions
    out.println("<div class='dashboard-card'>");
    out.println("<h3>Quick Actions</h3>");
    out.println("<div class='quick-actions'>");
    out.println("<a href='controller/vehicles?action=add' class='action-btn'>Add Vehicle</a>");
    out.println(
        "<a href='controller/maintenance?action=schedule' class='action-btn secondary'>Schedule Maintenance</a>");
    out.println("<a href='controller/fuel-logs' class='action-btn secondary'>Fuel Management</a>");
    out.println("<a href='controller/alerts' class='action-btn secondary'>View Alerts</a>");
    out.println("</div>");
    out.println("</div>");

    out.println("</div>"); // End dashboard-grid

    // Recently Added Vehicles
    out.println("<div class='vehicles-table'>");
    out.println("<h3>Recently Added Vehicles</h3>");
    out.println("<div class='table-responsive'>");
    out.println("<table>");
    out.println("<thead>");
    out.println("<tr>");
    out.println("<th>VIN</th>");
    out.println("<th>Type</th>");
    out.println("<th>Route</th>");
    out.println("<th>Status</th>");
    out.println("<th>Action</th>");
    out.println("</tr>");
    out.println("</thead>");
    out.println("<tbody>");

    if (vehicles != null && !vehicles.isEmpty()) {
      int count = 0;
      for (VehicleDTO vehicle : vehicles) {
        if (count >= 5)
          break; // Limit to 5 vehicles
        out.println("<tr>");
        out.println("<td>" + vehicle.getVin() + "</td>");
        out.println("<td>" + vehicle.getMake() + " " + vehicle.getModel() + "</td>");
        out.println("<td>" + (vehicle.getCurrentRoute() != null ? vehicle.getCurrentRoute() : "N/A") + "</td>");
        out.println("<td><span class='status status-" + vehicle.getStatus().toLowerCase() + "'>" + vehicle.getStatus()
            + "</span></td>");
        out.println("<td>");
        out.println("<a href='vehicles?action=view&id=" + vehicle.getVehicleId() + "' class='btn-link'>View</a>");
        out.println("</td>");
        out.println("</tr>");
        count++;
      }
    } else {
      out.println("<tr>");
      out.println(
          "<td colspan='5' class='no-data-cell'>No vehicles found. <a href='vehicles?action=add'>Add your first vehicle</a></td>");
      out.println("</tr>");
    }

    out.println("</tbody>");
    out.println("</table>");
    out.println("</div>");
    out.println("</div>");

    out.println("</div>"); // End container
  }

  /**
   * Generates the HTML header with CSS references for the manager dashboard.
   * Creates responsive page structure with manager-specific styling and layout
   * elements.
   * 
   * @param out the PrintWriter for writing HTML output
   */
  private void generateHeader(PrintWriter out) {
    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<meta charset='UTF-8'>");
    out.println("<title>Manager Dashboard - PTFMS</title>");
    out.println("<link rel='stylesheet' type='text/css' href='assets/styles/common.css'>");
    out.println("<link rel='stylesheet' type='text/css' href='assets/styles/manager-dashboard.css'>");
    out.println("</head>");
    out.println("<body>");
  }

  /**
   * Generates the navigation bar for manager interface.
   * Creates HTML navigation with links to all manager-accessible pages
   * and a logout button.
   * 
   * @param out the PrintWriter for HTML output
   */
  private void generateNavigation(PrintWriter out) {
    out.println("<div class='header'>");
    out.println("<h1>Public Transportation Fleet Management System</h1>");
    out.println("</div>");

    out.println("<div class='nav-bar'>");
    out.println("<div class='nav-links'>");
    out.println("<a href='manager-dashboard'>Dashboard</a>");
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
   * Generates the HTML footer for manager dashboard.
   * Closes the HTML document with proper closing tags.
   * 
   * @param out the PrintWriter for HTML output
   */
  private void generateFooter(PrintWriter out) {
    out.println("</body>");
    out.println("</html>");
  }
}
