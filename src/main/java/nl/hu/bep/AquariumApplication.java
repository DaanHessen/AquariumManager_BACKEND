package nl.hu.bep;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.config.DatabaseConfig;
import nl.hu.bep.presentation.resource.*;
import org.glassfish.jersey.server.ResourceConfig;

@Slf4j
@ApplicationPath("/api")
public class AquariumApplication extends ResourceConfig {

    public AquariumApplication(@Context UriInfo uriInfo) {
        try {
            log.info("Initializing Aquarium API application...");
            DatabaseConfig.initialize();

            // Register all resource classes for CDI
            register(AquariumManagerResource.class);
            register(AccessoryResource.class);
            register(InhabitantResource.class);
            register(OrnamentResource.class);
            register(RootResource.class);
            
            // Register security resources
            register(nl.hu.bep.security.presentation.resource.AuthResource.class);
            
        } catch (Exception e) {
            log.error("Failed to initialize application: {}", e.getMessage(), e);
        }
    }
}
