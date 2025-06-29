package nl.hu.bep.common;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import nl.hu.bep.data.interfaces.*;
import nl.hu.bep.security.application.service.AuthenticationService;
import nl.hu.bep.security.application.service.JwtService;

@Singleton
public class TestHelper {
    @Inject
    public JwtService jwtService;

    @Inject
    public AuthenticationService mockAuthService;

    @Inject
    public OwnerRepository mockOwnerRepository;

    @Inject
    public AquariumRepository mockAquariumRepository;

    @Inject
    public AccessoryRepository mockAccessoryRepository;

    @Inject
    public InhabitantRepository mockInhabitantRepository;

    @Inject
    public OrnamentRepository mockOrnamentRepository;
} 