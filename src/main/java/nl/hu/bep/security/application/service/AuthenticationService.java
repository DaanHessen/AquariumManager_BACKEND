package nl.hu.bep.security.application.service;

import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.data.OwnerRepository;
import nl.hu.bep.domain.Owner;
import nl.hu.bep.exception.security.SecurityException;
import nl.hu.bep.presentation.dto.security.AuthRequest;
import nl.hu.bep.security.model.AuthResponse;
import nl.hu.bep.security.model.RegisterRequest;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@Slf4j
public class AuthenticationService {
    private final JwtService jwtService;
    private final OwnerRepository ownerRepository;

    public AuthenticationService() {
        this.jwtService = new JwtService();
        this.ownerRepository = new OwnerRepository();
    }

    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration request for: {}", request.email());

        Optional<Owner> existingOwner = ownerRepository.findByEmail(request.email());
        if (existingOwner.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        Owner owner = Owner.create(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password());

        owner = ownerRepository.insert(owner);
        log.info("New owner registered with ID: {}", owner.getId());

        String token = jwtService.generateToken(owner.getId(), owner.getEmail());
        log.info("JWT token generated for user: {}", owner.getEmail());
        return new AuthResponse(owner.getId(), token);
    }

    public AuthResponse authenticate(AuthRequest request) {
        log.info("Processing authentication request for: {}", request.email());

        Owner owner = ownerRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed: No user found with email: {}", request.email());
                    return new SecurityException.AuthenticationException("Invalid email or password");
                });

        log.debug("Found owner with ID: {}", owner.getId());

        boolean passwordMatches = BCrypt.checkpw(request.password(), owner.getPassword());
        if (!passwordMatches) {
            log.warn("Login failed: Invalid password for email: {}", request.email());
            throw new SecurityException.AuthenticationException("Invalid email or password");
        }

        log.info("User authenticated successfully: {}", owner.getEmail());

        // Domain method handles login recording
        owner.recordLogin();
        ownerRepository.update(owner);

        String token = jwtService.generateToken(owner.getId(), owner.getEmail());
        log.info("JWT token generated for user: {}", owner.getEmail());
        return new AuthResponse(owner.getId(), token);
    }

    public Long validateTokenAndGetOwnerId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("Invalid token format or missing token");
            throw new SecurityException.TokenException("Authentication required");
        }

        try {
            String jwt = token.substring(7);
            Long ownerId = jwtService.extractOwnerId(jwt);
            log.debug("Successfully validated token for owner ID: {}", ownerId);
            return ownerId;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            throw new SecurityException.TokenException("Invalid authentication token");
        }
    }
}