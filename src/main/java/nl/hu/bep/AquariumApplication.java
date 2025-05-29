package nl.hu.bep;

import jakarta.inject.Singleton;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.application.AccessoryService;
import nl.hu.bep.application.AquariumService;
import nl.hu.bep.application.InhabitantService;
import nl.hu.bep.application.OrnamentService;
import nl.hu.bep.security.presentation.resource.AuthResource;
import nl.hu.bep.application.mapper.*;
import nl.hu.bep.config.DatabaseConfig;
import nl.hu.bep.config.JacksonConfig;
import nl.hu.bep.data.*;
import nl.hu.bep.presentation.exception.ExceptionMappers;
import nl.hu.bep.presentation.resource.AccessoryResource;
import nl.hu.bep.presentation.resource.AquariumResource;
import nl.hu.bep.presentation.resource.InhabitantResource;
import nl.hu.bep.presentation.resource.OrnamentResource;
import nl.hu.bep.presentation.resource.RootResource;
import nl.hu.bep.security.application.filter.AquariumSecurityFilter;
import nl.hu.bep.security.application.filter.OwnershipFilter;
import nl.hu.bep.security.application.service.AuthenticationService;
import nl.hu.bep.security.application.service.AuthorizationService;
import nl.hu.bep.security.application.service.JwtService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

@Slf4j
@ApplicationPath("/api")
public class AquariumApplication extends ResourceConfig {

    public AquariumApplication(@Context UriInfo uriInfo) {
        try {
            log.info("Initializing Aquarium API application...");
            
            // Initialize database in a way that doesn't block application startup
            try {
                DatabaseConfig.initialize();
                log.info("Database initialized successfully during application startup");
            } catch (Exception e) {
                log.warn("Database initialization encountered issues during startup, but continuing with application startup. Database will be initialized on first use. Error: {}", e.getMessage());
                // Continue with application startup even if database initialization fails
                // The health check endpoints will handle database availability separately
            }

            // Register dependency binder first
            register(new DependencyBinder(uriInfo));

            // Register resource classes
            register(AccessoryResource.class);
            register(AquariumResource.class);
            register(OrnamentResource.class);
            register(InhabitantResource.class);
            register(RootResource.class);
            register(AuthResource.class);

            // Register exception mappers
            register(ExceptionMappers.class);

            // Scan for additional resources
            packages(
                    "nl.hu.bep.presentation.resource",
                    "nl.hu.bep.security.presentation.resource",
                    "nl.hu.bep.security.application.filter",
                    "nl.hu.bep.application.mapper");

            // Register features
            register(RolesAllowedDynamicFeature.class);
            register(JacksonFeature.class);

            log.info("Aquarium API initialized successfully (using AquariumApplication as main config)");
        } catch (Exception e) {
            log.error("Failed to initialize application: {}", e.getMessage(), e);
            // For Railway deployment, we want to continue even if there are initialization issues
            // to allow the health check to succeed and give the database time to become available
            log.warn("Application startup will continue despite initialization errors to allow health checks to succeed");
        }
    }

    private static class DependencyBinder extends AbstractBinder {
        private final UriInfo uriInfo;

        public DependencyBinder(UriInfo uriInfo) {
            this.uriInfo = uriInfo;
        }

        @Override
        protected void configure() {
            bind(uriInfo).to(UriInfo.class);

            bindAsSingleton(JacksonFeature.class);
            bindAsSingleton(JacksonConfig.class);

            // CorsFilter is registered as servlet filter in web.xml, not as Jersey component
            bindAsSingleton(AquariumSecurityFilter.class);
            bindAsSingleton(OwnershipFilter.class);

            bindAsSingleton(AccessoryRepository.class);
            bindAsSingleton(AquariumManagerRepository.class);
            bindAsSingleton(AquariumRepository.class);
            bindAsSingleton(InhabitantRepository.class);
            bindAsSingleton(OrnamentRepository.class);
            bindAsSingleton(OwnerRepository.class);

            bindAsSingleton(AquariumService.class);
            bindAsSingleton(InhabitantService.class);
            bindAsSingleton(AccessoryService.class);
            bindAsSingleton(OrnamentService.class);

            bindAsSingleton(JwtService.class);
            bindAsSingleton(AuthenticationService.class);
            bindAsSingleton(AuthorizationService.class);

            bindAsSingleton(EntityMappingService.class);
            bindAsSingleton(AquariumMapper.class);

            log.info("Aquarium API initialized successfully");
        }

        private <T> void bindAsSingleton(Class<T> clazz) {
            bind(clazz).to(clazz).in(Singleton.class);
        }
    }
}
