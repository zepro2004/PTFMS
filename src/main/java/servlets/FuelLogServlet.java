package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import controllers.PTFMSController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import transferobjects.FuelLogDTO;
import transferobjects.UserDTO;
import transferobjects.VehicleDTO;
import utils.HeaderUtils;

/**
 * Servlet for handling fuel log operations in the PTFMS system.
 * Provides comprehensive fuel management functionality including logging,
 * editing, viewing, and analytics for fleet fuel consumption.
 */
public class FuelLogServlet extends HttpServlet {
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
   * Handles HTTP GET requests for fuel log operations.
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
      case "list" -> displayFuelLogList(request, response, user);
      case "add" -> displayAddFuelLogForm(request, response, user);
      case "edit" -> displayEditFuelLogForm(request, response, user);
      case "view" -> displayVehicleFuelLogs(request, response, user);
      case "analytics" -> displayFuelAnalytics(request, response, user);
      default -> displayFuelLogList(request, response, user);
    }
  }

  /**
   * Handles HTTP POST requests for fuel log operations.
   * Processes actions like adding, editing, and deleting fuel logs.
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
        case "add" -> handleAddFuelLog(request, response, user);
        case "edit" -> handleEditFuelLog(request, response, user);
        case "delete" -> handleDeleteFuelLog(request, response, user);
        default -> response.sendRedirect("controller/fuel-logs");
      }
    } catch (NumberFormatException e) {
      request.setAttribute("error", "Invalid number format: " + e.getMessage());
      doGet(request, response);
    } catch (Exception e) {
      request.setAttribute("error", "Error processing fuel log: " + e.getMessage());
      doGet(request, response);
    }
  }

  /**
   * Displays the main fuel log list page with vehicle summaries and recent logs.
   * Shows fuel consumption statistics for each vehicle and recent fuel entries.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayFuelLogList(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    List<FuelLogDTO> allFuelLogs = controller.getAllFuelLogs();
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    // Debug: Print fuel log data to console
    System.out.println("=== DEBUG: Fuel Log Data ===");
    System.out.println("Total fuel logs found: " + (allFuelLogs != null ? allFuelLogs.size() : "null"));
    if (allFuelLogs != null && !allFuelLogs.isEmpty()) {
      for (int i = 0; i < Math.min(5, allFuelLogs.size()); i++) {
        FuelLogDTO log = allFuelLogs.get(i);
        System.out.println("Log " + i + ": ID=" + log.getFuelLogId() +
            ", Amount=" + log.getAmount() +
            ", Cost=" + log.getCost() +
            ", Distance=" + log.getDistance());
      }
    }
    System.out.println("============================");

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Fuel Log Management", user, "fuel-logs");

      HeaderUtils.generatePageHeader(out, "Fuel Log Management",
          "<a href='controller/fuel-logs?action=analytics' class='analytics-btn'>View Analytics</a>");

      String success = (String) request.getAttribute("success");
      if (success != null) {
        out.println("<div class='success'>" + success + "</div>");
      }

      String error = (String) request.getAttribute("error");
      if (error != null) {
        out.println("<div class='error'>" + error + "</div>");
      }

      // Vehicle fuel summary cards
      out.println("<div class='fuel-summary-grid'>");

      if (vehicles != null && !vehicles.isEmpty()) {
        for (VehicleDTO vehicle : vehicles) {
          double totalFuelCost = controller.getTotalFuelCost(vehicle.getVehicleId());
          double totalFuelConsumption = controller.getTotalFuelConsumption(vehicle.getVehicleId());
          List<FuelLogDTO> vehicleFuelLogs = controller.getFuelLogsByVehicleId(vehicle.getVehicleId());

          out.println("<div class='fuel-summary-card'>");
          out.println("<h3>Vehicle " + vehicle.getVin() + "</h3>");
          out.println("<div class='vehicle-info'>");
          out.println("<p><strong>Type:</strong> " + vehicle.getMake() + " " + vehicle.getModel() + "</p>");
          out.println("<p><strong>Total Fuel Cost:</strong> $" + String.format("%.2f", totalFuelCost) + "</p>");
          out.println(
              "<p><strong>Total Consumption:</strong> " + String.format("%.2f", totalFuelConsumption) + " L</p>");
          out.println("<p><strong>Fuel Entries:</strong> " + vehicleFuelLogs.size() + "</p>");
          out.println("</div>");

          out.println("<div class='fuel-actions'>");
          out.println("<a href='controller/fuel-logs?action=view&vehicleId=" + vehicle.getVehicleId() +
              "' class='btn-small'>View Details</a>");
          out.println("<a href='controller/fuel-logs?action=add&vehicleId=" + vehicle.getVehicleId() +
              "' class='btn-small btn-primary'>Add Fuel Entry</a>");
          out.println("</div>");

          out.println("</div>");
        }
      }

      out.println("</div>"); // End fuel-summary-grid

      // Recent fuel logs table
      out.println("<div class='recent-fuel-logs'>");
      out.println("<h3>Recent Fuel Logs</h3>");

      if (allFuelLogs != null && !allFuelLogs.isEmpty()) {
        out.println("<table class='data-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Date</th>");
        out.println("<th>Vehicle</th>");
        out.println("<th>Fuel Type</th>");
        out.println("<th>Amount (L)</th>");
        out.println("<th>Cost ($)</th>");
        out.println("<th>Distance (km)</th>");
        out.println("<th>Actions</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Show last 20 entries
        int count = 0;
        for (int i = allFuelLogs.size() - 1; i >= 0 && count < 20; i--, count++) {
          FuelLogDTO fuelLog = allFuelLogs.get(i);
          VehicleDTO vehicle = controller.getVehicleById(fuelLog.getVehicleId());

          out.println("<tr>");
          out.println("<td>" + dateFormat.format(fuelLog.getLogDate()) + "</td>");
          out.println("<td>" + (vehicle != null ? vehicle.getVin() : "Unknown") + "</td>");
          out.println("<td>" + fuelLog.getFuelType() + "</td>");
          out.println(
              "<td>" + (fuelLog.getAmount() != null ? fuelLog.getAmount().setScale(2, RoundingMode.HALF_UP) : "0.00")
                  + "</td>");
          out.println("<td>$"
              + (fuelLog.getCost() != null ? fuelLog.getCost().setScale(2, RoundingMode.HALF_UP) : "0.00") + "</td>");
          out.println(
              "<td>" + (fuelLog.getDistance() != null ? fuelLog.getDistance().setScale(1, RoundingMode.HALF_UP) : "N/A")
                  + "</td>");
          out.println("<td>");
          out.println("<a href='controller/fuel-logs?action=edit&id=" + fuelLog.getFuelLogId() +
              "' class='btn-small btn-edit'>Edit</a>");
          out.println("<a href='#' onclick='deleteFuelLog(" + fuelLog.getFuelLogId() +
              ")' class='btn-small btn-delete'>Delete</a>");
          out.println("</td>");
          out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
      } else {
        out.println("<div class='no-data'>");
        out.println("<p>No fuel logs found. <a href='controller/fuel-logs?action=add'>Add your first fuel log</a></p>");
        out.println("</div>");
      }

      out.println("</div>");
      out.println("</div>");

      // JavaScript for delete confirmation
      out.println("<script>");
      out.println("function deleteFuelLog(id) {");
      out.println("  if (confirm('Are you sure you want to delete this fuel log?')) {");
      out.println("    var form = document.createElement('form');");
      out.println("    form.method = 'POST';");
      out.println("    form.action = 'controller/fuel-logs';");
      out.println("    var actionInput = document.createElement('input');");
      out.println("    actionInput.type = 'hidden';");
      out.println("    actionInput.name = 'action';");
      out.println("    actionInput.value = 'delete';");
      out.println("    form.appendChild(actionInput);");
      out.println("    var idInput = document.createElement('input');");
      out.println("    idInput.type = 'hidden';");
      out.println("    idInput.name = 'fuelLogId';");
      out.println("    idInput.value = id;");
      out.println("    form.appendChild(idInput);");
      out.println("    document.body.appendChild(form);");
      out.println("    form.submit();");
      out.println("  }");
      out.println("}");
      out.println("</script>");

      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Displays the form for adding a new fuel log entry.
   * Pre-populates the vehicle dropdown and allows entry of fuel details.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayAddFuelLogForm(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    List<VehicleDTO> vehicles = controller.getAllVehicles();
    String preselectedVehicleId = request.getParameter("vehicleId");

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Add Fuel Log", user, "fuel-logs");

      HeaderUtils.generatePageHeader(out, "Add Fuel Log Entry",
          "<a href='controller/fuel-logs' class='back-btn'>Back to Fuel Logs</a>");

      out.println("<div class='form-container'>");
      out.println("<form method='post' action='controller/fuel-logs'>");
      out.println("<input type='hidden' name='action' value='add'>");

      out.println("<div class='form-group'>");
      out.println("<label for='vehicleId'>Vehicle:</label>");
      out.println("<select id='vehicleId' name='vehicleId' required>");
      out.println("<option value=''>Select Vehicle</option>");

      if (vehicles != null) {
        for (VehicleDTO vehicle : vehicles) {
          String selected = (preselectedVehicleId != null &&
              preselectedVehicleId.equals(String.valueOf(vehicle.getVehicleId())))
                  ? " selected"
                  : "";
          out.println("<option value='" + vehicle.getVehicleId() + "'" + selected + ">" +
              vehicle.getVin() + " - " + vehicle.getMake() + " " + vehicle.getModel() + "</option>");
        }
      }

      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='logDate'>Date:</label>");
      out.println("<input type='date' id='logDate' name='logDate' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='fuelType'>Fuel Type:</label>");
      out.println("<select id='fuelType' name='fuelType' required>");
      out.println("<option value=''>Select Fuel Type</option>");
      out.println("<option value='Diesel'>Diesel</option>");
      out.println("<option value='Gasoline'>Gasoline</option>");
      out.println("<option value='Electric'>Electric</option>");
      out.println("<option value='Hybrid'>Hybrid</option>");
      out.println("<option value='Natural Gas'>Natural Gas</option>");
      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='amount'>Amount (Liters):</label>");
      out.println("<input type='number' id='amount' name='amount' step='0.01' min='0' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='cost'>Cost ($):</label>");
      out.println("<input type='number' id='cost' name='cost' step='0.01' min='0' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='distance'>Distance Traveled (km) - Optional:</label>");
      out.println("<input type='number' id='distance' name='distance' step='0.1' min='0'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='notes'>Notes (Optional):</label>");
      out.println(
          "<textarea id='notes' name='notes' rows='3' placeholder='Additional notes about this fuel entry'></textarea>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<input type='submit' value='Add Fuel Log' class='submit-btn'>");
      out.println("</div>");

      out.println("</form>");
      out.println("</div>");
      out.println("</div>");
      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Displays the form for editing an existing fuel log entry.
   * Pre-populates the form with current fuel log data for modification.
   * 
   * @param request  the HttpServletRequest object containing the fuel log ID
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayEditFuelLogForm(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String fuelLogIdStr = request.getParameter("id");
    if (fuelLogIdStr == null) {
      response.sendRedirect("controller/fuel-logs");
      return;
    }

    int fuelLogId = Integer.parseInt(fuelLogIdStr);
    FuelLogDTO fuelLog = controller.getFuelLogById(fuelLogId);
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    if (fuelLog == null) {
      response.sendRedirect("controller/fuel-logs");
      return;
    }

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Edit Fuel Log", user, "fuel-logs");

      HeaderUtils.generatePageHeader(out, "Edit Fuel Log Entry",
          "<a href='controller/fuel-logs' class='back-btn'>Back to Fuel Logs</a>");

      out.println("<div class='form-container'>");
      out.println("<form method='post' action='controller/fuel-logs'>");
      out.println("<input type='hidden' name='action' value='edit'>");
      out.println("<input type='hidden' name='fuelLogId' value='" + fuelLog.getFuelLogId() + "'>");

      out.println("<div class='form-group'>");
      out.println("<label for='vehicleId'>Vehicle:</label>");
      out.println("<select id='vehicleId' name='vehicleId' required>");

      if (vehicles != null) {
        for (VehicleDTO vehicle : vehicles) {
          String selected = (vehicle.getVehicleId() == fuelLog.getVehicleId()) ? " selected" : "";
          out.println("<option value='" + vehicle.getVehicleId() + "'" + selected + ">" +
              vehicle.getVin() + " - " + vehicle.getMake() + " " + vehicle.getModel() + "</option>");
        }
      }

      out.println("</select>");
      out.println("</div>");

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      out.println("<div class='form-group'>");
      out.println("<label for='logDate'>Date:</label>");
      out.println("<input type='date' id='logDate' name='logDate' value='" +
          dateFormat.format(fuelLog.getLogDate()) + "' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='fuelType'>Fuel Type:</label>");
      out.println("<select id='fuelType' name='fuelType' required>");
      String[] fuelTypes = { "Diesel", "Gasoline", "Electric", "Hybrid", "Natural Gas" };
      for (String type : fuelTypes) {
        String selected = type.equals(fuelLog.getFuelType()) ? " selected" : "";
        out.println("<option value='" + type + "'" + selected + ">" + type + "</option>");
      }
      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='amount'>Amount (Liters):</label>");
      out.println("<input type='number' id='amount' name='amount' step='0.01' min='0' value='" +
          fuelLog.getAmount() + "' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='cost'>Cost ($):</label>");
      out.println("<input type='number' id='cost' name='cost' step='0.01' min='0' value='" +
          fuelLog.getCost() + "' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='distance'>Distance Traveled (km) - Optional:</label>");
      out.println("<input type='number' id='distance' name='distance' step='0.1' min='0' value='" +
          (fuelLog.getDistance() != null ? fuelLog.getDistance() : "") + "'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='notes'>Notes (Optional):</label>");
      out.println("<textarea id='notes' name='notes' rows='3'>" +
          (fuelLog.getNotes() != null ? fuelLog.getNotes() : "") + "</textarea>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<input type='submit' value='Update Fuel Log' class='submit-btn'>");
      out.println("</div>");

      out.println("</form>");
      out.println("</div>");
      out.println("</div>");
      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Displays detailed fuel logs for a specific vehicle.
   * Shows all fuel entries for the selected vehicle with detailed information.
   * 
   * @param request  the HttpServletRequest object containing the vehicle ID
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayVehicleFuelLogs(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String vehicleIdStr = request.getParameter("vehicleId");
    if (vehicleIdStr == null) {
      response.sendRedirect("controller/fuel-logs");
      return;
    }

    int vehicleId = Integer.parseInt(vehicleIdStr);
    VehicleDTO vehicle = controller.getVehicleById(vehicleId);
    List<FuelLogDTO> fuelLogs = controller.getFuelLogsByVehicleId(vehicleId);

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Vehicle Fuel Logs", user, "fuel-logs");

      HeaderUtils.generatePageHeader(out, "Fuel Logs for Vehicle",
          "<a href='controller/fuel-logs' class='back-btn'>Back to All Logs</a>");

      if (vehicle != null) {
        out.println("<div class='vehicle-summary'>");
        out.println("<h3>Vehicle Information</h3>");
        out.println("<p><strong>VIN:</strong> " + vehicle.getVin() + "</p>");
        out.println("<p><strong>Type:</strong> " + vehicle.getMake() + " " + vehicle.getModel() + "</p>");
        out.println("<p><strong>Year:</strong> " + vehicle.getYear() + "</p>");

        // Calculate statistics
        double totalCost = controller.getTotalFuelCost(vehicleId);
        double totalConsumption = controller.getTotalFuelConsumption(vehicleId);
        double averageConsumption = controller.getAverageFuelConsumption(vehicleId);

        out.println("<div class='fuel-stats'>");
        out.println("<h4>Fuel Statistics</h4>");
        out.println("<p><strong>Total Fuel Cost:</strong> $" + String.format("%.2f", totalCost) + "</p>");
        out.println("<p><strong>Total Consumption:</strong> " + String.format("%.2f", totalConsumption) + " L</p>");
        out.println("<p><strong>Average per Refuel:</strong> " + String.format("%.2f", averageConsumption) + " L</p>");
        out.println("<p><strong>Total Entries:</strong> " + fuelLogs.size() + "</p>");
        out.println("</div>");

        out.println("</div>");
      }

      out.println("<div class='fuel-logs-table'>");
      out.println("<h3>Fuel Log History</h3>");
      out.println("<a href='controller/fuel-logs?action=add&vehicleId=" + vehicleId +
          "' class='add-btn'>Add New Entry</a>");

      if (fuelLogs != null && !fuelLogs.isEmpty()) {
        out.println("<table class='data-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Date</th>");
        out.println("<th>Fuel Type</th>");
        out.println("<th>Amount (L)</th>");
        out.println("<th>Cost ($)</th>");
        out.println("<th>Cost/L</th>");
        out.println("<th>Distance (km)</th>");
        out.println("<th>Notes</th>");
        out.println("<th>Actions</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (FuelLogDTO fuelLog : fuelLogs) {
          BigDecimal costPerLiter = BigDecimal.ZERO;
          if (fuelLog.getCost() != null && fuelLog.getAmount() != null
              && fuelLog.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            costPerLiter = fuelLog.getCost().divide(fuelLog.getAmount(), 2, RoundingMode.HALF_UP);
          }

          out.println("<tr>");
          out.println("<td>" + dateFormat.format(fuelLog.getLogDate()) + "</td>");
          out.println("<td>" + fuelLog.getFuelType() + "</td>");
          out.println(
              "<td>" + (fuelLog.getAmount() != null ? fuelLog.getAmount().setScale(2, RoundingMode.HALF_UP) : "0.00")
                  + "</td>");
          out.println("<td>$"
              + (fuelLog.getCost() != null ? fuelLog.getCost().setScale(2, RoundingMode.HALF_UP) : "0.00") + "</td>");
          out.println("<td>$" + costPerLiter.setScale(2, RoundingMode.HALF_UP) + "</td>");
          out.println(
              "<td>" + (fuelLog.getDistance() != null ? fuelLog.getDistance().setScale(1, RoundingMode.HALF_UP) : "N/A")
                  + "</td>");
          out.println("<td>" + (fuelLog.getNotes() != null ? fuelLog.getNotes() : "") + "</td>");
          out.println("<td>");
          out.println("<a href='controller/fuel-logs?action=edit&id=" + fuelLog.getFuelLogId() +
              "' class='btn-small btn-edit'>Edit</a>");
          out.println("</td>");
          out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
      } else {
        out.println("<div class='no-data'>");
        out.println("<p>No fuel logs found for this vehicle.</p>");
        out.println("<a href='controller/fuel-logs?action=add&vehicleId=" + vehicleId +
            "' class='btn'>Add First Entry</a>");
        out.println("</div>");
      }

      out.println("</div>");
      out.println("</div>");
      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Displays fuel consumption analytics and statistics.
   * Provides insights into fleet fuel efficiency and consumption trends.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void displayFuelAnalytics(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Fuel Analytics", user, "fuel-logs");

      HeaderUtils.generatePageHeader(out, "Fuel Analytics Dashboard",
          "<a href='controller/fuel-logs' class='back-btn'>Back to Fuel Logs</a>");

      // Fleet-wide statistics
      out.println("<div class='analytics-overview'>");
      out.println("<h3>Fleet Fuel Overview</h3>");

      double fleetTotalCost = 0;
      double fleetTotalConsumption = 0;
      int totalEntries = 0;

      if (vehicles != null) {
        for (VehicleDTO vehicle : vehicles) {
          fleetTotalCost += controller.getTotalFuelCost(vehicle.getVehicleId());
          fleetTotalConsumption += controller.getTotalFuelConsumption(vehicle.getVehicleId());
          totalEntries += controller.getFuelLogsByVehicleId(vehicle.getVehicleId()).size();
        }
      }

      out.println("<div class='analytics-grid'>");
      out.println("<div class='analytics-card'>");
      out.println("<h4>Total Fleet Fuel Cost</h4>");
      out.println("<p class='analytics-value'>$" + String.format("%.2f", fleetTotalCost) + "</p>");
      out.println("</div>");

      out.println("<div class='analytics-card'>");
      out.println("<h4>Total Fleet Consumption</h4>");
      out.println("<p class='analytics-value'>" + String.format("%.2f", fleetTotalConsumption) + " L</p>");
      out.println("</div>");

      out.println("<div class='analytics-card'>");
      out.println("<h4>Total Fuel Entries</h4>");
      out.println("<p class='analytics-value'>" + totalEntries + "</p>");
      out.println("</div>");

      out.println("<div class='analytics-card'>");
      out.println("<h4>Average Cost per Liter</h4>");
      double avgCostPerLiter = fleetTotalConsumption > 0 ? fleetTotalCost / fleetTotalConsumption : 0;
      out.println("<p class='analytics-value'>$" + String.format("%.2f", avgCostPerLiter) + "</p>");
      out.println("</div>");

      out.println("</div>"); // End analytics-grid
      out.println("</div>"); // End analytics-overview

      // Vehicle-specific analytics
      out.println("<div class='vehicle-analytics'>");
      out.println("<h3>Vehicle Fuel Performance</h3>");

      if (vehicles != null && !vehicles.isEmpty()) {
        out.println("<table class='analytics-table'>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Vehicle</th>");
        out.println("<th>Total Cost</th>");
        out.println("<th>Total Consumption (L)</th>");
        out.println("<th>Avg per Refuel (L)</th>");
        out.println("<th>Entries</th>");
        out.println("<th>Efficiency Rating</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");

        for (VehicleDTO vehicle : vehicles) {
          double totalCost = controller.getTotalFuelCost(vehicle.getVehicleId());
          double totalConsumption = controller.getTotalFuelConsumption(vehicle.getVehicleId());
          double avgConsumption = controller.getAverageFuelConsumption(vehicle.getVehicleId());
          int entries = controller.getFuelLogsByVehicleId(vehicle.getVehicleId()).size();

          String efficiencyRating = "N/A";
          if (avgConsumption > 0) {
            if (avgConsumption < 50)
              efficiencyRating = "Excellent";
            else if (avgConsumption < 80)
              efficiencyRating = "Good";
            else if (avgConsumption < 120)
              efficiencyRating = "Average";
            else
              efficiencyRating = "Poor";
          }

          out.println("<tr>");
          out.println("<td><a href='controller/fuel-logs?action=view&vehicleId=" + vehicle.getVehicleId() +
              "'>" + vehicle.getVin() + "</a></td>");
          out.println("<td>$" + String.format("%.2f", totalCost) + "</td>");
          out.println("<td>" + String.format("%.2f", totalConsumption) + "</td>");
          out.println("<td>" + String.format("%.2f", avgConsumption) + "</td>");
          out.println("<td>" + entries + "</td>");
          out.println("<td><span class='efficiency-" + efficiencyRating.toLowerCase() + "'>" +
              efficiencyRating + "</span></td>");
          out.println("</tr>");
        }

        out.println("</tbody>");
        out.println("</table>");
      } else {
        out.println("<div class='no-data'>");
        out.println("<p>No vehicles found for analytics.</p>");
        out.println("</div>");
      }

      out.println("</div>"); // End vehicle-analytics
      out.println("</div>"); // End container
      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Handles the addition of a new fuel log entry via POST request.
   * Processes form data and creates a new fuel log in the database.
   * 
   * @param request  the HttpServletRequest object containing the fuel log data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during redirect
   */
  private void handleAddFuelLog(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
      Date logDate = Date.valueOf(request.getParameter("logDate"));
      String fuelType = request.getParameter("fuelType");
      BigDecimal amount = new BigDecimal(request.getParameter("amount"));
      BigDecimal cost = new BigDecimal(request.getParameter("cost"));
      String distanceStr = request.getParameter("distance");
      String notes = request.getParameter("notes");

      FuelLogDTO fuelLog = new FuelLogDTO();
      fuelLog.setVehicleId(vehicleId);
      fuelLog.setLogDate(logDate);
      fuelLog.setFuelType(fuelType);
      fuelLog.setAmount(amount);
      fuelLog.setCost(cost);
      fuelLog.setOperatorId(user.getUserId());

      if (distanceStr != null && !distanceStr.trim().isEmpty()) {
        fuelLog.setDistance(new BigDecimal(distanceStr));
      }

      if (notes != null && !notes.trim().isEmpty()) {
        fuelLog.setNotes(notes.trim());
      }

      boolean success = controller.addFuelLog(fuelLog);

      if (success) {
        request.setAttribute("success", "Fuel log added successfully!");
      } else {
        request.setAttribute("error", "Failed to add fuel log.");
      }

    } catch (IllegalArgumentException e) {
      request.setAttribute("error", "Invalid input data: " + e.getMessage());
    }

    response.sendRedirect("controller/fuel-logs");
  }

  /**
   * Handles the editing of an existing fuel log entry via POST request.
   * Updates fuel log data in the database with new values from the form.
   * 
   * @param request  the HttpServletRequest object containing the updated fuel log
   *                 data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during redirect
   */
  private void handleEditFuelLog(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int fuelLogId = Integer.parseInt(request.getParameter("fuelLogId"));
      int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
      Date logDate = Date.valueOf(request.getParameter("logDate"));
      String fuelType = request.getParameter("fuelType");
      BigDecimal amount = new BigDecimal(request.getParameter("amount"));
      BigDecimal cost = new BigDecimal(request.getParameter("cost"));
      String distanceStr = request.getParameter("distance");
      String notes = request.getParameter("notes");

      FuelLogDTO fuelLog = new FuelLogDTO();
      fuelLog.setFuelLogId(fuelLogId);
      fuelLog.setVehicleId(vehicleId);
      fuelLog.setLogDate(logDate);
      fuelLog.setFuelType(fuelType);
      fuelLog.setAmount(amount);
      fuelLog.setCost(cost);
      fuelLog.setOperatorId(user.getUserId());

      if (distanceStr != null && !distanceStr.trim().isEmpty()) {
        fuelLog.setDistance(new BigDecimal(distanceStr));
      }

      if (notes != null && !notes.trim().isEmpty()) {
        fuelLog.setNotes(notes.trim());
      }

      boolean success = controller.updateFuelLog(fuelLog);

      if (success) {
        request.setAttribute("success", "Fuel log updated successfully!");
      } else {
        request.setAttribute("error", "Failed to update fuel log.");
      }

    } catch (IllegalArgumentException e) {
      request.setAttribute("error", "Invalid input data: " + e.getMessage());
    }

    response.sendRedirect("controller/fuel-logs");
  }

  /**
   * Handles the deletion of a fuel log entry via POST request.
   * Removes the specified fuel log from the database permanently.
   * 
   * @param request  the HttpServletRequest object containing the fuel log ID
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during redirect
   */
  private void handleDeleteFuelLog(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int fuelLogId = Integer.parseInt(request.getParameter("fuelLogId"));

      boolean success = controller.deleteFuelLog(fuelLogId);

      if (success) {
        request.setAttribute("success", "Fuel log deleted successfully!");
      } else {
        request.setAttribute("error", "Failed to delete fuel log.");
      }

    } catch (NumberFormatException e) {
      request.setAttribute("error", "Invalid fuel log ID.");
    }

    response.sendRedirect("controller/fuel-logs");
  }
}
