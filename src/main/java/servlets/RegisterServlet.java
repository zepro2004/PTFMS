package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import controllers.PTFMSController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet for handling user registration in the PTFMS system.
 * Provides registration functionality for new users with validation
 * and role assignment. Handles both GET requests for displaying the
 * registration form and POST requests for processing new user data.
 */
public class RegisterServlet extends HttpServlet {
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
   * Handles HTTP GET requests to display the registration form.
   * Renders the registration page with appropriate form fields and styling.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");

    String errorMessage = request.getParameter("error");
    String successMessage = request.getParameter("success");
    String errorAttr = (String) request.getAttribute("error");
    String successAttr = (String) request.getAttribute("success");

    try (PrintWriter out = response.getWriter()) {
      out.println(generateHeader("Register - PTFMS"));

      out.println("<div class='container'>");
      out.println("<div class='form-container'>");
      out.println("<h2>Register New User</h2>");

      // Display messages
      if (errorMessage != null || errorAttr != null) {
        out.println("<div class='error-message'>" +
            (errorMessage != null ? errorMessage : errorAttr) + "</div>");
      }
      if (successMessage != null || successAttr != null) {
        out.println("<div class='success-message'>" +
            (successMessage != null ? successMessage : successAttr) + "</div>");
      }

      out.println("<form method='post' action='register' class='modern-form'>");
      out.println("<div class='form-group'>");
      out.println("<label for='username'>Username:</label>");
      out.println("<input type='text' id='username' name='username' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='password'>Password:</label>");
      out.println("<input type='password' id='password' name='password' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='confirmPassword'>Confirm Password:</label>");
      out.println("<input type='password' id='confirmPassword' name='confirmPassword' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='email'>Email:</label>");
      out.println("<input type='email' id='email' name='email' required>");
      out.println("</div>");

      out.println("<div class='form-group'>");
      out.println("<label for='role'>Role:</label>");
      out.println("<select id='role' name='role' required>");
      out.println("<option value=''>Select Role</option>");
      out.println("<option value='OPERATOR'>Operator</option>");
      out.println("<option value='MANAGER'>Manager</option>");
      out.println("</select>");
      out.println("</div>");

      out.println("<div class='form-actions'>");
      out.println("<button type='submit' class='btn btn-primary'>Register</button>");
      out.println("<a href='login' class='btn btn-secondary'>Back to Login</a>");
      out.println("</div>");
      out.println("</form>");

      out.println("</div>");
      out.println("</div>");

      out.println(generateFooter());
    }
  }

  /**
   * Handles HTTP POST requests to process user registration form submissions.
   * Validates form data, creates new user accounts, and redirects to the login
   * page on success or shows appropriate error messages on failure.
   * 
   * @param request  the HttpServletRequest object containing the registration
   *                 form data
   * @param response the HttpServletResponse object for sending the response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String confirmPassword = request.getParameter("confirmPassword");
    String email = request.getParameter("email");
    String role = request.getParameter("role");

    // Validation
    if (username == null || username.trim().isEmpty()) {
      request.setAttribute("error", "Username is required");
      doGet(request, response);
      return;
    }

    if (password == null || password.length() < 6) {
      request.setAttribute("error", "Password must be at least 6 characters");
      doGet(request, response);
      return;
    }

    if (!password.equals(confirmPassword)) {
      request.setAttribute("error", "Passwords do not match");
      doGet(request, response);
      return;
    }

    if (email == null || email.trim().isEmpty() || !email.contains("@")) {
      request.setAttribute("error", "Valid email is required");
      doGet(request, response);
      return;
    }

    if (role == null || role.trim().isEmpty()) {
      request.setAttribute("error", "Role selection is required");
      doGet(request, response);
      return;
    }

    try {
      if (controller.registerUser(username.trim(), password, role.toUpperCase())) {
        response.sendRedirect("login?success=User registered successfully. Please login.");
      } else {
        request.setAttribute("error", "Registration failed. Username may already exist.");
        doGet(request, response);
      }
    } catch (Exception e) {
      request.setAttribute("error", "Registration error: " + e.getMessage());
      doGet(request, response);
    }
  }

  /**
   * Generates the HTML header with modern responsive styling for the registration
   * page.
   * Creates a professional layout with gradients, responsive design, and form
   * styling.
   * 
   * @param title the page title to display in the browser tab
   * @return a complete HTML header as a String
   */
  private String generateHeader(String title) {
    StringBuilder sb = new StringBuilder();
    sb.append("<!DOCTYPE html>");
    sb.append("<html lang='en'>");
    sb.append("<head>");
    sb.append("<meta charset='UTF-8'>");
    sb.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
    sb.append("<title>").append(title).append("</title>");
    sb.append("<style>");
    sb.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
    sb.append(
        "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
    sb.append(".container { width: 100%; max-width: 500px; padding: 20px; }");
    sb.append(
        ".form-container { background: white; padding: 40px; border-radius: 15px; box-shadow: 0 15px 35px rgba(0,0,0,0.1); }");
    sb.append("h2 { color: #333; margin-bottom: 30px; text-align: center; font-size: 28px; }");
    sb.append(".modern-form { width: 100%; }");
    sb.append(".form-group { margin-bottom: 20px; }");
    sb.append("label { display: block; margin-bottom: 8px; color: #555; font-weight: 500; }");
    sb.append(
        "input, select { width: 100%; padding: 12px 15px; border: 2px solid #e1e1e1; border-radius: 8px; font-size: 16px; transition: border-color 0.3s; }");
    sb.append("input:focus, select:focus { outline: none; border-color: #667eea; }");
    sb.append(".form-actions { margin-top: 30px; display: flex; gap: 15px; }");
    sb.append(
        ".btn { padding: 12px 25px; border: none; border-radius: 8px; cursor: pointer; text-decoration: none; text-align: center; font-size: 16px; font-weight: 500; transition: all 0.3s; flex: 1; }");
    sb.append(".btn-primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }");
    sb.append(".btn-primary:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(102,126,234,0.4); }");
    sb.append(".btn-secondary { background: #f8f9fa; color: #6c757d; border: 2px solid #e9ecef; }");
    sb.append(".btn-secondary:hover { background: #e9ecef; color: #495057; }");
    sb.append(
        ".error-message { background: #f8d7da; color: #721c24; padding: 12px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #f5c6cb; }");
    sb.append(
        ".success-message { background: #d1f2eb; color: #0c5460; padding: 12px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #bee5eb; }");
    sb.append("</style>");
    sb.append("</head>");
    sb.append("<body>");
    return sb.toString();
  }

  /**
   * Generates the HTML footer with proper closing tags.
   * Ensures valid HTML document structure by closing all open tags.
   * 
   * @return the HTML footer as a String
   */
  private String generateFooter() {
    return "</body></html>";
  }
}