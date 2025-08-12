package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import controllers.PTFMSController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import transferobjects.UserDTO;

/**
 * Servlet for handling user authentication in the PTFMS system.
 * Provides login functionality with session management and user validation.
 * Handles both GET requests for displaying the login form and POST requests
 * for processing login credentials.
 */
public class LoginServlet extends HttpServlet {
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
   * Handles HTTP GET requests to display the login form.
   * Renders the login page with appropriate styling and form elements.
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

    try (PrintWriter out = response.getWriter()) {
      generateHeader(out, "Login");

      out.println("<div class='login-container'>");
      out.println("<div class='login-card'>");
      out.println("<h2>Public Transit Fleet Management System</h2>");
      out.println("<h3>Login</h3>");

      String error = (String) request.getAttribute("error");
      if (error != null) {
        out.println("<div class='error-message'>" + error + "</div>");
      }

      String success = (String) request.getAttribute("success");
      if (success != null) {
        out.println("<div class='success-message'>" + success + "</div>");
      }

      out.println("<form method='post' action='login' class='login-form'>");
      out.println("<div class='form-group'>");
      out.println("<label for='username'>Username:</label>");
      out.println("<input type='text' id='username' name='username' required>");
      out.println("</div>");
      out.println("<div class='form-group'>");
      out.println("<label for='password'>Password:</label>");
      out.println("<input type='password' id='password' name='password' required>");
      out.println("</div>");
      out.println("<div class='form-group'>");
      out.println("<button type='submit' class='btn btn-primary'>Login</button>");
      out.println("</div>");
      out.println("</form>");
      out.println(
          "<p class='register-link'><a href='controller/register'>Don't have an account? Register here</a></p>");
      out.println("</div>");
      out.println("</div>");

      generateFooter(out);
    }
  }

  /**
   * Handles HTTP POST requests for user authentication.
   * Validates submitted credentials, creates user session upon successful login,
   * and redirects to appropriate dashboard based on user role.
   * On failure, redirects back to the login page with error message.
   * 
   * @param request  the HttpServletRequest object containing login credentials
   * @param response the HttpServletResponse object for sending the response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String username = request.getParameter("username");
    String password = request.getParameter("password");

    UserDTO user = controller.loginUser(username, password);

    if (user != null) {
      HttpSession session = request.getSession();
      session.setAttribute("user", user);
      session.setAttribute("username", user.getUsername());
      session.setAttribute("role", user.getRole());

      if (user.isManager()) {
        response.sendRedirect("controller/manager-dashboard");
      } else {
        response.sendRedirect("controller/operator-dashboard");
      }
    } else {
      request.setAttribute("error", "Invalid username or password");
      doGet(request, response);
    }
  }

  /**
   * Generates the HTML header and CSS links for the login page.
   * Creates consistent page structure with responsive design elements.
   * 
   * @param out   the PrintWriter for writing HTML output
   * @param title the page title to display in the browser tab
   */
  private void generateHeader(PrintWriter out, String title) {
    out.println("<!DOCTYPE html>");
    out.println("<html lang='en'>");
    out.println("<head>");
    out.println("<meta charset='UTF-8'>");
    out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
    out.println("<title>" + title + " - PTFMS</title>");
    out.println("<link rel='stylesheet' type='text/css' href='assets/styles/login.css'>");
    out.println("</head>");
    out.println("<body>");
  }

  /**
   * Generates the HTML footer and closing tags for the login page.
   * Ensures proper HTML document structure and closes all open tags.
   * 
   * @param out the PrintWriter for writing HTML output
   */
  private void generateFooter(PrintWriter out) {
    out.println("</body>");
    out.println("</html>");
  }
}
