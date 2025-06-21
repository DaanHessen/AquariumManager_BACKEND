package nl.hu.bep;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.config.DatabaseConfig;
import nl.hu.bep.config.JacksonConfig;
import nl.hu.bep.exception.GlobalExceptionMapper;
import nl.hu.bep.security.application.filter.AquariumSecurityFilter;
import nl.hu.bep.security.application.filter.OwnershipFilter;

@Slf4j
@ApplicationPath("/api")
public class AquariumApplication extends ResourceConfig {

    public AquariumApplication() {
        try {
            log.info("Initializing Aquarium API application...");
            // Initialize database connection/configuration
            DatabaseConfig.initialize();

            /*
             * Register all application components.
             * Using package scanning for resources & providers keeps the list short and maintainable.
             */
            packages("nl.hu.bep.presentation.resource",
                     "nl.hu.bep.security.presentation.resource");

            // Register global providers explicitly when not automatically discovered
            register(GlobalExceptionMapper.class);
            register(JacksonConfig.class);

            // Security filters
            register(AquariumSecurityFilter.class);
            register(OwnershipFilter.class);

            log.info("Aquarium API application initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize application: {}", e.getMessage(), e);
            throw e; // Re-throw to prevent silent failure
        }
    }
}
