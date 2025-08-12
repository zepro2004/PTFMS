package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import controllers.PTFMSController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import transferobjects.UserDTO;
import transferobjects.VehicleDTO;
import utils.HeaderUtils;

/**
 * Servlet for handling vehicle management operations in the PTFMS system.
 * Provides comprehensive vehicle management functionality including
 * listing, adding, editing, viewing, and deleting vehicles.
 */
public class VehicleServlet extends HttpServlet {
  private PTFMSController controller;

  /**
   * Initializes the servlet by creating an instance of PTFMSController.
   * Called once when the servlet is first loaded.
   * 
   * @throws ServletException if an error occurs during servlet initialization
   */
  @Override
  public void init() throws ServletException {
    try {
      System.out.println("VehicleServlet: Initializing servlet...");
      controller = new PTFMSController();
      System.out.println("VehicleServlet: Successfully initialized PTFMSController");
    } catch (Exception e) {
      System.err.println("VehicleServlet: Failed to initialize PTFMSController - " + e.getMessage());
      e.printStackTrace();
      throw new ServletException("Failed to initialize PTFMSController", e);
    }
  }

  /**
   * Handles HTTP GET requests for vehicle operations.
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
      case "add":
        generateAddVehicleForm(request, response, user);
        break;
      case "view":
        generateVehicleView(request, response, user);
        break;
      case "edit":
        generateEditVehicleForm(request, response, user);
        break;
      case "delete":
        handleDeleteVehicle(request, response, user);
        break;
      case "select":
        handleSelectVehicle(request, response, user);
        break;
      default:
        generateVehicleList(request, response, user);
        break;
    }
  }

  /**
   * Handles HTTP POST requests for vehicle operations.
   * Processes actions like adding and editing vehicles.
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
        case "add":
          handleAddVehicle(request, response, user);
          break;
        case "edit":
          handleEditVehicle(request, response, user);
          break;
        default:
          response.sendRedirect("controller/vehicles");
          break;
      }
    } catch (Exception e) {
      request.setAttribute("error", "Error processing vehicle operation: " + e.getMessage());
      doGet(request, response);
    }
  }

  /**
   * Generates and displays the vehicle list page.
   * Shows all vehicles in the fleet with their details and action buttons.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void generateVehicleList(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    List<VehicleDTO> vehicles = controller.getAllVehicles();

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Vehicle Management", user, "vehicles");

      HeaderUtils.generatePageHeader(out, "Fleet Vehicles",
          "<a href='controller/vehicles?action=add' class='add-btn'>Add New Vehicle</a>");

      String success = (String) request.getAttribute("success");
      if (success != null) {
        out.println("<div class='success'>" + success + "</div>");
      }

      String error = (String) request.getAttribute("error");
      if (error != null) {
        out.println("<div class='error'>" + error + "</div>");
      }

      out.println("<div class='vehicles-table'>");
      out.println("<table class='data-table'>");
      out.println("<thead>");
      out.println("<tr>");
      out.println("<th>Vehicle ID</th>");
      out.println("<th>VIN</th>");
      out.println("<th>Type</th>");
      out.println("<th>Make</th>");
      out.println("<th>Model</th>");
      out.println("<th>Year</th>");
      out.println("<th>Status</th>");
      out.println("<th>Actions</th>");
      out.println("</tr>");
      out.println("</thead>");
      out.println("<tbody>");

      if (vehicles != null && !vehicles.isEmpty()) {
        for (VehicleDTO vehicle : vehicles) {
          out.println("<tr>");
          out.println("<td>" + vehicle.getVehicleId() + "</td>");
          out.println("<td>" + vehicle.getVin() + "</td>");
          out.println("<td>" + (vehicle.getVehicleType() != null ? vehicle.getVehicleType() : "N/A") + "</td>");
          out.println("<td>" + vehicle.getMake() + "</td>");
          out.println("<td>" + vehicle.getModel() + "</td>");
          out.println("<td>" + vehicle.getYear() + "</td>");
          String status = vehicle.getStatus() != null ? vehicle.getStatus() : "ACTIVE";
          String statusClass = "ACTIVE".equals(status) ? "status-active" : "status-inactive";
          out.println("<td><span class='" + statusClass + "'>" + status + "</span></td>");
          out.println("<td class='action-links'>");
          out.println(
              "<a href='controller/vehicles?action=view&id=" + vehicle.getVehicleId() + "' class='btn-small'>View</a>");
          out.println(
              "<a href='controller/vehicles?action=edit&id=" + vehicle.getVehicleId() + "' class='btn-small'>Edit</a>");
          out.println("<a href='controller/vehicles?action=delete&id=" + vehicle.getVehicleId() +
              "' onclick='return confirm(\"Are you sure you want to delete this vehicle?\")' class='btn-small btn-danger'>Delete</a>");
          out.println("</td>");
          out.println("</tr>");
        }
      } else {
        out.println("<tr>");
        out.println("<td colspan='8' class='empty-state'>");
        out.println("<div class='no-data'>");
        out.println("<h3>No vehicles found</h3>");
        out.println("<p>Start by adding your first vehicle to the fleet.</p>");
        out.println("<a href='controller/vehicles?action=add' class='add-btn'>Add Vehicle</a>");
        out.println("</div>");
        out.println("</td>");
        out.println("</tr>");
      }

      out.println("</tbody>");
      out.println("</table>");
      out.println("</div>");

      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Generates the HTML form for adding a new vehicle to the fleet.
   * Displays form fields for vehicle information and handles error display.
   * 
   * @param request  the HttpServletRequest object
   * @param response the HttpServletResponse object
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void generateAddVehicleForm(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Add Vehicle", user, "vehicles");

      HeaderUtils.generatePageHeader(out, "Add New Vehicle to Fleet",
          "<a href='controller/vehicles' class='back-btn'>Back to Vehicles</a>");

      String error = (String) request.getAttribute("error");
      if (error != null) {
        out.println("<div class='error'>" + error + "</div>");
      }

      out.println("<div class='form-container'>");
      out.println("<form method='post' action='controller/vehicles'>");
      out.println("<input type='hidden' name='action' value='add'>");

      out.println("<div class='form-group'>");
      out.println("<label for='vin'>Vehicle Identification Number (VIN) <span class='required'>*</span></label>");
      out.println(
          "<input type='text' id='vin' name='vin' required maxlength='17' placeholder='Enter 17-character VIN'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='vehicleType'>Vehicle Type <span class='required'>*</span></label>");
      out.println("<select id='vehicleType' name='vehicleType' required>");
      out.println("<option value=''>Select Vehicle Type</option>");
      out.println("<option value='Diesel Bus'>Diesel Bus</option>");
      out.println("<option value='Electric Bus'>Electric Bus</option>");
      out.println("<option value='CNG Bus'>CNG Bus</option>");
      out.println("<option value='Electric Light Rail'>Electric Light Rail</option>");
      out.println("<option value='Diesel-Electric Train'>Diesel-Electric Train</option>");
      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='make'>Vehicle Make/Manufacturer <span class='required'>*</span></label>");
      out.println(
          "<input type='text' id='make' name='make' required maxlength='50' placeholder='e.g., New Flyer, Siemens, Bombardier'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='model'>Vehicle Model <span class='required'>*</span></label>");
      out.println(
          "<input type='text' id='model' name='model' required maxlength='50' placeholder='Enter vehicle model'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='year'>Manufacturing Year <span class='required'>*</span></label>");
      out.println(
          "<input type='number' id='year' name='year' required min='1990' max='2025' placeholder='Enter year'>");
      out.println("</div>");

      out.println("<div class='button-group'>");
      out.println("<input type='submit' value='Add Vehicle' class='submit-btn'>");
      out.println("<a href='controller/vehicles' class='back-btn'>Cancel</a>");
      out.println("</div>");

      out.println("</form>");
      out.println("</div>");

      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Generates a detailed view of a specific vehicle.
   * Displays vehicle information, status, and related action buttons.
   * 
   * @param request  the HttpServletRequest object containing vehicle ID
   * @param response the HttpServletResponse object
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void generateVehicleView(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String vehicleIdStr = request.getParameter("id");
    if (vehicleIdStr == null) {
      response.sendRedirect("controller/vehicles");
      return;
    }

    int vehicleId = Integer.parseInt(vehicleIdStr);
    VehicleDTO vehicle = controller.getVehicleById(vehicleId);

    if (vehicle == null) {
      request.setAttribute("error", "Vehicle not found");
      generateVehicleList(request, response, user);
      return;
    }

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Vehicle Details", user, "vehicles");

      HeaderUtils.generatePageHeader(out, "Vehicle Details",
          "<a href='controller/vehicles' class='back-btn'>Back to Vehicles</a>");

      out.println("<div class='vehicle-details'>");
      out.println("<div class='detail-card'>");
      out.println("<h3>Vehicle Information</h3>");
      out.println("<table class='detail-table'>");
      out.println("<tr><td><strong>Vehicle ID:</strong></td><td>" + vehicle.getVehicleId() + "</td></tr>");
      out.println("<tr><td><strong>VIN:</strong></td><td>" + vehicle.getVin() + "</td></tr>");
      out.println("<tr><td><strong>Vehicle Type:</strong></td><td>"
          + (vehicle.getVehicleType() != null ? vehicle.getVehicleType() : "N/A") + "</td></tr>");
      out.println("<tr><td><strong>Make:</strong></td><td>" + vehicle.getMake() + "</td></tr>");
      out.println("<tr><td><strong>Model:</strong></td><td>" + vehicle.getModel() + "</td></tr>");
      out.println("<tr><td><strong>Year:</strong></td><td>" + vehicle.getYear() + "</td></tr>");
      String status = vehicle.getStatus() != null ? vehicle.getStatus() : "ACTIVE";
      String statusClass = "ACTIVE".equals(status) ? "status-active" : "status-inactive";
      out.println(
          "<tr><td><strong>Status:</strong></td><td><span class='" + statusClass + "'>" + status + "</span></td></tr>");
      out.println("</table>");
      out.println("</div>");

      // Quick Actions
      out.println("<div class='quick-actions'>");
      out.println("<h3>Quick Actions</h3>");
      out.println("<div class='action-buttons'>");
      out.println("<a href='controller/vehicles?action=edit&id=" + vehicle.getVehicleId()
          + "' class='btn btn-primary'>Edit Vehicle</a>");
      out.println("<a href='controller/gps-tracking?action=view&vehicleId=" + vehicle.getVehicleId()
          + "' class='btn btn-secondary'>View GPS Tracking</a>");
      out.println("<a href='controller/maintenance?vehicleId=" + vehicle.getVehicleId()
          + "' class='btn btn-secondary'>View Maintenance</a>");
      out.println("<a href='controller/fuel-logs?vehicleId=" + vehicle.getVehicleId()
          + "' class='btn btn-secondary'>View Fuel Logs</a>");
      out.println("</div>");
      out.println("</div>");

      out.println("</div>");

      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Generates the HTML form for editing an existing vehicle.
   * Pre-populates form fields with current vehicle information.
   * 
   * @param request  the HttpServletRequest object containing vehicle ID
   * @param response the HttpServletResponse object
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during response writing
   */
  private void generateEditVehicleForm(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String vehicleIdStr = request.getParameter("id");
    if (vehicleIdStr == null) {
      response.sendRedirect("controller/vehicles");
      return;
    }

    int vehicleId = Integer.parseInt(vehicleIdStr);
    VehicleDTO vehicle = controller.getVehicleById(vehicleId);

    if (vehicle == null) {
      request.setAttribute("error", "Vehicle not found");
      generateVehicleList(request, response, user);
      return;
    }

    try (PrintWriter out = response.getWriter()) {
      HeaderUtils.generateHeader(out, "Edit Vehicle", user, "vehicles");

      HeaderUtils.generatePageHeader(out, "Edit Vehicle Information",
          "<a href='controller/vehicles?action=view&id=" + vehicleId + "' class='back-btn'>Back to Vehicle</a>");

      String error = (String) request.getAttribute("error");
      if (error != null) {
        out.println("<div class='error'>" + error + "</div>");
      }

      out.println("<div class='form-container'>");
      out.println("<form method='post' action='controller/vehicles'>");
      out.println("<input type='hidden' name='action' value='edit'>");
      out.println("<input type='hidden' name='id' value='" + vehicle.getVehicleId() + "'>");

      out.println("<div class='form-group'>");
      out.println("<label for='vin'>Vehicle Identification Number (VIN) <span class='required'>*</span></label>");
      out.println("<input type='text' id='vin' name='vin' required maxlength='17' value='" + vehicle.getVin() + "'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='vehicleType'>Vehicle Type <span class='required'>*</span></label>");
      out.println("<select id='vehicleType' name='vehicleType' required>");
      out.println("<option value=''>Select Vehicle Type</option>");
      String[] vehicleTypes = { "Diesel Bus", "Electric Bus", "CNG Bus", "Electric Light Rail",
          "Diesel-Electric Train" };
      for (String type : vehicleTypes) {
        String selected = type.equals(vehicle.getVehicleType()) ? "selected" : "";
        out.println("<option value='" + type + "' " + selected + ">" + type + "</option>");
      }
      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='make'>Vehicle Make/Manufacturer <span class='required'>*</span></label>");
      out.println("<input type='text' id='make' name='make' required maxlength='50' value='" + vehicle.getMake()
          + "' placeholder='e.g., New Flyer, Siemens, Bombardier'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='model'>Vehicle Model <span class='required'>*</span></label>");
      out.println(
          "<input type='text' id='model' name='model' required maxlength='50' value='" + vehicle.getModel() + "'>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='year'>Manufacturing Year <span class='required'>*</span></label>");
      out.println("<input type='number' id='year' name='year' required min='1990' max='2025' value='"
          + vehicle.getYear() + "'>");
      out.println("</div>");

      out.println("<div class='button-group'>");
      out.println("<input type='submit' value='Update Vehicle' class='submit-btn'>");
      out.println("<a href='controller/vehicles?action=view&id=" + vehicleId + "' class='back-btn'>Cancel</a>");
      out.println("</div>");

      out.println("</form>");
      out.println("</div>");

      HeaderUtils.generateFooter(out);
    }
  }

  /**
   * Handles the deletion of a vehicle from the fleet.
   * Removes the vehicle from the database and redirects to the vehicle list.
   * 
   * @param request  the HttpServletRequest object containing vehicle ID
   * @param response the HttpServletResponse object
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during redirection
   */
  private void handleDeleteVehicle(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String vehicleIdStr = request.getParameter("id");
    if (vehicleIdStr == null) {
      response.sendRedirect("controller/vehicles");
      return;
    }

    try {
      int vehicleId = Integer.parseInt(vehicleIdStr);

      // Since there's no delete method in the controller, we'll update status to
      // inactive
      boolean success = controller.updateVehicleStatus(vehicleId, "INACTIVE");

      if (success) {
        request.setAttribute("success", "Vehicle has been deactivated successfully.");
      } else {
        request.setAttribute("error", "Failed to deactivate vehicle.");
      }
    } catch (Exception e) {
      request.setAttribute("error", "Error deactivating vehicle: " + e.getMessage());
    }

    response.sendRedirect("controller/vehicles");
  }

  /**
   * Handles the addition of a new vehicle to the fleet.
   * Processes form data and creates a new vehicle record in the database.
   * 
   * @param request  the HttpServletRequest object containing form data
   * @param response the HttpServletResponse object
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during processing
   */
  private void handleAddVehicle(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      String vin = request.getParameter("vin");
      String vehicleType = request.getParameter("vehicleType");
      String make = request.getParameter("make");
      String model = request.getParameter("model");
      int year = Integer.parseInt(request.getParameter("year"));

      // Generate a vehicle number
      String vehicleNumber = "V" + System.currentTimeMillis();

      boolean success = controller.addVehicle(vin, vehicleNumber, vehicleType, make, model, year);

      if (success) {
        request.setAttribute("success", "Vehicle added successfully!");
        response.sendRedirect("controller/vehicles");
      } else {
        request.setAttribute("error", "Failed to add vehicle. VIN may already exist.");
        generateAddVehicleForm(request, response, user);
      }
    } catch (Exception e) {
      request.setAttribute("error", "Error adding vehicle: " + e.getMessage());
      generateAddVehicleForm(request, response, user);
    }
  }

  /**
   * Handles the editing of an existing vehicle's information.
   * Updates vehicle data in the database with new values from the form.
   * 
   * @param request  the HttpServletRequest object containing form data and
   *                 vehicle ID
   * @param response the HttpServletResponse object
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during processing
   */
  private void handleEditVehicle(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    try {
      int vehicleId = Integer.parseInt(request.getParameter("id"));
      String vin = request.getParameter("vin");
      String vehicleType = request.getParameter("vehicleType");
      String make = request.getParameter("make");
      String model = request.getParameter("model");
      int year = Integer.parseInt(request.getParameter("year"));

      boolean success = controller.updateVehicle(vehicleId, vin, vehicleType, make, model, year);

      if (success) {
        request.setAttribute("success", "Vehicle updated successfully!");
        response.sendRedirect("controller/vehicles?action=view&id=" + vehicleId);
      } else {
        request.setAttribute("error", "Failed to update vehicle.");
        generateEditVehicleForm(request, response, user);
      }

    } catch (Exception e) {
      request.setAttribute("error", "Error updating vehicle: " + e.getMessage());
      response.sendRedirect("controller/vehicles");
    }
  }

  /**
   * Handles vehicle selection for operations that require a specific vehicle.
   * Stores the selected vehicle in the session for subsequent operations.
   * 
   * @param request  the HttpServletRequest object containing vehicle ID
   * @param response the HttpServletResponse object
   * @param user     the currently authenticated user
   * @throws IOException if an I/O error occurs during processing
   */
  private void handleSelectVehicle(HttpServletRequest request, HttpServletResponse response, UserDTO user)
      throws IOException {
    String vehicleIdStr = request.getParameter("id");
    if (vehicleIdStr == null) {
      response.sendRedirect("controller/vehicles");
      return;
    }

    try {
      int vehicleId = Integer.parseInt(vehicleIdStr);

      // For now, just redirect to vehicle view
      // In a real implementation, you might assign the vehicle to the operator
      response.sendRedirect("controller/vehicles?action=view&id=" + vehicleId);

    } catch (Exception e) {
      request.setAttribute("error", "Error selecting vehicle: " + e.getMessage());
      response.sendRedirect("controller/vehicles");
    }
  }
}
