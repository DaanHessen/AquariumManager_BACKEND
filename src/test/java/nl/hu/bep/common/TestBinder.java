package nl.hu.bep.common;

import nl.hu.bep.application.service.AccessoryService;
import nl.hu.bep.application.service.AquariumService;
import nl.hu.bep.application.service.InhabitantService;
import nl.hu.bep.application.service.OrnamentService;
import nl.hu.bep.config.DatabaseManager;
import nl.hu.bep.data.AccessoryRepositoryImpl;
import nl.hu.bep.data.AquariumRepositoryImpl;
import nl.hu.bep.data.InhabitantRepositoryImpl;
import nl.hu.bep.data.OrnamentRepositoryImpl;
import nl.hu.bep.data.OwnerRepositoryImpl;
import nl.hu.bep.data.interfaces.*;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.security.application.service.AuthenticationService;
import nl.hu.bep.security.application.service.JwtService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import jakarta.inject.Singleton;

public class TestBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bindFactory(TestDatabaseManagerFactory.class).to(DatabaseManager.class).in(Singleton.class);

        bind(AquariumRepositoryImpl.class).to(AquariumRepository.class).in(Singleton.class);
        bind(AccessoryRepositoryImpl.class).to(AccessoryRepository.class).in(Singleton.class);
        bind(InhabitantRepositoryImpl.class).to(InhabitantRepository.class).in(Singleton.class);
        bind(OrnamentRepositoryImpl.class).to(OrnamentRepository.class).in(Singleton.class);
        bind(OwnerRepositoryImpl.class).to(OwnerRepository.class).in(Singleton.class);

        bindAsContract(AquariumService.class).in(Singleton.class);
        bindAsContract(AccessoryService.class).in(Singleton.class);
        bindAsContract(InhabitantService.class).in(Singleton.class);
        bindAsContract(OrnamentService.class).in(Singleton.class);
        bindAsContract(AuthenticationService.class).in(Singleton.class);

        bindAsContract(JwtService.class).in(Singleton.class);
        bindAsContract(EntityMapper.class).in(Singleton.class);

        bindAsContract(TestHelper.class).in(Singleton.class);
    }
} 