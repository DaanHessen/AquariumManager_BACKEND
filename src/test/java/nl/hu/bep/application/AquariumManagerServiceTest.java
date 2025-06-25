package nl.hu.bep.application;

import nl.hu.bep.application.service.AquariumManagerService;
import nl.hu.bep.application.factory.InhabitantFactory;
import nl.hu.bep.data.interfaces.*;
import nl.hu.bep.domain.*;
import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.dto.request.AquariumRequest;
import nl.hu.bep.presentation.dto.response.AquariumResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AquariumManagerService Unit Tests")
class AquariumManagerServiceTest {

    @Mock
    private AquariumRepository aquariumRepository;
    
    @Mock
    private AccessoryRepository accessoryRepository;
    
    @Mock
    private OrnamentRepository ornamentRepository;
    
    @Mock
    private InhabitantRepository inhabitantRepository;
    
    @Mock
    private OwnerRepository ownerRepository;
    
    @Mock
    private EntityMapper entityMapper;
    
    @Mock
    private InhabitantFactory inhabitantFactory;

    private AquariumManagerService service;

    private static final Long OWNER_ID = 1L;
    private static final Long AQUARIUM_ID = 10L;
    private static final String AQUARIUM_NAME = "Test Aquarium";

    @BeforeEach
    void setUp() {
        service = new AquariumManagerService(
                aquariumRepository,
                accessoryRepository,
                ornamentRepository,
                inhabitantRepository,
                ownerRepository,
                entityMapper,
                inhabitantFactory
        );
    }

    @Nested
    @DisplayName("Aquarium Retrieval")
    class AquariumRetrieval {

        @Test
        @DisplayName("Should get all aquariums for owner")
        void shouldGetAllAquariumsForOwner() {
            // Arrange
            List<Aquarium> aquariums = List.of(
                    createTestAquarium(1L, "Aquarium 1"),
                    createTestAquarium(2L, "Aquarium 2")
            );
            List<AquariumResponse> expectedResponses = List.of(
                    createTestAquariumResponse(1L, "Aquarium 1"),
                    createTestAquariumResponse(2L, "Aquarium 2")
            );

            when(aquariumRepository.findByOwnerId(OWNER_ID)).thenReturn(aquariums);
            when(entityMapper.mapToAquariumResponses(aquariums)).thenReturn(expectedResponses);

            // Act
            List<AquariumResponse> result = service.getAllAquariums(OWNER_ID);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedResponses, result);
            
            verify(aquariumRepository).findByOwnerId(OWNER_ID);
            verify(entityMapper).mapToAquariumResponses(aquariums);
            verifyNoMoreInteractions(aquariumRepository, entityMapper);
        }

        @Test
        @DisplayName("Should get single aquarium by id")
        void shouldGetSingleAquariumById() {
            // Arrange
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, AQUARIUM_NAME);
            AquariumResponse expectedResponse = createTestAquariumResponse(AQUARIUM_ID, AQUARIUM_NAME);

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));
            when(entityMapper.mapToAquariumResponse(aquarium)).thenReturn(expectedResponse);

            // Act
            AquariumResponse result = service.getAquarium(AQUARIUM_ID, OWNER_ID);

            // Assert
            assertNotNull(result);
            assertEquals(expectedResponse, result);
            
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verify(entityMapper).mapToAquariumResponse(aquarium);
            verifyNoMoreInteractions(aquariumRepository, entityMapper);
        }

        @Test
        @DisplayName("Should throw exception when aquarium not found")
        void shouldThrowExceptionWhenAquariumNotFound() {
            // Arrange
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.empty());

            // Act & Assert
            ApplicationException.NotFoundException exception = assertThrows(
                    ApplicationException.NotFoundException.class,
                    () -> service.getAquarium(AQUARIUM_ID, OWNER_ID)
            );

            assertEquals("Aquarium with ID " + AQUARIUM_ID + " not found", exception.getMessage());
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verifyNoMoreInteractions(aquariumRepository);
            verifyNoInteractions(entityMapper);
        }

        @Test
        @DisplayName("Should validate ownership when getting aquarium")
        void shouldValidateOwnershipWhenGettingAquarium() {
            // Arrange
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, AQUARIUM_NAME);
            aquarium.assignToOwner(2L); // Different owner

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));

            // Act & Assert
            assertThrows(
                    nl.hu.bep.exception.domain.DomainException.class,
                    () -> service.getAquarium(AQUARIUM_ID, OWNER_ID)
            );

            verify(aquariumRepository).findById(AQUARIUM_ID);
            verifyNoMoreInteractions(aquariumRepository);
            verifyNoInteractions(entityMapper);
        }
    }

    @Nested
    @DisplayName("Aquarium Creation")
    class AquariumCreation {

        @Test
        @DisplayName("Should create aquarium with valid request")
        void shouldCreateAquariumWithValidRequest() {
            // Arrange
            AquariumRequest request = new AquariumRequest(
                    AQUARIUM_NAME,
                    100.0,
                    50.0,
                    60.0,
                    SubstrateType.SAND,
                    WaterType.FRESHWATER,
                    "blue",
                    "A test aquarium",
                    AquariumState.SETUP
            );

            Owner owner = mock(Owner.class);
            when(owner.getId()).thenReturn(OWNER_ID);
            Aquarium savedAquarium = createTestAquarium(AQUARIUM_ID, AQUARIUM_NAME);
            AquariumResponse expectedResponse = createTestAquariumResponse(AQUARIUM_ID, AQUARIUM_NAME);

            when(ownerRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner));
            when(aquariumRepository.insert(any(Aquarium.class))).thenReturn(savedAquarium);
            when(entityMapper.mapToAquariumResponse(savedAquarium)).thenReturn(expectedResponse);

            // Act
            AquariumResponse result = service.createAquarium(request, OWNER_ID);

            // Assert
            assertNotNull(result);
            assertEquals(expectedResponse, result);

            verify(ownerRepository).findById(OWNER_ID);
            verify(aquariumRepository).insert(any(Aquarium.class));
            verify(entityMapper).mapToAquariumResponse(savedAquarium);
        }

        @Test
        @DisplayName("Should throw exception when owner not found during creation")
        void shouldThrowExceptionWhenOwnerNotFoundDuringCreation() {
            // Arrange
            AquariumRequest request = new AquariumRequest(
                    AQUARIUM_NAME,
                    100.0,
                    50.0,
                    60.0,
                    SubstrateType.SAND,
                    WaterType.FRESHWATER,
                    "blue",
                    "A test aquarium",
                    AquariumState.SETUP
            );

            when(ownerRepository.findById(OWNER_ID)).thenReturn(Optional.empty());

            // Act & Assert
            ApplicationException.NotFoundException exception = assertThrows(
                    ApplicationException.NotFoundException.class,
                    () -> service.createAquarium(request, OWNER_ID)
            );

            assertEquals("Owner with ID " + OWNER_ID + " not found", exception.getMessage());
            verify(ownerRepository).findById(OWNER_ID);
            verifyNoInteractions(aquariumRepository, entityMapper);
        }
    }

    @Nested
    @DisplayName("Aquarium Updates")
    class AquariumUpdates {

        @Test
        @DisplayName("Should update aquarium with valid request")
        void shouldUpdateAquariumWithValidRequest() {
            // Arrange
            AquariumRequest updateRequest = new AquariumRequest(
                    "Updated Aquarium",
                    120.0,
                    60.0,
                    70.0,
                    SubstrateType.GRAVEL,
                    WaterType.SALTWATER,
                    "red",
                    "Updated description",
                    AquariumState.RUNNING
            );

            Aquarium existingAquarium = createTestAquarium(AQUARIUM_ID, AQUARIUM_NAME);
            Aquarium updatedAquarium = createTestAquarium(AQUARIUM_ID, "Updated Aquarium");
            AquariumResponse expectedResponse = createTestAquariumResponse(AQUARIUM_ID, "Updated Aquarium");

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(existingAquarium));
            when(aquariumRepository.update(existingAquarium)).thenReturn(updatedAquarium);
            when(entityMapper.mapToAquariumResponse(updatedAquarium)).thenReturn(expectedResponse);

            // Act
            AquariumResponse result = service.updateAquarium(AQUARIUM_ID, updateRequest, OWNER_ID);

            // Assert
            assertNotNull(result);
            assertEquals(expectedResponse, result);

            verify(aquariumRepository).findById(AQUARIUM_ID);
            verify(aquariumRepository).update(existingAquarium);
            verify(entityMapper).mapToAquariumResponse(updatedAquarium);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent aquarium")
        void shouldThrowExceptionWhenUpdatingNonExistentAquarium() {
            // Arrange
            AquariumRequest updateRequest = new AquariumRequest(
                    "Updated Aquarium",
                    120.0,
                    60.0,
                    70.0,
                    SubstrateType.GRAVEL,
                    WaterType.SALTWATER,
                    "red",
                    "Updated description",
                    AquariumState.RUNNING
            );

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.empty());

            // Act & Assert
            ApplicationException.NotFoundException exception = assertThrows(
                    ApplicationException.NotFoundException.class,
                    () -> service.updateAquarium(AQUARIUM_ID, updateRequest, OWNER_ID)
            );

            assertEquals("Aquarium with ID " + AQUARIUM_ID + " not found", exception.getMessage());
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verifyNoMoreInteractions(aquariumRepository);
            verifyNoInteractions(entityMapper);
        }
    }

    @Nested
    @DisplayName("Aquarium Deletion")
    class AquariumDeletion {

        @Test
        @DisplayName("Should delete aquarium when owner matches")
        void shouldDeleteAquariumWhenOwnerMatches() {
            // Arrange
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, AQUARIUM_NAME);

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));

            // Act
            service.deleteAquarium(AQUARIUM_ID, OWNER_ID);

            // Assert
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verify(aquariumRepository).deleteById(AQUARIUM_ID);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent aquarium")
        void shouldThrowExceptionWhenDeletingNonExistentAquarium() {
            // Arrange
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.empty());

            // Act & Assert
            ApplicationException.NotFoundException exception = assertThrows(
                    ApplicationException.NotFoundException.class,
                    () -> service.deleteAquarium(AQUARIUM_ID, OWNER_ID)
            );

            assertEquals("Aquarium with ID " + AQUARIUM_ID + " not found", exception.getMessage());
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verify(aquariumRepository, never()).deleteById(any());
        }
    }

    // Helper methods
    private Aquarium createTestAquarium(Long id, String name) {
        Aquarium aquarium = Aquarium.create(
                name,
                100.0,
                50.0,
                60.0,
                SubstrateType.SAND,
                WaterType.FRESHWATER,
                "blue",
                "Test aquarium",
                AquariumState.SETUP
        );
        aquarium.assignToOwner(OWNER_ID);
        return aquarium;
    }

    private AquariumResponse createTestAquariumResponse(Long id, String name) {
        return new AquariumResponse(
                id,
                name,
                100.0,
                50.0,
                60.0,
                300.0, // volumeInLiters
                SubstrateType.SAND,
                WaterType.FRESHWATER,
                AquariumState.SETUP,
                java.time.LocalDateTime.now(), // currentStateStartTime
                0L, // currentStateDurationMinutes
                OWNER_ID,
                "owner@test.com", // ownerEmail
                List.of(), // inhabitants
                List.of(), // accessories
                List.of(), // ornaments
                "blue",
                "Test aquarium",
                java.time.LocalDateTime.now() // dateCreated
        );
    }
}
