package nl.hu.bep.security.model;

public record RegisterRequest(
    String firstName,
    String lastName,
    String email,
    String password
) { } 