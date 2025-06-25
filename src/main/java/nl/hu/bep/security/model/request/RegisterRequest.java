package nl.hu.bep.security.model.request;

public record RegisterRequest(
    String firstName,
    String lastName,
    String email,
    String password
) { } 