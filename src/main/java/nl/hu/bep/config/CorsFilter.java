package nl.hu.bep.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        
        // Railway health checks come from healthcheck.railway.app
        String host = request.getHeader("Host");
        String userAgent = request.getHeader("User-Agent");
        if ((host != null && host.contains("railway.app")) || 
            (userAgent != null && userAgent.contains("Railway")) ||
            request.getRequestURI().contains("/health")) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            log.debug("Railway health check or internal request detected");
        }
        
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", 
            "Origin, X-Requested-With, Content-Type, Accept, Authorization, Cache-Control, Pragma");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // Handle preflight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        log.info("CorsFilter destroyed");
    }
} 