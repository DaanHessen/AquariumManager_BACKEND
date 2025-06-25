package nl.hu.bep.security.model.request;

public record AuthRequest(
    String email,
    String password
) { } 