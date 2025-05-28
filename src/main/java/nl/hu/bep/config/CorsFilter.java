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
        
        // Allow requests from your specific Vercel domain and localhost for development
        if (origin != null && (
            origin.equals("https://aquarium-manager-frontend.vercel.app") ||
            origin.contains("localhost") || 
            origin.contains("127.0.0.1") ||
            origin.contains("vercel.app") ||
            origin.contains("railway.app") ||
            origin.contains("render.com") ||
            origin.contains("netlify.app") ||
            origin.contains("github.io")
        )) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            // For any other origins, still allow but log for monitoring
            response.setHeader("Access-Control-Allow-Origin", "*");
            if (origin != null) {
                log.info("CORS request from origin: {}", origin);
            }
        }
        
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "OK");
            responseBody.put("message", "CORS preflight request successful");
            
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        log.info("CorsFilter destroyed");
    }
} 