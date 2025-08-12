package utils;

import java.io.PrintWriter;

import transferobjects.UserDTO;

/**
 * Utility class for generating consistent HTML headers and navigation across
 * all servlets in the PTFMS system. Provides standardized page layouts,
 * navigation bars, and CSS styling for consistent user experience.
 */
public class HeaderUtils {

  /**
   * Generates a standardized HTML header with consistent navigation.
   * Uses default styling without specific page activation.
   * 
   * @param out       the PrintWriter for writing HTML output
   * @param pageTitle the title to display in the browser tab and page header
   * @param user      the currently authenticated user for navigation context
   */
  public static void generateHeader(PrintWriter out, String pageTitle, UserDTO user) {
    generateHeader(out, pageTitle, user, null);
  }

  /**
   * Generates a standardized HTML header with consistent navigation and active
   * page indicator for enhanced user experience.
   * 
   * @param out        the PrintWriter for writing HTML output
   * @param pageTitle  the title to display in the browser tab and page header
   * @param user       the currently authenticated user for role-based navigation
   * @param activePage the current page identifier for active styling (nullable)
   */
  public static void generateHeader(PrintWriter out, String pageTitle, UserDTO user, String activePage) {
    out.println("<!DOCTYPE html>");
    out.println("<html lang='en'>");
    out.println("<head>");
    out.println("<meta charset='UTF-8'>");
    out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
    out.println("<title>" + pageTitle + " - PTFMS</title>");
    out.println("<link rel='stylesheet' href='assets/styles/common.css'>");

    // Add page-specific CSS based on active page
    if (activePage != null) {
      switch (activePage) {
        case "dashboard":
          if (user.isManager()) {
            out.println("<link rel='stylesheet' href='assets/styles/manager-dashboard.css'>");
          } else {
            out.println("<link rel='stylesheet' href='assets/styles/operator-dashboard.css'>");
          }
          break;
        case "vehicles":
          out.println("<link rel='stylesheet' href='assets/styles/vehicle-management.css'>");
          break;
        case "gps-tracking":
          out.println("<link rel='stylesheet' href='assets/styles/gps-tracking.css'>");
          break;
        case "fuel-logs":
          out.println("<link rel='stylesheet' href='assets/styles/fuel-logs.css'>");
          break;
        case "maintenance":
          out.println("<link rel='stylesheet' href='assets/styles/maintenance.css'>");
          break;
        case "alerts":
          out.println("<link rel='stylesheet' href='assets/styles/alerts.css'>");
          break;
        case "reports":
          out.println("<link rel='stylesheet' href='assets/styles/reports.css'>");
          break;
      }
    }

    out.println("</head>");
    out.println("<body>");

    // Standard header
    out.println("<div class='header'>");
    out.println("<h1>Public Transportation Fleet Management System</h1>");
    out.println("</div>");

    // Standard navigation
    out.println("<div class='nav-bar'>");
    out.println("<div class='nav-links'>");

    // Dashboard link (role-specific)
    String dashboardClass = "dashboard".equals(activePage) ? " class='active'" : "";
    if (user.isManager()) {
      out.println("<a href='controller/manager-dashboard'" + dashboardClass + ">Dashboard</a>");
    } else {
      out.println("<a href='controller/operator-dashboard'" + dashboardClass + ">Dashboard</a>");
    }

    // Standard navigation links - consistent order and all pages included
    String vehiclesClass = "vehicles".equals(activePage) ? " class='active'" : "";
    String gpsClass = "gps-tracking".equals(activePage) ? " class='active'" : "";
    String fuelClass = "fuel-logs".equals(activePage) ? " class='active'" : "";
    String maintenanceClass = "maintenance".equals(activePage) ? " class='active'" : "";
    String alertsClass = "alerts".equals(activePage) ? " class='active'" : "";
    String reportsClass = "reports".equals(activePage) ? " class='active'" : "";

    out.println("<a href='controller/vehicles'" + vehiclesClass + ">Vehicles</a>");
    out.println("<a href='controller/gps-tracking'" + gpsClass + ">GPS Tracking</a>");
    out.println("<a href='controller/fuel-logs'" + fuelClass + ">Fuel Logs</a>");
    out.println("<a href='controller/maintenance'" + maintenanceClass + ">Maintenance</a>");
    out.println("<a href='controller/alerts'" + alertsClass + ">Alerts</a>");
    out.println("<a href='controller/reports'" + reportsClass + ">Reports</a>");

    out.println("</div>");
    out.println("<a href='controller/logout' class='logout-btn'>Logout</a>");
    out.println("</div>");

    // Container div for page content
    out.println("<div class='container'>");
  }

  /**
   * Generates standard footer
   */
  public static void generateFooter(PrintWriter out) {
    out.println("</div>"); // Close container
    out.println("</body>");
    out.println("</html>");
  }

  /**
   * Generates standard page header section (not the HTML header)
   */
  public static void generatePageHeader(PrintWriter out, String title, String... actionButtons) {
    out.println("<div class='page-header'>");
    out.println("<h2>" + title + "</h2>");

    if (actionButtons.length > 0) {
      out.println("<div class='header-actions'>");
      for (String button : actionButtons) {
        out.println(button);
      }
      out.println("</div>");
    }

    out.println("</div>");
  }
}
