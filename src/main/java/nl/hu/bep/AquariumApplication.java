package nl.hu.bep;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.config.DatabaseConfig;
import nl.hu.bep.config.JacksonConfig;
import nl.hu.bep.config.HK2Binder;
import nl.hu.bep.exception.GlobalExceptionMapper;

@Slf4j
@ApplicationPath("/api")
public class AquariumApplication extends ResourceConfig {

    public AquariumApplication() {
        try {
            log.info("Initializing Aquarium API application...");
            DatabaseConfig.initialize();

            packages("nl.hu.bep.presentation.resource",
                     "nl.hu.bep.security.presentation.resource",
                     "nl.hu.bep.security.application.filter");

            register(GlobalExceptionMapper.class);
            register(JacksonConfig.class);

            register(new HK2Binder());
            
            log.info("Aquarium API application initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize application: {}", e.getMessage(), e);
            throw e;
        }
    }
}
