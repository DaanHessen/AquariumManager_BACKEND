package nl.hu.bep.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HealthCheckServlet", urlPatterns = {"/health"}, loadOnStartup = 1)
public class HealthCheckServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        String healthResponse = String.format(
            "{\"status\":\"UP\",\"timestamp\":%d,\"service\":\"AquariumAPI\"}",
            System.currentTimeMillis()
        );
        
        try (PrintWriter writer = response.getWriter()) {
            writer.print(healthResponse);
            writer.flush();
        }
    }
    
    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @Override
    public void init() throws ServletException {
        super.init();
        getServletContext().log("HealthCheckServlet initialized for Railway");
    }
} 