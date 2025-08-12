package servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Front Controller servlet implementing the Front Controller pattern for the
 * PTFMS system.
 * Routes incoming requests to appropriate servlets based on the URL path.
 * Centralizes request handling and provides a single entry point for the
 * application.
 */
public class FrontController extends HttpServlet {

  /**
   * Handles GET requests by dispatching to the appropriate servlet based on URL
   * path.
   * Parses the request path and forwards to the corresponding controller.
   * 
   * @param request  the HttpServletRequest object containing the request data
   * @param response HttpServletResponse object for sending response
   * @throws ServletException if servlet-related error occurs
   * @throws IOException      if I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    dispatch(request, response);
  }

  /**
   * Handles POST requests by dispatching to appropriate servlet
   * 
   * @param request  HttpServletRequest object containing the request data
   * @param response HttpServletResponse object for sending response
   * @throws ServletException if servlet-related error occurs
   * @throws IOException      if I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    dispatch(request, response);
  }

  /**
   * Dispatches requests to appropriate servlets based on the path
   * 
   * @param request  HttpServletRequest object containing the request data
   * @param response HttpServletResponse object for sending response
   * @throws ServletException if servlet-related error occurs
   * @throws IOException      if I/O error occurs
   */
  private void dispatch(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String pathInfo = request.getPathInfo();

    // Handle root path - redirect to login
    if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
      pathInfo = "/login";
    }

    try {
      request.getRequestDispatcher(pathInfo).forward(request, response);
    } catch (Exception e) {
      System.err.println("Error processing request for path " + pathInfo + ": " + e.getMessage());
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found: " + pathInfo);
    }
  }
}