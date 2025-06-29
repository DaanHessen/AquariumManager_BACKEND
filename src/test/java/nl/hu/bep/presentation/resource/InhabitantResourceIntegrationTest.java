package nl.hu.bep.presentation.resource;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.hu.bep.common.TestBinder;
import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.data.interfaces.InhabitantRepository;
import nl.hu.bep.data.interfaces.OwnerRepository;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.Owner;
import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.GlobalExceptionMapper;
import nl.hu.bep.presentation.dto.request.InhabitantRequest;
import nl.hu.bep.security.application.filter.AquariumSecurityFilter;
import nl.hu.bep.security.application.service.JwtService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("InhabitantResource Integration Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InhabitantResourceIntegrationTest extends JerseyTest {

    private OwnerRepository ownerRepository;
    private AquariumRepository aquariumRepository;
    private InhabitantRepository inhabitantRepository;
    private JwtService jwtService;

    private String validAuthHeader;
    private Long testOwnerId;
    private Long testAquariumId;

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig(InhabitantResource.class, AquariumSecurityFilter.class, GlobalExceptionMapper.class);
        config.register(new TestBinder());
        return config;
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        // Use direct instantiation as workaround for Jersey 3.x service locator changes
        var databaseManager = new nl.hu.bep.common.TestDatabaseManagerFactory().provide();
        
        this.ownerRepository = new nl.hu.bep.data.OwnerRepositoryImpl(databaseManager);
        this.aquariumRepository = new nl.hu.bep.data.AquariumRepositoryImpl(databaseManager);
        this.inhabitantRepository = new nl.hu.bep.data.InhabitantRepositoryImpl(databaseManager);
        this.jwtService = new JwtService();

        Owner owner = Owner.create("Test", "Owner", "inhabitant-owner@test.com", "password");
        owner = ownerRepository.insert(owner);
        testOwnerId = owner.getId();
        validAuthHeader = "Bearer " + jwtService.generateToken(testOwnerId, owner.getEmail());

        Aquarium aquarium = Aquarium.create("My Test Tank", 50, 30, 30, SubstrateType.SAND, WaterType.FRESHWATER, "blue", "A simple test tank", AquariumState.RUNNING);
        aquarium.assignToOwner(testOwnerId);
        aquarium = aquariumRepository.insert(aquarium);
        testAquariumId = aquarium.getId();
    }
    
    @AfterAll
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private InhabitantRequest createValidInhabitantRequest(Long aquariumId) {
        return new InhabitantRequest("Clownfish", "ORANGE", "A classic clownfish", 1, false, WaterType.SALTWATER, "fish", aquariumId, false, false, false, "Nemo", 1, "MALE", 8.2, 25.0, 120.0, 2, 1.025);
    }

    @Test
    @DisplayName("POST /inhabitants - should create a new inhabitant")
    void shouldCreateInhabitant() {
        // Arrange
        InhabitantRequest request = createValidInhabitantRequest(testAquariumId);

        // Act
        Response response = target("/inhabitants")
            .request(MediaType.APPLICATION_JSON)
            .header("Authorization", validAuthHeader)
            .post(Entity.entity(request, MediaType.APPLICATION_JSON));

        // Assert
        assertEquals(201, response.getStatus());
        long count = inhabitantRepository.findAll().stream()
            .filter(i -> "Clownfish".equals(i.getSpecies()))
            .count();
        assertEquals(1, count);
    }
}
