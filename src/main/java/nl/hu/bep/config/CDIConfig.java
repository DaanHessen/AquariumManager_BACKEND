package nl.hu.bep.config;

import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * CDI Configuration for dependency injection.
 */
@ApplicationScoped
public class CDIConfig {
    
    @Produces
    @ApplicationScoped
    public EntityMapper entityMapper() {
        return new EntityMapper();
    }
} 