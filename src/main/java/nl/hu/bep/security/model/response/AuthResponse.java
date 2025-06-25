package nl.hu.bep.security.model.response;

public record AuthResponse(
    Long ownerId,
    String token
) { } 