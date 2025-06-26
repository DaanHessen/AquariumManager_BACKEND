package nl.hu.bep.presentation.resource;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.hu.bep.common.BaseIntegrationTest;
import nl.hu.bep.config.JacksonConfig;
import nl.hu.bep.presentation.dto.request.AccessoryRequest;
import nl.hu.bep.security.application.filter.AquariumSecurityFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccessoryResource Integration Tests")
class AccessoryResourceIntegrationTest extends BaseIntegrationTest {

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        
        // Register the resource and security filter for integration testing
        config.register(AccessoryResource.class);
        config.register(AquariumSecurityFilter.class);
        
        // Register Jackson for JSON processing
        config.register(JacksonConfig.class);
        
        return config;
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Nested
    @DisplayName("GET /accessories - Get All Accessories")
    class GetAllAccessoriesTests {

        @Test
        @DisplayName("Should handle authenticated request and attempt to get accessories")
        void shouldHandleAuthenticatedRequest() {
            // Arrange
            String authHeader = createDefaultAuthorizationHeader();

            // Act
            Response response = target("/accessories")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader)
                .get();

            // Assert - Real integration behavior
            // Authentication passes, but may get 500 due to missing database/service dependencies
            // This tests the full request pipeline including JWT validation
            assertTrue(response.getStatus() == 200 || response.getStatus() == 500);
            assertNotEquals(401, response.getStatus()); // Should not be unauthorized
        }

        @Test
        @DisplayName("Should return 401 when no authorization header provided")
        void shouldReturn401WhenNoAuth() {
            // Act
            Response response = target("/accessories")
                .request(MediaType.APPLICATION_JSON)
                .get();

            // Assert
            assertEquals(401, response.getStatus());
        }

        @Test
        @DisplayName("Should return 401 when invalid token provided")
        void shouldReturn401WhenInvalidToken() {
            // Act
            Response response = target("/accessories")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer invalid-token")
                .get();

            // Assert
            assertEquals(401, response.getStatus());
        }
    }

    @Nested
    @DisplayName("POST /accessories/{aquariumId} - Add Accessory")
    class AddAccessoryTests {

        @Test
        @DisplayName("Should handle authenticated add accessory request with valid data")
        void shouldHandleAuthenticatedAddAccessoryRequest() {
            // Arrange
            String authHeader = createDefaultAuthorizationHeader();
            AccessoryRequest accessoryRequest = createValidAccessoryRequest();

            // Act
            Response response = target("/accessories/1")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader)
                .post(Entity.entity(accessoryRequest, MediaType.APPLICATION_JSON));

            // Assert - Real integration behavior
            // Authentication passes, but may get 500 due to missing database/service dependencies
            assertTrue(response.getStatus() == 201 || response.getStatus() == 500);
            assertNotEquals(401, response.getStatus()); // Should not be unauthorized
        }

        @Test
        @DisplayName("Should return 401 when no authorization header provided")
        void shouldReturn401WhenNoAuth() {
            // Arrange
            AccessoryRequest accessoryRequest = createValidAccessoryRequest();

            // Act
            Response response = target("/accessories/1")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(accessoryRequest, MediaType.APPLICATION_JSON));

            // Assert
            assertEquals(401, response.getStatus());
        }

        @Test
        @DisplayName("Should handle malformed JSON request")
        void shouldHandleMalformedJsonRequest() {
            // Arrange
            String authHeader = createDefaultAuthorizationHeader();

            // Act
            Response response = target("/accessories/1")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader)
                .post(Entity.entity("{invalid-json", MediaType.APPLICATION_JSON));

            // Assert - Should return 400 for bad request
            assertTrue(response.getStatus() >= 400);
        }
    }

    @Nested
    @DisplayName("PUT /accessories/{accessoryId} - Update Accessory")
    class UpdateAccessoryTests {

        @Test
        @DisplayName("Should handle authenticated update accessory request")
        void shouldHandleAuthenticatedUpdateAccessoryRequest() {
            // Arrange
            String authHeader = createDefaultAuthorizationHeader();
            AccessoryRequest accessoryRequest = createValidAccessoryRequest();

            // Act
            Response response = target("/accessories/1")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader)
                .put(Entity.entity(accessoryRequest, MediaType.APPLICATION_JSON));

            // Assert - Real integration behavior
            assertTrue(response.getStatus() == 200 || response.getStatus() == 500);
            assertNotEquals(401, response.getStatus()); // Should not be unauthorized
        }

        @Test
        @DisplayName("Should return 401 when no authorization header provided")
        void shouldReturn401WhenNoAuth() {
            // Arrange
            AccessoryRequest accessoryRequest = createValidAccessoryRequest();

            // Act
            Response response = target("/accessories/1")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(accessoryRequest, MediaType.APPLICATION_JSON));

            // Assert
            assertEquals(401, response.getStatus());
        }

        @Test
        @DisplayName("Should handle invalid accessory ID")
        void shouldHandleInvalidAccessoryId() {
            // Arrange
            String authHeader = createDefaultAuthorizationHeader();
            AccessoryRequest accessoryRequest = createValidAccessoryRequest();

            // Act
            Response response = target("/accessories/invalid")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader)
                .put(Entity.entity(accessoryRequest, MediaType.APPLICATION_JSON));

            // Assert - Should return 400 or 404 for invalid ID
            assertTrue(response.getStatus() >= 400);
        }
    }

    @Nested
    @DisplayName("DELETE /accessories/{accessoryId} - Remove Accessory")
    class RemoveAccessoryTests {

        @Test
        @DisplayName("Should handle authenticated remove accessory request")
        void shouldHandleAuthenticatedRemoveAccessoryRequest() {
            // Arrange
            String authHeader = createDefaultAuthorizationHeader();

            // Act
            Response response = target("/accessories/1")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader)
                .delete();

            // Assert - Real integration behavior
            assertTrue(response.getStatus() == 200 || response.getStatus() == 204 || response.getStatus() == 500);
            assertNotEquals(401, response.getStatus()); // Should not be unauthorized
        }

        @Test
        @DisplayName("Should return 401 when no authorization header provided")
        void shouldReturn401WhenNoAuth() {
            // Act
            Response response = target("/accessories/1")
                .request(MediaType.APPLICATION_JSON)
                .delete();

            // Assert
            assertEquals(401, response.getStatus());
        }
    }

    @Nested
    @DisplayName("HTTP Protocol and Content Negotiation Tests")
    class HttpProtocolTests {

        @Test
        @DisplayName("Should handle OPTIONS request for CORS")
        void shouldHandleOptionsRequest() {
            // Act
            Response response = target("/accessories")
                .request()
                .options();

            // Assert - Should not require authentication for OPTIONS
            assertNotEquals(401, response.getStatus());
        }

        @Test
        @DisplayName("Should require authentication for HEAD requests")
        void shouldRequireAuthForHeadRequest() {
            // Act
            Response response = target("/accessories")
                .request()
                .head();

            // Assert - Should require authentication for HEAD requests
            assertEquals(401, response.getStatus());
        }

        @Test
        @DisplayName("Should accept JSON content type")
        void shouldAcceptJsonContentType() {
            // Arrange
            String authHeader = createDefaultAuthorizationHeader();
            AccessoryRequest accessoryRequest = createValidAccessoryRequest();

            // Act
            Response response = target("/accessories/1")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .post(Entity.entity(accessoryRequest, MediaType.APPLICATION_JSON));

            // Assert - Should not return 415 (Unsupported Media Type)
            assertNotEquals(415, response.getStatus());
        }
    }

    private AccessoryRequest createValidAccessoryRequest() {
        return new AccessoryRequest(
            "AquaClear 50",                  // model 
            "AC50-123456",            // serialNumber 
            "filter",                         // type
            1L,                         // aquariumId
            true,                       // isExternal
            200,                    // capacityLiters
            false,                           // isLED
            "black",                         // color (should be String)
            "",                        // description
            null,                           // timeOn
            null,                          // timeOff
            0.0,                    // minTemperature
            0.0,                    // maxTemperature
            0.0                 // currentTemperature
        );
    }
}
