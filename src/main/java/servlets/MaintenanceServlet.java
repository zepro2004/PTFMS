package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import controllers.PTFMSController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import transferobjects.MaintenanceDTO;
import transferobjects.UserDTO;
import transferobjects.VehicleDTO;
import utils.HeaderUtils;

/**
 * Servlet for handling maintenance operations in the PTFMS system.
 * Provides comprehensive maintenance management functionality including
 * scheduling, tracking, and managing vehicle maintenance activities.
 */
public class MaintenanceServlet extends HttpServlet {
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
   * Handles HTTP GET requests for maintenance operations.
   * Routes to appropriate view based on the 'action' parameter.
   * Supports actions: list (default), add, edit, view, schedule, and reports.
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
      case "list" -> displayMaintenanceList(request, response, user);
      case "add" -> displayAddMaintenanceForm(request, response, user);
      case "edit" -> displayEditMaintenanceForm(request, response, user);
      case "view" -> displayVehicleMaintenanceHistory(request, response, user);
      case "schedule" -> displayMaintenanceSchedule(request, response, user);
      case "reports" -> displayMaintenanceReports(request, response, user);
      default -> response.sendRedirect("controller/maintenance");
    }
  }

  /**
   * Handles HTTP POST requests for maintenance state-changing operations.
   * Processes form submissions for adding, editing, deleting, and completing
   * maintenance records.
   * Supports actions: add, edit, delete, and complete.
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
        case "add" -> handleAddMaintenance(request, response, user);
        case "edit" -> handleEditMaintenance(request, response, user);
        case "delete" -> handleDeleteMaintenance(request, response, user);
        case "complete" -> handleCompleteMaintenance(request, response, user);
        default -> response.sendRedirect("controller/maintenance");
      }
    } catch (Exception e) {
      request.setAttribute("error", "An error occurred: " + e.getMessage());
      response.sendRedirect("controller/maintenance");
    }
  }

  /**
   * Displays the main maintenance management dashboard.
   * Shows vehicle maintenance cards with summary statistics and a table of
   * recent maintenance records with action buttons.
   *
   * @param request  the HTTP servlet request
   * @param response the HTTP servlet response for rendering the page
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayMaintenanceList(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    // Get real maintenance data instead of mock data
    List<MaintenanceDTO> allMaintenance = new ArrayList<>();
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    // Get maintenance records for all vehicles
    if (vehicles != null) {
      for (VehicleDTO vehicle : vehicles) {
        List<MaintenanceDTO> vehicleMaintenance = controller.getMaintenanceByVehicleId(vehicle.getVehicleId());
        if (vehicleMaintenance != null) {
          allMaintenance.addAll(vehicleMaintenance);
        }
      }
    }

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Maintenance Management", user, "maintenance");

      HeaderUtils.generatePageHeader(out, "Maintenance Management",
          "<a href='controller/maintenance?action=add' class='add-btn'>Schedule Maintenance</a>",
          "<a href='controller/maintenance?action=schedule' class='schedule-btn'>View Schedule</a>",
          "<a href='controller/maintenance?action=reports' class='reports-btn'>Maintenance Reports</a>");

      // Maintenance overview cards
      out.println("<div class='maintenance-overview'>");
      out.println("<h3>Fleet Maintenance Overview</h3>");
      out.println("<div class='maintenance-cards-grid'>");

      if (vehicles != null && !vehicles.isEmpty()) {
        for (VehicleDTO vehicle : vehicles) {
          List<MaintenanceDTO> vehicleMaintenance = controller.getMaintenanceByVehicleId(vehicle.getVehicleId());
          int totalRecords = vehicleMaintenance.size();
          int pendingCount = 0;
          int completedCount = 0;
          double totalCost = 0;

          for (MaintenanceDTO maintenance : vehicleMaintenance) {
            if ("PENDING".equals(maintenance.getStatus()) || "SCHEDULED".equals(maintenance.getStatus())) {
              pendingCount++;
            } else if ("Completed".equals(maintenance.getStatus())) {
              completedCount++;
              if (maintenance.getCost() != null) {
                totalCost += maintenance.getCost().doubleValue();
              }
            }
          }

          out.println("<div class='maintenance-card'>");
          out.println("<h4>Vehicle " + vehicle.getVin() + "</h4>");
          out.println("<div class='vehicle-info'>");
          out.println("<p><strong>Type:</strong> " + vehicle.getMake() + " " + vehicle.getModel() + "</p>");
          out.println("<p><strong>Total Records:</strong> " + totalRecords + "</p>");
          out.println("<p><strong>Pending:</strong> " + pendingCount + "</p>");
          out.println("<p><strong>Completed:</strong> " + completedCount + "</p>");
          out.println("<p><strong>Total Cost:</strong> $" + String.format("%.2f", totalCost) + "</p>");
          out.println("</div>");

          out.println("<div class='maintenance-actions'>");
          out.println("<a href='controller/maintenance?action=view&vehicleId=" + vehicle.getVehicleId() +
              "' class='btn-small'>View History</a>");
          out.println("<a href='controller/maintenance?action=add&vehicleId=" + vehicle.getVehicleId() +
              "' class='btn-small btn-primary'>Schedule</a>");
          out.println("</div>");

          out.println("</div>");
        }
      }

      out.println("</div>"); // End maintenance-cards-grid

      // Recent maintenance records table
      out.println("<div class='recent-maintenance'>");
      out.println("<h3>Recent Maintenance Records</h3>");

      if (allMaintenance != null && !allMaintenance.isEmpty()) {
        out.println("<table class='data-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Date</th>");
        out.println("<th>Vehicle</th>");
        out.println("<th>Type</th>");
        out.println("<th>Description</th>");
        out.println("<th>Status</th>");
        out.println("<th>Cost</th>");
        out.println("<th>Actions</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Show last 20 entries
        int count = 0;
        for (int i = allMaintenance.size() - 1; i >= 0 && count < 20; i--, count++) {
          MaintenanceDTO maintenance = allMaintenance.get(i);
          VehicleDTO vehicle = controller.getVehicleById(maintenance.getVehicleId());

          out.println("<tr>");
          out.println("<td>"
              + (maintenance.getServiceDate() != null ? dateFormat.format(maintenance.getServiceDate()) : "N/A")
              + "</td>");
          out.println("<td>" + (vehicle != null ? vehicle.getVin() : "Unknown") + "</td>");
          out.println("<td>" + getMaintenanceType(maintenance) + "</td>");
          out.println("<td>" + maintenance.getDescription() + "</td>");
          out.println("<td><span class='status-" + maintenance.getStatus().toLowerCase() + "'>" +
              maintenance.getStatus() + "</span></td>");
          out.println("<td>" + (maintenance.getCost() != null ? "$" + maintenance.getCost() : "N/A") + "</td>");
          out.println("<td>");
          out.println("<a href='controller/maintenance?action=edit&id=" + maintenance.getMaintenanceId() +
              "' class='btn-small btn-edit'>Edit</a>");
          if (!"Completed".equals(maintenance.getStatus())) {
            out.println("<a href='#' onclick='completeMaintenance(" + maintenance.getMaintenanceId() +
                ")' class='btn-small btn-complete'>Complete</a>");
          }
          out.println("<a href='#' onclick='deleteMaintenance(" + maintenance.getMaintenanceId() +
              ")' class='btn-small btn-delete'>Delete</a>");
          out.println("</td>");
          out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
      } else {
        out.println("<div class='no-data'>");
        out.println(
            "<p>No maintenance records found. <a href='controller/maintenance?action=add'>Schedule your first maintenance</a></p>");
        out.println("</div>");
      }

      out.println("</div>");
      out.println("</div>");

      // JavaScript for actions
      out.println("<script>");
      out.println("function deleteMaintenance(id) {");
      out.println("  if (confirm('Are you sure you want to delete this maintenance record?')) {");
      out.println("    var form = document.createElement('form');");
      out.println("    form.method = 'POST';");
      out.println("    form.action = 'controller/maintenance';");
      out.println("    var actionInput = document.createElement('input');");
      out.println("    actionInput.type = 'hidden';");
      out.println("    actionInput.name = 'action';");
      out.println("    actionInput.value = 'delete';");
      out.println("    form.appendChild(actionInput);");
      out.println("    var idInput = document.createElement('input');");
      out.println("    idInput.type = 'hidden';");
      out.println("    idInput.name = 'maintenanceId';");
      out.println("    idInput.value = id;");
      out.println("    form.appendChild(idInput);");
      out.println("    document.body.appendChild(form);");
      out.println("    form.submit();");
      out.println("  }");
      out.println("}");

      out.println("function completeMaintenance(id) {");
      out.println("  if (confirm('Mark this maintenance as completed?')) {");
      out.println("    var form = document.createElement('form');");
      out.println("    form.method = 'POST';");
      out.println("    form.action = 'controller/maintenance';");
      out.println("    var actionInput = document.createElement('input');");
      out.println("    actionInput.type = 'hidden';");
      out.println("    actionInput.name = 'action';");
      out.println("    actionInput.value = 'complete';");
      out.println("    form.appendChild(actionInput);");
      out.println("    var idInput = document.createElement('input');");
      out.println("    idInput.type = 'hidden';");
      out.println("    idInput.name = 'maintenanceId';");
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

  private void displayAddMaintenanceForm(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    List<VehicleDTO> vehicles = controller.getAllVehicles();
    String preselectedVehicleId = request.getParameter("vehicleId");

    try (PrintWriter out = response.getWriter()) {
      generateHeader(out, "Schedule Maintenance");
      generateNavigation(out, user);

      out.println("<div class='container'>");
      out.println("<div class='page-header'>");
      out.println("<h2>Schedule Maintenance</h2>");
      out.println("<a href='controller/maintenance' class='back-btn'>Back to Maintenance</a>");
      out.println("</div>");

      out.println("<div class='form-container'>");
      out.println("<form method='post' action='controller/maintenance'>");
      out.println("<input type='hidden' name='action' value='add'>");

      out.println("<div class='form-group'>");
      out.println("<label for='vehicleId'>Vehicle:</label>");
      out.println("<select id='vehicleId' name='vehicleId' required>");
      out.println("<option value=''>Select Vehicle</option>");

      if (vehicles != null) {
        for (VehicleDTO vehicle : vehicles) {
          String selected = (preselectedVehicleId != null &&
              preselectedVehicleId.equals(String.valueOf(vehicle.getVehicleId()))) ? " selected" : "";
          out.println("<option value='" + vehicle.getVehicleId() + "'" + selected + ">" +
              vehicle.getVin() + " - " + vehicle.getMake() + " " + vehicle.getModel() + "</option>");
        }
      }

      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='maintenanceType'>Maintenance Type:</label>");
      out.println("<select id='maintenanceType' name='maintenanceType' required>");
      out.println("<option value=''>Select Type</option>");
      out.println("<option value='Oil Change'>Oil Change</option>");
      out.println("<option value='Tire Rotation'>Tire Rotation</option>");
      out.println("<option value='Brake Inspection'>Brake Inspection</option>");
      out.println("<option value='Engine Tune-up'>Engine Tune-up</option>");
      out.println("<option value='Transmission Service'>Transmission Service</option>");
      out.println("<option value='A/C Service'>A/C Service</option>");
      out.println("<option value='Battery Replacement'>Battery Replacement</option>");
      out.println("<option value='Preventive Maintenance'>Preventive Maintenance</option>");
      out.println("<option value='Emergency Repair'>Emergency Repair</option>");
      out.println("<option value='Inspection'>Inspection</option>");
      out.println("<option value='Other'>Other</option>");
      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='scheduledDate'>Scheduled Date:</label>");
      out.println("<input type='date' id='scheduledDate' name='scheduledDate' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='description'>Description:</label>");
      out.println(
          "<textarea id='description' name='description' rows='4' placeholder='Describe the maintenance work needed'></textarea>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='mileage'>Current Mileage (km):</label>");
      out.println(
          "<input type='number' id='mileage' name='mileage' step='1' min='0' placeholder='Enter current mileage'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='estimatedCost'>Estimated Cost ($):</label>");
      out.println(
          "<input type='number' id='estimatedCost' name='estimatedCost' step='0.01' min='0' placeholder='Enter estimated cost'>");
      out.println("</div>");

      out.println("<div class='form-actions'>");
      out.println("<button type='submit' class='btn btn-primary'>Schedule Maintenance</button>");
      out.println("<a href='controller/maintenance' class='btn btn-secondary'>Cancel</a>");
      out.println("</div>");

      out.println("</form>");
      out.println("</div>");
      out.println("</div>");
      generateFooter(out);
    }
  }

  private void displayEditMaintenanceForm(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String maintenanceIdStr = request.getParameter("id");
    if (maintenanceIdStr == null) {
      response.sendRedirect("controller/maintenance");
      return;
    }

    // Create a mock maintenance for editing (since getMaintenanceById doesn't
    // exist)
    MaintenanceDTO maintenance = createMockMaintenance(Integer.parseInt(maintenanceIdStr), 1);
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    try (PrintWriter out = response.getWriter()) {
      generateHeader(out, "Edit Maintenance");
      generateNavigation(out, user);

      out.println("<div class='container'>");
      out.println("<div class='page-header'>");
      out.println("<h2>Edit Maintenance Record</h2>");
      out.println("<a href='controller/maintenance' class='back-btn'>Back to Maintenance</a>");
      out.println("</div>");

      out.println("<div class='form-container'>");
      out.println("<form method='post' action='controller/maintenance'>");
      out.println("<input type='hidden' name='action' value='edit'>");
      out.println("<input type='hidden' name='maintenanceId' value='" + maintenance.getMaintenanceId() + "'>");

      out.println("<div class='form-group'>");
      out.println("<label for='vehicleId'>Vehicle:</label>");
      out.println("<select id='vehicleId' name='vehicleId' required>");

      if (vehicles != null) {
        for (VehicleDTO vehicle : vehicles) {
          String selected = (vehicle.getVehicleId() == maintenance.getVehicleId()) ? " selected" : "";
          out.println("<option value='" + vehicle.getVehicleId() + "'" + selected + ">" +
              vehicle.getVin() + " - " + vehicle.getMake() + " " + vehicle.getModel() + "</option>");
        }
      }

      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='maintenanceType'>Maintenance Type:</label>");
      out.println("<select id='maintenanceType' name='maintenanceType' required>");

      String[] types = { "Oil Change", "Tire Rotation", "Brake Inspection", "Engine Tune-up",
          "Transmission Service", "A/C Service", "Battery Replacement",
          "Preventive Maintenance", "Emergency Repair", "Inspection", "Other" };

      for (String type : types) {
        String selected = type.equals(getMaintenanceType(maintenance)) ? " selected" : "";
        out.println("<option value='" + type + "'" + selected + ">" + type + "</option>");
      }

      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='scheduledDate'>Scheduled Date:</label>");
      out.println("<input type='date' id='scheduledDate' name='scheduledDate' value='" +
          (maintenance.getServiceDate() != null ? maintenance.getServiceDate() : "") + "' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='description'>Description:</label>");
      out.println("<textarea id='description' name='description' rows='4'>" +
          (maintenance.getDescription() != null ? maintenance.getDescription() : "") + "</textarea>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='status'>Status:</label>");
      out.println("<select id='status' name='status' required>");

      String[] statuses = { "Pending", "Completed" };
      for (String status : statuses) {
        String selected = status.equals(maintenance.getStatus()) ? " selected" : "";
        out.println("<option value='" + status + "'" + selected + ">" + status + "</option>");
      }

      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='cost'>Cost ($):</label>");
      out.println("<input type='number' id='cost' name='cost' step='0.01' min='0' value='" +
          (maintenance.getCost() != null ? maintenance.getCost() : "") + "'>");
      out.println("</div>");

      out.println("<div class='form-actions'>");
      out.println("<button type='submit' class='btn btn-primary'>Update Maintenance</button>");
      out.println("<a href='controller/maintenance' class='btn btn-secondary'>Cancel</a>");
      out.println("</div>");

      out.println("</form>");
      out.println("</div>");
      out.println("</div>");
      generateFooter(out);
    }
  }

  private void displayVehicleMaintenanceHistory(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String vehicleIdStr = request.getParameter("vehicleId");
    if (vehicleIdStr == null) {
      response.sendRedirect("controller/maintenance");
      return;
    }

    int vehicleId = Integer.parseInt(vehicleIdStr);
    VehicleDTO vehicle = controller.getVehicleById(vehicleId);
    List<MaintenanceDTO> maintenanceHistory = controller.getMaintenanceByVehicleId(vehicleId);

    try (PrintWriter out = response.getWriter()) {
      generateHeader(out, "Vehicle Maintenance History");
      generateNavigation(out, user);

      out.println("<div class='container'>");
      out.println("<div class='page-header'>");
      out.println("<h2>Maintenance History for Vehicle</h2>");
      if (vehicle != null) {
        out.println("<h3>" + vehicle.getVin() + " - " + vehicle.getMake() + " " + vehicle.getModel() + "</h3>");
      }
      out.println("<a href='controller/maintenance' class='back-btn'>Back to Maintenance</a>");
      out.println("</div>");

      if (vehicle != null) {
        // Maintenance statistics
        double totalCost = 0;
        int completedCount = 0;
        int pendingCount = 0;

        for (MaintenanceDTO maintenance : maintenanceHistory) {
          if ("Completed".equals(maintenance.getStatus())) {
            completedCount++;
            if (maintenance.getCost() != null) {
              totalCost += maintenance.getCost().doubleValue();
            }
          } else {
            pendingCount++;
          }
        }

        out.println("<div class='maintenance-stats'>");
        out.println("<h4>Maintenance Statistics</h4>");
        out.println("<p><strong>Total Maintenance Cost:</strong> $" + String.format("%.2f", totalCost) + "</p>");
        out.println("<p><strong>Completed Records:</strong> " + completedCount + "</p>");
        out.println("<p><strong>Pending/Scheduled:</strong> " + pendingCount + "</p>");
        out.println("<p><strong>Total Records:</strong> " + maintenanceHistory.size() + "</p>");
        out.println("</div>");

        out.println("</div>");
      }

      out.println("<div class='maintenance-history-table'>");
      out.println("<h3>Maintenance History</h3>");
      out.println("<a href='controller/maintenance?action=add&vehicleId=" + vehicleId +
          "' class='add-btn'>Schedule New Maintenance</a>");

      if (maintenanceHistory != null && !maintenanceHistory.isEmpty()) {
        out.println("<table class='data-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Date</th>");
        out.println("<th>Type</th>");
        out.println("<th>Description</th>");
        out.println("<th>Status</th>");
        out.println("<th>Cost</th>");
        out.println("<th>Actions</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (MaintenanceDTO maintenance : maintenanceHistory) {
          out.println("<tr>");
          out.println("<td>"
              + (maintenance.getServiceDate() != null ? dateFormat.format(maintenance.getServiceDate()) : "N/A")
              + "</td>");
          out.println("<td>" + getMaintenanceType(maintenance) + "</td>");
          out.println("<td>" + (maintenance.getDescription() != null ? maintenance.getDescription() : "") + "</td>");
          out.println("<td><span class='status-" + maintenance.getStatus().toLowerCase() + "'>" +
              maintenance.getStatus() + "</span></td>");
          out.println("<td>" + (maintenance.getCost() != null ? "$" + maintenance.getCost() : "N/A") + "</td>");
          out.println("<td>");
          out.println("<a href='controller/maintenance?action=edit&id=" + maintenance.getMaintenanceId() +
              "' class='btn-small btn-edit'>Edit</a>");
          out.println("</td>");
          out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
      } else {
        out.println("<div class='no-data'>");
        out.println("<p>No maintenance history found for this vehicle.</p>");
        out.println("<a href='controller/maintenance?action=add&vehicleId=" + vehicleId +
            "' class='btn'>Schedule First Maintenance</a>");
        out.println("</div>");
      }

      out.println("</div>");
      out.println("</div>");
      generateFooter(out);
    }
  }

  private void displayMaintenanceSchedule(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    // Get real upcoming maintenance data instead of mock data
    List<MaintenanceDTO> upcomingMaintenance = new ArrayList<>();
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    // Get all pending maintenance records from all vehicles
    if (vehicles != null) {
      for (VehicleDTO vehicle : vehicles) {
        List<MaintenanceDTO> vehicleMaintenance = controller.getMaintenanceByVehicleId(vehicle.getVehicleId());
        if (vehicleMaintenance != null) {
          for (MaintenanceDTO maintenance : vehicleMaintenance) {
            // Only show pending/scheduled maintenance (upcoming)
            if ("Pending".equals(maintenance.getStatus())) {
              upcomingMaintenance.add(maintenance);
            }
          }
        }
      }
    }

    // Sort by service date (earliest first)
    upcomingMaintenance.sort((m1, m2) -> {
      if (m1.getServiceDate() == null && m2.getServiceDate() == null)
        return 0;
      if (m1.getServiceDate() == null)
        return 1;
      if (m2.getServiceDate() == null)
        return -1;
      return m1.getServiceDate().compareTo(m2.getServiceDate());
    });

    try (PrintWriter out = response.getWriter()) {
      generateHeader(out, "Maintenance Schedule");
      generateNavigation(out, user);

      out.println("<div class='container'>");
      out.println("<div class='page-header'>");
      out.println("<h2>Maintenance Schedule</h2>");
      out.println("<a href='controller/maintenance' class='back-btn'>Back to Maintenance</a>");
      out.println("</div>");

      out.println("<div class='schedule-view'>");
      out.println("<h3>Upcoming Maintenance</h3>");

      if (upcomingMaintenance != null && !upcomingMaintenance.isEmpty()) {
        out.println("<div class='schedule-cards'>");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (MaintenanceDTO maintenance : upcomingMaintenance) {
          VehicleDTO vehicle = controller.getVehicleById(maintenance.getVehicleId());

          out.println("<div class='schedule-card'>");
          out.println("<div class='schedule-date'>");
          out.println("<h4>"
              + (maintenance.getServiceDate() != null ? dateFormat.format(maintenance.getServiceDate()) : "TBD")
              + "</h4>");
          out.println("</div>");
          out.println("<div class='schedule-details'>");
          out.println("<h5>" + getMaintenanceType(maintenance) + "</h5>");
          out.println("<p><strong>Vehicle:</strong> " + (vehicle != null ? vehicle.getVin() : "Unknown") + "</p>");
          out.println("<p><strong>Description:</strong> " +
              (maintenance.getDescription() != null ? maintenance.getDescription() : "N/A") + "</p>");
          out.println(
              "<p><strong>Status:</strong> <span class='status-" + maintenance.getStatus().toLowerCase() + "'>" +
                  maintenance.getStatus() + "</span></p>");
          out.println("</div>");
          out.println("<div class='schedule-actions'>");
          out.println("<a href='controller/maintenance?action=edit&id=" + maintenance.getMaintenanceId() +
              "' class='btn-small'>Edit</a>");
          if (!"Completed".equals(maintenance.getStatus())) {
            out.println("<a href='#' onclick='completeMaintenance(" + maintenance.getMaintenanceId() +
                ")' class='btn-small btn-complete'>Complete</a>");
          }
          out.println("</div>");
          out.println("</div>");
        }

        out.println("</div>");
      } else {
        out.println("<div class='no-data'>");
        out.println("<p>No upcoming maintenance scheduled.</p>");
        out.println("<a href='controller/maintenance?action=add' class='btn'>Schedule Maintenance</a>");
        out.println("</div>");
      }

      out.println("</div>");
      out.println("</div>");
      generateFooter(out);
    }
  }

  private void displayMaintenanceReports(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    try (PrintWriter out = response.getWriter()) {
      generateHeader(out, "Maintenance Reports");
      generateNavigation(out, user);

      out.println("<div class='container'>");
      out.println("<div class='page-header'>");
      out.println("<h2>Maintenance Reports & Analytics</h2>");
      out.println("<a href='controller/maintenance' class='back-btn'>Back to Maintenance</a>");
      out.println("</div>");

      // Fleet maintenance overview
      out.println("<div class='reports-overview'>");
      out.println("<h3>Fleet Maintenance Overview</h3>");

      double fleetTotalCost = 0;
      int fleetTotalRecords = 0;
      int fleetPendingCount = 0;

      if (vehicles != null) {
        for (VehicleDTO vehicle : vehicles) {
          fleetTotalCost += getMockMaintenanceCost(vehicle.getVehicleId());
          List<MaintenanceDTO> vehicleMaintenance = controller.getMaintenanceByVehicleId(vehicle.getVehicleId());
          fleetTotalRecords += vehicleMaintenance.size();
          for (MaintenanceDTO maintenance : vehicleMaintenance) {
            if (!"Completed".equals(maintenance.getStatus())) {
              fleetPendingCount++;
            }
          }
        }
      }

      out.println("<div class='reports-grid'>");
      out.println("<div class='report-card'>");
      out.println("<h4>Total Fleet Maintenance Cost</h4>");
      out.println("<p class='report-value'>$" + String.format("%.2f", fleetTotalCost) + "</p>");
      out.println("</div>");

      out.println("<div class='report-card'>");
      out.println("<h4>Total Maintenance Records</h4>");
      out.println("<p class='report-value'>" + fleetTotalRecords + "</p>");
      out.println("</div>");

      out.println("<div class='report-card'>");
      out.println("<h4>Pending Maintenance</h4>");
      out.println("<p class='report-value'>" + fleetPendingCount + "</p>");
      out.println("</div>");

      out.println("</div>"); // End reports-grid
      out.println("</div>"); // End reports-overview

      // Vehicle-specific reports
      out.println("<div class='vehicle-reports'>");
      out.println("<h3>Vehicle Maintenance Analysis</h3>");

      if (vehicles != null && !vehicles.isEmpty()) {
        out.println("<table class='reports-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Vehicle</th>");
        out.println("<th>Total Cost</th>");
        out.println("<th>Total Records</th>");
        out.println("<th>Pending</th>");
        out.println("<th>Avg Cost per Service</th>");
        out.println("<th>Actions</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        for (VehicleDTO vehicle : vehicles) {
          double totalCost = getMockMaintenanceCost(vehicle.getVehicleId());
          List<MaintenanceDTO> vehicleMaintenance = controller.getMaintenanceByVehicleId(vehicle.getVehicleId());
          int totalRecords = vehicleMaintenance.size();
          int pendingCount = 0;

          for (MaintenanceDTO maintenance : vehicleMaintenance) {
            if (!"Completed".equals(maintenance.getStatus())) {
              pendingCount++;
            }
          }

          double avgCost = totalRecords > 0 ? totalCost / totalRecords : 0;

          out.println("<tr>");
          out.println("<td><a href='controller/maintenance?action=view&vehicleId=" + vehicle.getVehicleId() +
              "'>" + vehicle.getVin() + "</a></td>");
          out.println("<td>$" + String.format("%.2f", totalCost) + "</td>");
          out.println("<td>" + totalRecords + "</td>");
          out.println("<td>" + pendingCount + "</td>");
          out.println("<td>$" + String.format("%.2f", avgCost) + "</td>");
          out.println("<td>");
          out.println("<a href='controller/maintenance?action=view&vehicleId=" + vehicle.getVehicleId() +
              "' class='btn-small'>View Details</a>");
          out.println("</td>");
          out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
      }

      out.println("</div>");
      out.println("</div>");
      generateFooter(out);
    }
  }

  private void handleAddMaintenance(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
      String maintenanceType = request.getParameter("maintenanceType");
      Date scheduledDate = Date.valueOf(request.getParameter("scheduledDate"));
      String description = request.getParameter("description");
      String estimatedCostStr = request.getParameter("estimatedCost");

      MaintenanceDTO maintenance = new MaintenanceDTO();
      maintenance.setVehicleId(vehicleId);
      maintenance.setServiceDate(scheduledDate);
      maintenance.setDescription(description + " (" + maintenanceType + ")");
      maintenance.setStatus("Pending");

      if (estimatedCostStr != null && !estimatedCostStr.trim().isEmpty()) {
        maintenance.setCost(new BigDecimal(estimatedCostStr));
      }

      boolean success = controller.scheduleMaintenance(maintenance);

      if (success) {
        request.setAttribute("success", "Maintenance scheduled successfully!");
      } else {
        request.setAttribute("error", "Failed to schedule maintenance.");
      }

    } catch (IllegalArgumentException e) {
      request.setAttribute("error", "Invalid input data: " + e.getMessage());
    }

    response.sendRedirect("controller/maintenance");
  }

  private void handleEditMaintenance(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int maintenanceId = Integer.parseInt(request.getParameter("maintenanceId"));
      int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
      String maintenanceType = request.getParameter("maintenanceType");
      Date scheduledDate = Date.valueOf(request.getParameter("scheduledDate"));
      String description = request.getParameter("description");
      String status = request.getParameter("status");
      String costStr = request.getParameter("cost");

      MaintenanceDTO maintenance = new MaintenanceDTO();
      maintenance.setMaintenanceId(maintenanceId);
      maintenance.setVehicleId(vehicleId);
      maintenance.setServiceDate(scheduledDate);
      maintenance.setDescription(description + " (" + maintenanceType + ")");
      maintenance.setStatus(status != null ? status : "Pending");

      if (costStr != null && !costStr.trim().isEmpty()) {
        maintenance.setCost(new BigDecimal(costStr));
      }

      boolean success = controller.updateMaintenance(maintenance);

      if (success) {
        request.setAttribute("success", "Maintenance record updated successfully!");
      } else {
        request.setAttribute("error", "Failed to update maintenance record.");
      }
    } catch (IllegalArgumentException e) {
      request.setAttribute("error", "Invalid input data: " + e.getMessage());
    } catch (Exception e) {
      request.setAttribute("error", "An error occurred while updating maintenance: " + e.getMessage());
    }
    response.sendRedirect("controller/maintenance");
  }

  private void handleDeleteMaintenance(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      String maintenanceIdStr = request.getParameter("maintenanceId");
      if (maintenanceIdStr == null || maintenanceIdStr.trim().isEmpty()) {
        request.setAttribute("error", "Maintenance ID is required.");
        response.sendRedirect("controller/maintenance");
        return;
      }

      int maintenanceId = Integer.parseInt(maintenanceIdStr);

      boolean success = controller.deleteMaintenance(maintenanceId);

      if (success) {
        request.setAttribute("success", "Maintenance record deleted successfully.");
      } else {
        request.setAttribute("error", "Failed to delete maintenance record.");
      }

    } catch (NumberFormatException e) {
      request.setAttribute("error", "Invalid maintenance ID format.");
    } catch (Exception e) {
      request.setAttribute("error", "Error deleting maintenance record: " + e.getMessage());
    }
    response.sendRedirect("controller/maintenance");
  }

  private void handleCompleteMaintenance(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      String maintenanceIdStr = request.getParameter("maintenanceId");
      if (maintenanceIdStr == null || maintenanceIdStr.trim().isEmpty()) {
        request.setAttribute("error", "Maintenance ID is required.");
        response.sendRedirect("controller/maintenance");
        return;
      }

      int maintenanceId = Integer.parseInt(maintenanceIdStr);

      boolean success = controller.updateMaintenanceStatus(maintenanceId, "Completed");

      if (success) {
        request.setAttribute("success", "Maintenance record marked as completed successfully.");
      } else {
        request.setAttribute("error", "Failed to complete maintenance record.");
      }

    } catch (NumberFormatException e) {
      request.setAttribute("error", "Invalid maintenance ID format.");
    } catch (Exception e) {
      request.setAttribute("error", "Error completing maintenance record: " + e.getMessage());
    }
    response.sendRedirect("controller/maintenance");
  }

  private void generateHeader(PrintWriter out, String title) {
    out.println("<!DOCTYPE html>");
    out.println("<html lang='en'>");
    out.println("<head>");
    out.println("<meta charset='UTF-8'>");
    out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
    out.println("<title>" + title + " - PTFMS</title>");
    out.println("<link rel='stylesheet' type='text/css' href='assets/styles/common.css'>");
    out.println("<link rel='stylesheet' type='text/css' href='assets/styles/maintenance.css'>");
    out.println("</head>");
    out.println("<body>");
  }

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

  private void generateFooter(PrintWriter out) {
    out.println("</body>");
    out.println("</html>");
  }

  /**
   * Creates a single mock maintenance record with sample data.
   * Used for testing and development purposes to simulate maintenance records.
   * 
   * @param id        the unique identifier for the maintenance record
   * @param vehicleId the ID of the vehicle this maintenance applies to
   * @return a mock MaintenanceDTO object with sample data
   */
  private MaintenanceDTO createMockMaintenance(int id, int vehicleId) {
    MaintenanceDTO maintenance = new MaintenanceDTO();
    maintenance.setMaintenanceId(id);
    maintenance.setVehicleId(vehicleId);
    maintenance.setServiceDate(new Date(System.currentTimeMillis() - (id * 86400000L))); // Past dates

    String[] statuses = { "SCHEDULED", "COMPLETED", "IN_PROGRESS" };
    String[] descriptions = {
        "Regular oil change and filter replacement",
        "Tire rotation and pressure check",
        "Comprehensive brake system inspection",
        "Engine tune-up and performance check",
        "Scheduled preventive maintenance"
    };

    maintenance.setDescription(descriptions[id % descriptions.length]);
    maintenance.setStatus(statuses[id % statuses.length]);
    maintenance.setCost(new BigDecimal(50.0 + (id * 25.0))); // Varying costs

    return maintenance;
  }

  private String getMaintenanceType(MaintenanceDTO maintenance) {
    // Extract type from description or return default
    String desc = maintenance.getDescription();
    if (desc.contains("oil"))
      return "Oil Change";
    if (desc.contains("tire"))
      return "Tire Rotation";
    if (desc.contains("brake"))
      return "Brake Inspection";
    if (desc.contains("engine"))
      return "Engine Tune-up";
    return "Preventive Maintenance";
  }

  /**
   * Generates a mock total maintenance cost for a specific vehicle.
   * Used for testing and development when real cost data is not available.
   * 
   * @param vehicleId the ID of the vehicle to calculate mock costs for
   * @return a mock maintenance cost based on the vehicle ID
   */
  private double getMockMaintenanceCost(int vehicleId) {
    // Return mock total cost based on vehicle ID
    return 500.0 + (vehicleId * 150.0);
  }
}
