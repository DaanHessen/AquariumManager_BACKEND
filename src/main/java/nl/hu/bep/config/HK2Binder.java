package nl.hu.bep.config;

import nl.hu.bep.application.service.AquariumManagerService;
import nl.hu.bep.application.factory.InhabitantFactory;
import nl.hu.bep.data.AccessoryRepositoryImpl;
import nl.hu.bep.data.AquariumRepositoryImpl;
import nl.hu.bep.data.InhabitantRepositoryImpl;
import nl.hu.bep.data.OrnamentRepositoryImpl;
import nl.hu.bep.data.OwnerRepositoryImpl;
import nl.hu.bep.data.interfaces.AccessoryRepository;
import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.data.interfaces.InhabitantRepository;
import nl.hu.bep.data.interfaces.OrnamentRepository;
import nl.hu.bep.data.interfaces.OwnerRepository;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.resource.AccessoryResource;
import nl.hu.bep.presentation.resource.AquariumManagerResource;
import nl.hu.bep.presentation.resource.InhabitantResource;
import nl.hu.bep.presentation.resource.OrnamentResource;
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
        bind(InhabitantFactory.class).in(Singleton.class);
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
