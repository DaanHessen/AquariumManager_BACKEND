package nl.hu.bep.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Simple health check servlet that responds immediately without requiring
 * full application initialization. This ensures Railway health checks
 * pass during startup even if database connectivity is delayed.
 */
public class SimpleHealthServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(SimpleHealthServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Basic application health - always returns success as long as servlet container is running
            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("{");
            jsonResponse.append("\"status\":\"success\",");
            jsonResponse.append("\"data\":{");
            jsonResponse.append("\"status\":\"UP\",");
            jsonResponse.append("\"timestamp\":").append(System.currentTimeMillis()).append(",");
            jsonResponse.append("\"application\":\"RUNNING\",");
            jsonResponse.append("\"service\":\"Aquarium API\",");
            jsonResponse.append("\"version\":\"1.0.0\",");
            jsonResponse.append("\"environment\":{");
            jsonResponse.append("\"PORT\":\"").append(getPortInfo()).append("\",");
            jsonResponse.append("\"java_version\":\"").append(System.getProperty("java.version")).append("\"");
            jsonResponse.append("}");
            jsonResponse.append("},");
            jsonResponse.append("\"message\":\"Application is running\"");
            jsonResponse.append("}");
            
            response.setStatus(HttpServletResponse.SC_OK);
            
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse.toString());
                out.flush();
            }
            
            log.log(Level.FINE, "Health check request successful from: " + request.getRemoteAddr());
            
        } catch (Exception e) {
            log.log(Level.SEVERE, "Health check failed: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"status\":\"error\",\"message\":\"Health check failed: " + e.getMessage() + "\"}");
                out.flush();
            }
        }
    }
    
    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Support HEAD requests for health checks
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private String getPortInfo() {
        String port = System.getenv("PORT");
        return port != null ? port : "8080";
    }
    
    @Override
    public void init() throws ServletException {
        super.init();
        log.info("SimpleHealthServlet initialized successfully");
    }
} 