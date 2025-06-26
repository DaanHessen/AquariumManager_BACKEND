package nl.hu.bep.application;

import nl.hu.bep.application.service.AquariumManagerService;

import nl.hu.bep.data.interfaces.*;
import nl.hu.bep.domain.*;
import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.dto.request.AquariumRequest;
import nl.hu.bep.presentation.dto.response.AquariumResponse;
import nl.hu.bep.presentation.dto.response.AccessoryResponse;
import nl.hu.bep.presentation.dto.response.OrnamentResponse;
import nl.hu.bep.presentation.dto.response.InhabitantResponse;

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
                entityMapper
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
                    ApplicationException.BusinessRuleException.class,
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

    @Nested
    @DisplayName("Accessory Management")
    class AccessoryManagement {

        @Test
        @DisplayName("Should get all accessories for owner")
        void shouldGetAllAccessoriesForOwner() {
            // Arrange
            List<Accessory> accessories = List.of(
                    mock(Accessory.class),
                    mock(Accessory.class)
            );
            List<AccessoryResponse> expectedResponses = List.of(
                    mock(AccessoryResponse.class),
                    mock(AccessoryResponse.class)
            );

            when(accessoryRepository.findByOwnerId(OWNER_ID)).thenReturn(accessories);
            when(entityMapper.mapToAccessoryResponse(accessories.get(0))).thenReturn(expectedResponses.get(0));
            when(entityMapper.mapToAccessoryResponse(accessories.get(1))).thenReturn(expectedResponses.get(1));

            // Act
            List<AccessoryResponse> result = service.getAllAccessories(OWNER_ID);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedResponses, result);

            verify(accessoryRepository).findByOwnerId(OWNER_ID);
            verify(entityMapper, times(2)).mapToAccessoryResponse(any(Accessory.class));
        }

        @Test
        @DisplayName("Should get single accessory by id")
        void shouldGetSingleAccessoryById() {
            // Arrange
            Long accessoryId = 100L;
            Accessory accessory = mock(Accessory.class);
            AccessoryResponse expectedResponse = mock(AccessoryResponse.class);

            when(accessoryRepository.findById(accessoryId)).thenReturn(Optional.of(accessory));
            when(entityMapper.mapToAccessoryResponse(accessory)).thenReturn(expectedResponse);

            // Act
            AccessoryResponse result = service.getAccessory(accessoryId, OWNER_ID);

            // Assert
            assertNotNull(result);
            assertEquals(expectedResponse, result);

            verify(accessoryRepository).findById(accessoryId);
            verify(accessory).validateOwnership(OWNER_ID);
            verify(entityMapper).mapToAccessoryResponse(accessory);
        }

        @Test
        @DisplayName("Should throw exception when accessory not found")
        void shouldThrowExceptionWhenAccessoryNotFound() {
            // Arrange
            Long accessoryId = 999L;
            when(accessoryRepository.findById(accessoryId)).thenReturn(Optional.empty());

            // Act & Assert
            ApplicationException.NotFoundException exception = assertThrows(
                    ApplicationException.NotFoundException.class,
                    () -> service.getAccessory(accessoryId, OWNER_ID)
            );

            assertEquals("Accessory with ID " + accessoryId + " not found", exception.getMessage());
            verify(accessoryRepository).findById(accessoryId);
            verifyNoInteractions(entityMapper);
        }

        @Test
        @DisplayName("Should get accessories by aquarium")
        void shouldGetAccessoriesByAquarium() {
            // Arrange
            List<Accessory> accessories = List.of(mock(Accessory.class));
            List<AccessoryResponse> expectedResponses = List.of(mock(AccessoryResponse.class));

            when(accessoryRepository.findByAquariumId(AQUARIUM_ID)).thenReturn(accessories);
            when(entityMapper.mapToAccessoryResponse(accessories.get(0))).thenReturn(expectedResponses.get(0));

            // Act
            List<AccessoryResponse> result = service.getAccessoriesByAquarium(AQUARIUM_ID);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedResponses, result);

            verify(accessoryRepository).findByAquariumId(AQUARIUM_ID);
            verify(entityMapper).mapToAccessoryResponse(accessories.get(0));
        }
    }

    @Nested
    @DisplayName("Ornament Management")
    class OrnamentManagement {

        @Test
        @DisplayName("Should get all ornaments for owner")
        void shouldGetAllOrnamentsForOwner() {
            // Arrange
            List<Ornament> ornaments = List.of(
                    mock(Ornament.class),
                    mock(Ornament.class)
            );
            List<OrnamentResponse> expectedResponses = List.of(
                    mock(OrnamentResponse.class),
                    mock(OrnamentResponse.class)
            );

            when(ornamentRepository.findByOwnerId(OWNER_ID)).thenReturn(ornaments);
            when(entityMapper.mapToOrnamentResponse(ornaments.get(0))).thenReturn(expectedResponses.get(0));
            when(entityMapper.mapToOrnamentResponse(ornaments.get(1))).thenReturn(expectedResponses.get(1));

            // Act
            List<OrnamentResponse> result = service.getAllOrnaments(OWNER_ID);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedResponses, result);

            verify(ornamentRepository).findByOwnerId(OWNER_ID);
            verify(entityMapper, times(2)).mapToOrnamentResponse(any(Ornament.class));
        }

        @Test
        @DisplayName("Should get single ornament by id")
        void shouldGetSingleOrnamentById() {
            // Arrange
            Long ornamentId = 200L;
            Ornament ornament = mock(Ornament.class);
            OrnamentResponse expectedResponse = mock(OrnamentResponse.class);

            when(ornamentRepository.findById(ornamentId)).thenReturn(Optional.of(ornament));
            when(entityMapper.mapToOrnamentResponse(ornament)).thenReturn(expectedResponse);

            // Act
            OrnamentResponse result = service.getOrnament(ornamentId, OWNER_ID);

            // Assert
            assertNotNull(result);
            assertEquals(expectedResponse, result);

            verify(ornamentRepository).findById(ornamentId);
            verify(ornament).validateOwnership(OWNER_ID);
            verify(entityMapper).mapToOrnamentResponse(ornament);
        }

        @Test
        @DisplayName("Should throw exception when ornament not found")
        void shouldThrowExceptionWhenOrnamentNotFound() {
            // Arrange
            Long ornamentId = 999L;
            when(ornamentRepository.findById(ornamentId)).thenReturn(Optional.empty());

            // Act & Assert
            ApplicationException.NotFoundException exception = assertThrows(
                    ApplicationException.NotFoundException.class,
                    () -> service.getOrnament(ornamentId, OWNER_ID)
            );

            assertEquals("Ornament with ID " + ornamentId + " not found", exception.getMessage());
            verify(ornamentRepository).findById(ornamentId);
            verifyNoInteractions(entityMapper);
        }

        @Test
        @DisplayName("Should get ornaments by aquarium")
        void shouldGetOrnamentsByAquarium() {
            // Arrange
            List<Ornament> ornaments = List.of(mock(Ornament.class));
            List<OrnamentResponse> expectedResponses = List.of(mock(OrnamentResponse.class));

            when(ornamentRepository.findByAquariumId(AQUARIUM_ID)).thenReturn(ornaments);
            when(entityMapper.mapToOrnamentResponse(ornaments.get(0))).thenReturn(expectedResponses.get(0));

            // Act
            List<OrnamentResponse> result = service.getOrnamentsByAquarium(AQUARIUM_ID);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedResponses, result);

            verify(ornamentRepository).findByAquariumId(AQUARIUM_ID);
            verify(entityMapper).mapToOrnamentResponse(ornaments.get(0));
        }
    }

    @Nested
    @DisplayName("Inhabitant Management")
    class InhabitantManagement {

        @Test
        @DisplayName("Should get all inhabitants for owner")
        void shouldGetAllInhabitantsForOwner() {
            // Arrange
            List<Inhabitant> inhabitants = List.of(
                    mock(Inhabitant.class),
                    mock(Inhabitant.class)
            );
            List<InhabitantResponse> expectedResponses = List.of(
                    mock(InhabitantResponse.class),
                    mock(InhabitantResponse.class)
            );

            when(inhabitantRepository.findByOwnerId(OWNER_ID)).thenReturn(inhabitants);
            when(entityMapper.mapToInhabitantResponse(inhabitants.get(0))).thenReturn(expectedResponses.get(0));
            when(entityMapper.mapToInhabitantResponse(inhabitants.get(1))).thenReturn(expectedResponses.get(1));

            // Act
            List<InhabitantResponse> result = service.getAllInhabitants(OWNER_ID);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(expectedResponses, result);

            verify(inhabitantRepository).findByOwnerId(OWNER_ID);
            verify(entityMapper, times(2)).mapToInhabitantResponse(any(Inhabitant.class));
        }

        @Test
        @DisplayName("Should get single inhabitant by id")
        void shouldGetSingleInhabitantById() {
            // Arrange
            Long inhabitantId = 300L;
            Inhabitant inhabitant = mock(Inhabitant.class);
            InhabitantResponse expectedResponse = mock(InhabitantResponse.class);

            when(inhabitantRepository.findById(inhabitantId)).thenReturn(Optional.of(inhabitant));
            when(entityMapper.mapToInhabitantResponse(inhabitant)).thenReturn(expectedResponse);

            // Act
            InhabitantResponse result = service.getInhabitant(inhabitantId, OWNER_ID);

            // Assert
            assertNotNull(result);
            assertEquals(expectedResponse, result);

            verify(inhabitantRepository).findById(inhabitantId);
            verify(inhabitant).validateOwnership(OWNER_ID);
            verify(entityMapper).mapToInhabitantResponse(inhabitant);
        }

        @Test
        @DisplayName("Should throw exception when inhabitant not found")
        void shouldThrowExceptionWhenInhabitantNotFound() {
            // Arrange
            Long inhabitantId = 999L;
            when(inhabitantRepository.findById(inhabitantId)).thenReturn(Optional.empty());

            // Act & Assert
            ApplicationException.NotFoundException exception = assertThrows(
                    ApplicationException.NotFoundException.class,
                    () -> service.getInhabitant(inhabitantId, OWNER_ID)
            );

            assertEquals("Inhabitant with ID " + inhabitantId + " not found", exception.getMessage());
            verify(inhabitantRepository).findById(inhabitantId);
            verifyNoInteractions(entityMapper);
        }

        @Test
        @DisplayName("Should get inhabitants by aquarium")
        void shouldGetInhabitantsByAquarium() {
            // Arrange
            List<Inhabitant> inhabitants = List.of(mock(Inhabitant.class));
            List<InhabitantResponse> expectedResponses = List.of(mock(InhabitantResponse.class));

            when(inhabitantRepository.findByAquariumId(AQUARIUM_ID)).thenReturn(inhabitants);
            when(entityMapper.mapToInhabitantResponse(inhabitants.get(0))).thenReturn(expectedResponses.get(0));

            // Act
            List<InhabitantResponse> result = service.getInhabitantsByAquarium(AQUARIUM_ID);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(expectedResponses, result);

            verify(inhabitantRepository).findByAquariumId(AQUARIUM_ID);
            verify(entityMapper).mapToInhabitantResponse(inhabitants.get(0));
        }
    }

    @Nested
    @DisplayName("Owner Management")
    class OwnerManagement {

        @Test
        @DisplayName("Should find owner by email")
        void shouldFindOwnerByEmail() {
            // Arrange
            String email = "test@example.com";
            Owner expectedOwner = mock(Owner.class);

            when(ownerRepository.findByEmail(email)).thenReturn(Optional.of(expectedOwner));

            // Act
            Owner result = service.findOwnerByEmail(email);

            // Assert
            assertNotNull(result);
            assertEquals(expectedOwner, result);

            verify(ownerRepository).findByEmail(email);
        }

        @Test
        @DisplayName("Should return null when owner not found by email")
        void shouldReturnNullWhenOwnerNotFoundByEmail() {
            // Arrange
            String email = "notfound@example.com";

            when(ownerRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act
            Owner result = service.findOwnerByEmail(email);

            // Assert
            assertNull(result);

            verify(ownerRepository).findByEmail(email);
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
                SubstrateType.SAND,
                WaterType.FRESHWATER,
                25.0, // temperature
                AquariumState.SETUP,
                java.time.LocalDateTime.now(), // currentStateStartTime
                "blue",
                "Test aquarium",
                java.time.LocalDateTime.now(), // dateCreated
                OWNER_ID,
                1L // aquariumManagerId
        );
    }
}
