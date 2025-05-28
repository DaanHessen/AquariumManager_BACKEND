package nl.hu.bep.security.model;

public record AuthResponse(
    Long ownerId,
    String token
) { } 