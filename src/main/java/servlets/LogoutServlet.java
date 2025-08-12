package servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet for handling user logout in the PTFMS system.
 * Manages session termination and redirects users to the login page
 * after successful logout. Ensures proper cleanup of user sessions.
 */
public class LogoutServlet extends HttpServlet {

  /**
   * Handles HTTP GET requests for user logout.
   * Invalidates the current session and redirects to the login page.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    response.sendRedirect("controller/login");
  }

  /**
   * Handles HTTP POST requests for user logout.
   * Delegates to doGet method to ensure consistent logout behavior
   * regardless of HTTP method used for the request.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response the HttpServletResponse object for sending the response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }
}
