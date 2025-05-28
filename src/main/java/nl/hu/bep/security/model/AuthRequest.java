package nl.hu.bep.security.model;

public record AuthRequest(
    String email,
    String password
) { } 