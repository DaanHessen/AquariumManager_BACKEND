package nl.hu.bep.presentation.dto.request;

import nl.hu.bep.exception.ApplicationException.BusinessRuleException;

public record AuthRequest(
        String email,
        String password
) {
    public AuthRequest {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessRuleException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessRuleException("Password cannot be empty");
        }
    }
} 