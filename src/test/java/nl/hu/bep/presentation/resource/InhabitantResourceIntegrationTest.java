package nl.hu.bep.presentation.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.hu.bep.application.service.AquariumManagerService;
import nl.hu.bep.config.JacksonConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InhabitantResource Integration Tests")
class InhabitantResourceIntegrationTest extends JerseyTest {

    @Mock
    private AquariumManagerService mockAquariumManagerService;

    private ObjectMapper objectMapper;

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        
        // Register the resource with mocked service
        config.register(new InhabitantResource(mockAquariumManagerService));
        
        // Register Jackson for JSON processing
        config.register(JacksonConfig.class);
        
        return config;
    }

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        reset(mockAquariumManagerService);
    }

    @Nested
    @DisplayName("GET /inhabitants - Get All Inhabitants")
    class GetAllInhabitantsTests {

        @Test
        @DisplayName("Should return 500 when no authentication (integration behavior)")
        void shouldReturn500WhenNoAuth() {
            // Act
            Response response = target("/inhabitants")
                .request(MediaType.APPLICATION_JSON)
                .get();

            // Assert - Should return 500 due to authentication exception
            assertEquals(500, response.getStatus());
        }

        @Test
        @DisplayName("Should return 500 with mock authentication token (integration behavior)")  
        void shouldReturn500WithMockAuth() {
            // Arrange - No stubbing needed as authentication will fail first

            // Act
            Response response = target("/inhabitants")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer mock-token")
                .get();

            // Assert - Will still return 500 due to SecurityContextHelper not finding authenticated user
            assertEquals(500, response.getStatus());
        }
    }

    @Nested
    @DisplayName("GET /inhabitants/{id} - Get Single Inhabitant")
    class GetSingleInhabitantTests {

        @Test
        @DisplayName("Should return 500 for authentication issues")
        void shouldReturn500ForAuthIssues() {
            // Act
            Response response = target("/inhabitants/1")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer mock-token")
                .get();

            // Assert
            assertEquals(500, response.getStatus());
        }

        @Test
        @DisplayName("Should handle path parameter validation")
        void shouldHandleInvalidPathParam() {
            // Act
            Response response = target("/inhabitants/invalid")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer mock-token")
                .get();

            // Assert - Should return 404 or 400 for invalid path parameter
            assertTrue(response.getStatus() >= 400);
        }
    }

    @Nested
    @DisplayName("HTTP Protocol Tests")
    class HttpProtocolTests {

        @Test
        @DisplayName("Should handle unsupported HTTP methods")
        void shouldHandleUnsupportedMethods() {
            // Act - Try OPTIONS which should not be supported for this resource
            Response response = target("/inhabitants")
                .request(MediaType.APPLICATION_JSON)
                .options();

            // Assert - Should return 405 Method Not Allowed or similar
            assertTrue(response.getStatus() >= 400);
        }

        @Test
        @DisplayName("Should handle wrong media type")
        void shouldHandleWrongMediaType() {
            // Act
            Response response = target("/inhabitants")
                .request(MediaType.APPLICATION_XML) // Request XML but resource only supports JSON
                .header("Authorization", "Bearer mock-token")
                .get();

            // Assert - Should return 406 Not Acceptable
            assertEquals(406, response.getStatus());
        }

        @Test
        @DisplayName("Should handle OPTIONS request")
        void shouldHandleOptionsRequest() {
            // Act
            Response response = target("/inhabitants")
                .request()
                .options();

            // Assert - Should handle OPTIONS request for CORS
            assertTrue(response.getStatus() == 200 || response.getStatus() == 204);
        }
    }
}
