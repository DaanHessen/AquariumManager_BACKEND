package nl.hu.bep.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import nl.hu.bep.application.service.AquariumManagerService;
import nl.hu.bep.data.*;
import nl.hu.bep.data.interfaces.*;
import jakarta.inject.Singleton;

public class HK2Binder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(AquariumRepositoryImpl.class).to(AquariumRepository.class).in(Singleton.class);
        bind(AccessoryRepositoryImpl.class).to(AccessoryRepository.class).in(Singleton.class);
        bind(InhabitantRepositoryImpl.class).to(InhabitantRepository.class).in(Singleton.class);
        bind(OrnamentRepositoryImpl.class).to(OrnamentRepository.class).in(Singleton.class);
        bind(OwnerRepositoryImpl.class).in(Singleton.class);
        
        bind(AquariumRepositoryImpl.class).in(Singleton.class);
        bind(AccessoryRepositoryImpl.class).in(Singleton.class);
        bind(InhabitantRepositoryImpl.class).in(Singleton.class);
        bind(OrnamentRepositoryImpl.class).in(Singleton.class);
        
        bind(AquariumManagerService.class).in(Singleton.class);
    }
}
