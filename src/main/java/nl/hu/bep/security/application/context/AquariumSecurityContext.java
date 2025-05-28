package nl.hu.bep.security.application.context;

import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RequiredArgsConstructor
public class AquariumSecurityContext implements SecurityContext {
    private final Long ownerId;
    private final String username;

    @Override
    public Principal getUserPrincipal() {
        return () -> String.valueOf(ownerId);
    }

    @Override
    public boolean isUserInRole(String role) {
        return true;
    }

    @Override
    public boolean isSecure() {
        // always return true because security isn't the focus of this assignment and I don't want to deal with it
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getUsername() {
        return username;
    }
} 