package nl.hu.bep.presentation.dto.request;

/**
 * Authentication request DTO.
 * Properly organized by bounded context.
 */
public record AuthRequest(
        String email,
        String password
) {
    // Validation can be added here for presentation layer concerns
    public AuthRequest {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
} 