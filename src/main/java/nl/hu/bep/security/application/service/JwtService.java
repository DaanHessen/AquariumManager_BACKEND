package nl.hu.bep.security.application.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.exception.ApplicationException;

import java.util.Date;

@Slf4j
public class JwtService {
    private static final String SECRET_KEY = "your_aquarium_jwt_secret_key";

    private static final String ISSUER = "aquarium-api";

    private static final long TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    public String generateToken(Long ownerId, String username) {
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .withSubject(String.valueOf(ownerId))
                .withClaim("username", username)
                .sign(Algorithm.HMAC256(SECRET_KEY));

        log.info("Generated JWT token for owner: {} (ID: {})", username, ownerId);
        return token;
    }

    public Long extractOwnerId(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .withIssuer(ISSUER)
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            log.debug("Verified JWT token for owner ID: {}", jwt.getSubject());

            return Long.parseLong(jwt.getSubject());
        } catch (Exception e) {
            throw new ApplicationException.SecurityException.TokenException("Invalid JWT token: " + e.getMessage());
        }
    }

    public String extractUsername(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .withIssuer(ISSUER)
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("username").asString();
        } catch (Exception e) {
            throw new ApplicationException.SecurityException.TokenException("Invalid JWT token: " + e.getMessage());
        }
    }

    public DecodedJWT verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .withIssuer(ISSUER)
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            log.debug("Successfully verified JWT token");
            return jwt;
        } catch (Exception e) {
            log.error("Failed to verify JWT token: {}", e.getMessage());
            throw new ApplicationException.SecurityException.TokenException("Invalid JWT token: " + e.getMessage());
        }
    }
}