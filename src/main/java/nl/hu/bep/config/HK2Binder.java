package nl.hu.bep.config;

import jakarta.inject.Singleton;
import nl.hu.bep.application.service.*;
import nl.hu.bep.data.*;
import nl.hu.bep.data.interfaces.*;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.security.application.service.AuthenticationService;
import nl.hu.bep.security.application.service.JwtService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class HK2Binder extends AbstractBinder {

    @Override
    protected void configure() {
        bindFactory(DatabaseManagerFactory.class).to(DatabaseManager.class).in(Singleton.class);

        bind(OwnerRepositoryImpl.class).to(OwnerRepository.class).in(Singleton.class);
        bindAsContract(OwnerRepositoryImpl.class).in(Singleton.class); // Also bind concrete class for AuthenticationService
        bind(AquariumRepositoryImpl.class).to(AquariumRepository.class).in(Singleton.class);
        bind(AccessoryRepositoryImpl.class).to(AccessoryRepository.class).in(Singleton.class);
        bind(InhabitantRepositoryImpl.class).to(InhabitantRepository.class).in(Singleton.class);
        bind(OrnamentRepositoryImpl.class).to(OrnamentRepository.class).in(Singleton.class);

        bindAsContract(AuthenticationService.class).in(Singleton.class);
        bindAsContract(JwtService.class).in(Singleton.class);
        bindAsContract(AquariumService.class).in(Singleton.class);
        bindAsContract(AccessoryService.class).in(Singleton.class);
        bindAsContract(InhabitantService.class).in(Singleton.class);
        bindAsContract(OrnamentService.class).in(Singleton.class);
        
        bindAsContract(EntityMapper.class).in(Singleton.class);
    }
}
