package nl.hu.bep.common;

import nl.hu.bep.security.application.service.JwtService;
import org.glassfish.jersey.test.JerseyTest;

public abstract class BaseIntegrationTest extends JerseyTest {
    
    private static final JwtService jwtService = new JwtService();
    
    protected String generateValidJwtToken(Long ownerId, String username) {
        return jwtService.generateToken(ownerId, username);
    }
    
    protected String createAuthorizationHeader(Long ownerId, String username) {
        String token = generateValidJwtToken(ownerId, username);
        return "Bearer " + token;
    }
    
    protected static final Long TEST_USER_ID = 123L;
    
    protected static final String TEST_USERNAME = "testuser@example.com";
    
    protected String createDefaultAuthorizationHeader() {
        return createAuthorizationHeader(TEST_USER_ID, TEST_USERNAME);
    }
}
