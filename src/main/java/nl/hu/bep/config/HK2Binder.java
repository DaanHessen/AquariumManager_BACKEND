package nl.hu.bep.config;

import nl.hu.bep.data.*;
import nl.hu.bep.data.interfaces.*;
import nl.hu.bep.presentation.resource.*;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.application.service.AquariumManagerService;
import nl.hu.bep.security.application.filter.AquariumSecurityFilter;
import nl.hu.bep.security.application.filter.OwnershipFilter;
import nl.hu.bep.security.application.service.AuthenticationService;
import nl.hu.bep.security.application.service.JwtService;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import jakarta.inject.Singleton;

public class HK2Binder extends AbstractBinder {

    @Override
    protected void configure() {
        // Repository bindings
        bind(AquariumRepositoryImpl.class).to(AquariumRepository.class).in(Singleton.class);
        bind(AccessoryRepositoryImpl.class).to(AccessoryRepository.class).in(Singleton.class);
        bind(InhabitantRepositoryImpl.class).to(InhabitantRepository.class).in(Singleton.class);
        bind(OrnamentRepositoryImpl.class).to(OrnamentRepository.class).in(Singleton.class);
        bind(OwnerRepositoryImpl.class).to(OwnerRepository.class).in(Singleton.class);

        // Service bindings
        bind(AquariumManagerService.class).in(Singleton.class);
        bind(EntityMapper.class).in(Singleton.class);
        bind(JwtService.class).in(Singleton.class);
        bind(AuthenticationService.class).in(Singleton.class);
        
        // Resource bindings - JAX-RS resources that need dependency injection
        bind(AquariumManagerResource.class).in(Singleton.class);
        bind(AccessoryResource.class).in(Singleton.class);
        bind(InhabitantResource.class).in(Singleton.class);
        bind(OrnamentResource.class).in(Singleton.class);
        
        // Filter bindings - so HK2 can inject dependencies
        bind(AquariumSecurityFilter.class).in(Singleton.class);
        bind(OwnershipFilter.class).in(Singleton.class);
    }
}
