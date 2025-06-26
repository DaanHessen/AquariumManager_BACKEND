package nl.hu.bep.presentation.dto.request;

public record AuthRequest(
        String email,
        String password
) {
    public AuthRequest {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
} 