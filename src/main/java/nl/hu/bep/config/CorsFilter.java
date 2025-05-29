package nl.hu.bep.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CorsFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("CorsFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        // Get the origin from the request
        String origin = request.getHeader("Origin");
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        
        log.debug("CORS request - Origin: {}, Method: {}, URI: {}", origin, method, requestURI);
        
        // Always set these headers for all requests
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", 
            "Origin, X-Requested-With, Content-Type, Accept, Authorization, Cache-Control, Pragma");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // Determine the appropriate origin to allow
        if (origin != null) {
            // Explicitly allow your Vercel frontend
            if (origin.equals("https://aquarium-manager-frontend.vercel.app")) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                log.debug("CORS: Allowed Vercel frontend origin: {}", origin);
            }
            // Allow localhost for development
            else if (origin.contains("localhost") || origin.contains("127.0.0.1")) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                log.debug("CORS: Allowed localhost origin: {}", origin);
            }
            // Allow other Vercel deployment URLs (preview deployments)
            else if (origin.endsWith("vercel.app")) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                log.debug("CORS: Allowed Vercel deployment origin: {}", origin);
            }
            // Allow Railway origins (for internal checks)
            else if (origin.contains("railway.app")) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                log.debug("CORS: Allowed Railway origin: {}", origin);
            }
            // Log other origins but still allow them for debugging
            else {
                response.setHeader("Access-Control-Allow-Origin", "*");
                log.warn("CORS: Unknown origin allowed with wildcard: {}", origin);
            }
        } else {
            // No origin header (direct server requests, health checks, etc.)
            response.setHeader("Access-Control-Allow-Origin", "*");
            log.debug("CORS: No origin header, allowing wildcard");
        }
        
        // Handle preflight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(HttpServletResponse.SC_OK);
            log.debug("CORS: Handled OPTIONS preflight request for URI: {}", requestURI);
            return;
        }
        
        // Continue with the request
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in filter chain for request {}: {}", requestURI, e.getMessage(), e);
            // Set error response headers
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            
            // Create error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("path", requestURI);
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    @Override
    public void destroy() {
        log.info("CorsFilter destroyed");
    }
} 