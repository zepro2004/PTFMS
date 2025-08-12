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
 * Servlet for handling operator dashboard in the PTFMS system.
 * Provides comprehensive operator dashboard with vehicle assignments,
 * performance metrics, daily operations overview, and task management
 * capabilities specific to vehicle operators.
 */
public class OperatorDashboardServlet extends HttpServlet {
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
   * Handles HTTP GET requests for displaying the operator dashboard.
   * Verifies user is authenticated, retrieves vehicle data, and renders
   * the operator-specific dashboard with assignments and available vehicles.
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

    // Check if user is logged in
    if (user == null) {
      response.sendRedirect("controller/login");
      return;
    }

    // Get vehicle data for operator view
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    // Generate HTML dashboard
    response.setContentType("text/html;charset=UTF-8");
    try (PrintWriter out = response.getWriter()) {
      generateHeader(out);
      generateNavigation(out, user);
      generateOperatorDashboard(out, user, vehicles);
      generateFooter(out);
    }
  }

  /**
   * Generates the main operator dashboard content with vehicle assignments and
   * quick actions.
   * Displays current date, assigned vehicles, quick access buttons for common
   * operations,
   * and personalized operator-specific information and controls.
   * 
   * @param out      the PrintWriter object for writing HTML output to the
   *                 response
   * @param user     the currently authenticated operator user
   * @param vehicles the list of vehicles assigned to or accessible by the
   *                 operator
   */
  private void generateOperatorDashboard(PrintWriter out, UserDTO user, List<VehicleDTO> vehicles) {
    String currentDate = new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date());

    // Container
    out.println("<div class='container'>");

    // Welcome section
    out.println("<div class='welcome-section'>");
    out.println("<h2>Operator Dashboard</h2>");
    out.println("<p class='date-display'>Today's date: " + currentDate + "</p>");
    out.println("<div class='operator-specific'>");
    out.println("<strong>Your Role:</strong> Transit Operator | ");
    out.println("<strong>Shift Status:</strong> Active | ");
    out.println("<strong>Break Time:</strong> <a href='#' class='break-link'>Log Break</a>");
    out.println("</div>");
    out.println("</div>");

    // Dashboard grid
    out.println("<div class='dashboard-grid'>");

    // My Assignment
    out.println("<div class='dashboard-card'>");
    out.println("<h3>My Assignment</h3>");
    out.println("<div class='stat-item'>");
    out.println("<span>Assigned Vehicles:</span>");
    out.println("<span class='stat-value'>3</span>");
    out.println("</div>");
    out.println("<div class='stat-item'>");
    out.println("<span>Today's Routes:</span>");
    out.println("<span class='stat-value'>5</span>");
    out.println("</div>");
    out.println("<div class='stat-item'>");
    out.println("<span>Completed:</span>");
    out.println("<span class='stat-value'>3</span>");
    out.println("</div>");
    out.println("<div class='stat-item'>");
    out.println("<span>Remaining:</span>");
    out.println("<span class='stat-value'>2</span>");
    out.println("</div>");
    out.println("</div>");

    // Performance
    out.println("<div class='dashboard-card'>");
    out.println("<h3>Performance</h3>");
    out.println("<div class='stat-item'>");
    out.println("<span>On-time Rate:</span>");
    out.println("<span class='stat-value'>95%</span>");
    out.println("</div>");
    out.println("<div class='stat-item'>");
    out.println("<span>Fuel Efficiency:</span>");
    out.println("<span class='stat-value'>Good</span>");
    out.println("</div>");
    out.println("<div class='stat-item'>");
    out.println("<span>Safety Score:</span>");
    out.println("<span class='stat-value'>Excellent</span>");
    out.println("</div>");
    out.println("</div>");

    // Quick Actions
    out.println("<div class='dashboard-card'>");
    out.println("<h3>Quick Actions</h3>");
    out.println("<div class='quick-actions'>");
    out.println("<a href='fuel-logs?action=add' class='action-btn'>Log Fuel Use</a>");
    out.println("<a href='gps-tracking' class='action-btn secondary'>GPS Status</a>");
    out.println("<a href='alerts' class='action-btn secondary'>View Alerts</a>");
    out.println("<a href='maintenance?action=report' class='action-btn secondary'>Report Issue</a>");
    out.println("</div>");
    out.println("</div>");

    out.println("</div>"); // End dashboard-grid

    // Available Vehicles
    out.println("<div class='vehicles-table'>");
    out.println("<h3>Available Vehicles</h3>");
    out.println("<div class='table-responsive'>");
    out.println("<table>");
    out.println("<thead>");
    out.println("<tr>");
    out.println("<th>VIN</th>");
    out.println("<th>Type</th>");
    out.println("<th>Assigned Route</th>");
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
        out.println("<td>Route 001</td>");
        out.println("<td><span class='status-assigned'>Available</span></td>");
        out.println("<td>");
        out.println("<a href='controller/vehicles?action=select&id=" + vehicle.getVehicleId()
            + "' class='btn-link'>Select</a>");
        out.println("</td>");
        out.println("</tr>");
        count++;
      }
    } else {
      out.println("<tr>");
      out.println(
          "<td colspan='5' class='no-data-cell'>No vehicles available at the moment.</td>");
      out.println("</tr>");
    }

    out.println("</tbody>");
    out.println("</table>");
    out.println("</div>");
    out.println("</div>");

    // Recent Alerts
    out.println("<div class='alerts-section'>");
    out.println("<div class='dashboard-card'>");
    out.println("<h3>Recent Alerts</h3>");
    out.println("<div class='alert-placeholder'>");
    out.println("No new alerts. All systems operating normally.");
    out.println("</div>");
    out.println("</div>");
    out.println("</div>");

    out.println("</div>"); // End container
  }

  /**
   * Generates the HTML header with external CSS references for operator
   * dashboard.
   * Sets up the HTML document structure with proper meta tags and stylesheets.
   *
   * @param out the PrintWriter for HTML output
   */
  private void generateHeader(PrintWriter out) {
    out.println("<!DOCTYPE html>");
    out.println("<html>");
    out.println("<head>");
    out.println("<meta charset='UTF-8'>");
    out.println("<title>Operator Dashboard - PTFMS</title>");
    out.println("<link rel='stylesheet' type='text/css' href='assets/styles/common.css'>");
    out.println("<link rel='stylesheet' type='text/css' href='assets/styles/operator-dashboard.css'>");
    out.println("</head>");
    out.println("<body>");
  }

  /**
   * Generates the navigation bar with operator-specific menu options.
   * Creates HTML navigation with personalized welcome message and
   * links to pages accessible by vehicle operators.
   * 
   * @param out  the PrintWriter for HTML output
   * @param user the currently authenticated operator user
   */
  private void generateNavigation(PrintWriter out, UserDTO user) {
    out.println("<div class='header'>");
    out.println("<h1>Public Transportation Fleet Management System</h1>");
    out.println("<div class='user-info'>Welcome, " + user.getUsername() + "</div>");
    out.println("</div>");

    out.println("<div class='nav-bar'>");
    out.println("<div class='nav-links'>");
    out.println("<a href='operator-dashboard'>Dashboard</a>");
    out.println("<a href='controller/vehicles'>My Vehicles</a>");
    out.println("<a href='controller/fuel-logs'>Fuel Logs</a>");
    out.println("<a href='controller/gps-tracking'>GPS Tracking</a>");
    out.println("<a href='controller/alerts'>Alerts</a>");
    out.println("</div>");
    out.println("<a href='logout' class='logout-btn'>Logout</a>");
    out.println("</div>");
  }

  /**
   * Generates the HTML footer for operator dashboard.
   * Closes the HTML document with proper closing tags.
   * 
   * @param out the PrintWriter for HTML output
   */
  private void generateFooter(PrintWriter out) {
    out.println("</body>");
    out.println("</html>");
  }
}
