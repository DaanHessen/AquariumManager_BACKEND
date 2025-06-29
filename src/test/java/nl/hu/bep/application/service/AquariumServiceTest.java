package nl.hu.bep.application.service;

import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.data.interfaces.OwnerRepository;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.Owner;
import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.dto.request.AquariumRequest;
import nl.hu.bep.presentation.dto.response.AquariumResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AquariumService Unit Tests")
class AquariumServiceTest {

    @Mock
    private AquariumRepository aquariumRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private EntityMapper entityMapper;

    private AquariumService aquariumService;

    private static final Long OWNER_ID = 1L;
    private static final Long AQUARIUM_ID = 10L;
    private static final Long OTHER_OWNER_ID = 2L;

    @BeforeEach
    void setUp() {
        aquariumService = new AquariumService(aquariumRepository, ownerRepository, entityMapper);
    }

    @Nested
    @DisplayName("Get All Aquariums")
    class GetAllAquariums {

        @Test
        @DisplayName("Should return all aquariums for owner")
        void shouldReturnAllAquariumsForOwner() {
            // Given
            Aquarium aquarium1 = createTestAquarium(1L, "Tank 1");
            Aquarium aquarium2 = createTestAquarium(2L, "Tank 2");
            List<Aquarium> aquariums = Arrays.asList(aquarium1, aquarium2);
            
            AquariumResponse response1 = createTestAquariumResponse(1L, "Tank 1");
            AquariumResponse response2 = createTestAquariumResponse(2L, "Tank 2");

            when(aquariumRepository.findByOwnerId(OWNER_ID)).thenReturn(aquariums);
            when(entityMapper.mapToAquariumResponse(aquarium1)).thenReturn(response1);
            when(entityMapper.mapToAquariumResponse(aquarium2)).thenReturn(response2);

            // When
            List<AquariumResponse> result = aquariumService.getAllAquariums(OWNER_ID);

            // Then
            assertEquals(2, result.size());
            assertEquals("Tank 1", result.get(0).name());
            assertEquals("Tank 2", result.get(1).name());
            
            verify(aquariumRepository).findByOwnerId(OWNER_ID);
            verify(entityMapper, times(2)).mapToAquariumResponse(any(Aquarium.class));
        }

        @Test
        @DisplayName("Should return empty list when owner has no aquariums")
        void shouldReturnEmptyListWhenOwnerHasNoAquariums() {
            // Given
            when(aquariumRepository.findByOwnerId(OWNER_ID)).thenReturn(Collections.emptyList());

            // When
            List<AquariumResponse> result = aquariumService.getAllAquariums(OWNER_ID);

            // Then
            assertTrue(result.isEmpty());
            verify(aquariumRepository).findByOwnerId(OWNER_ID);
        }
    }

    @Nested
    @DisplayName("Get Single Aquarium")
    class GetSingleAquarium {

        @Test
        @DisplayName("Should return aquarium when owner has access")
        void shouldReturnAquariumWhenOwnerHasAccess() {
            // Given
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            AquariumResponse expectedResponse = createTestAquariumResponse(AQUARIUM_ID, "Test Tank");

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));
            when(entityMapper.mapToAquariumResponse(aquarium)).thenReturn(expectedResponse);

            // When
            AquariumResponse result = aquariumService.getAquarium(AQUARIUM_ID, OWNER_ID);

            // Then
            assertEquals(expectedResponse, result);
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verify(entityMapper).mapToAquariumResponse(aquarium);
        }

        @Test
        @DisplayName("Should throw NotFoundException when aquarium doesn't exist")
        void shouldThrowNotFoundExceptionWhenAquariumDoesNotExist() {
            // Given
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> aquariumService.getAquarium(AQUARIUM_ID, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Aquarium"));
            assertTrue(exception.getMessage().contains(AQUARIUM_ID.toString()));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when owner doesn't have access")
        void shouldThrowBusinessRuleExceptionWhenOwnerDoesNotHaveAccess() {
            // Given
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            aquarium.assignToOwner(OTHER_OWNER_ID); // Different owner

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class,
                () -> aquariumService.getAquarium(AQUARIUM_ID, OWNER_ID)
            );
            
            assertEquals("Access denied: You do not own this aquarium", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Create Aquarium")
    class CreateAquarium {

        @Test
        @DisplayName("Should create aquarium successfully")
        void shouldCreateAquariumSuccessfully() {
            // Given
            AquariumRequest request = createTestAquariumRequest("New Tank");
            Owner owner = createTestOwner(OWNER_ID);
            Aquarium createdAquarium = createTestAquarium(AQUARIUM_ID, "New Tank");
            AquariumResponse expectedResponse = createTestAquariumResponse(AQUARIUM_ID, "New Tank");

            when(ownerRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner));
            when(aquariumRepository.insert(any(Aquarium.class))).thenReturn(createdAquarium);
            when(entityMapper.mapToAquariumResponse(createdAquarium)).thenReturn(expectedResponse);

            // When
            AquariumResponse result = aquariumService.createAquarium(request, OWNER_ID);

            // Then
            assertEquals(expectedResponse, result);
            verify(ownerRepository).findById(OWNER_ID);
            verify(aquariumRepository).insert(any(Aquarium.class));
            verify(entityMapper).mapToAquariumResponse(createdAquarium);
        }

        @Test
        @DisplayName("Should throw NotFoundException when owner doesn't exist")
        void shouldThrowNotFoundExceptionWhenOwnerDoesNotExist() {
            // Given
            AquariumRequest request = createTestAquariumRequest("New Tank");
            when(ownerRepository.findById(OWNER_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> aquariumService.createAquarium(request, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Owner"));
            assertTrue(exception.getMessage().contains(OWNER_ID.toString()));
        }

        @Test
        @DisplayName("Should validate aquarium data during creation")
        void shouldValidateAquariumDataDuringCreation() {
            // Given
            AquariumRequest invalidRequest = new AquariumRequest(
                "", // Empty name should fail validation
                100.0, 50.0, 60.0,
                SubstrateType.SAND,
                WaterType.FRESHWATER,
                "Blue", "Test description",
                AquariumState.SETUP
            );
            Owner owner = createTestOwner(OWNER_ID);
            when(ownerRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner));

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> aquariumService.createAquarium(invalidRequest, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Aquarium name"));
        }
    }

    @Nested
    @DisplayName("Update Aquarium")
    class UpdateAquarium {

        @Test
        @DisplayName("Should update aquarium successfully")
        void shouldUpdateAquariumSuccessfully() {
            // Given
            AquariumRequest updateRequest = createTestAquariumRequest("Updated Tank");
            Aquarium existingAquarium = createTestAquarium(AQUARIUM_ID, "Old Tank");
            Aquarium updatedAquarium = createTestAquarium(AQUARIUM_ID, "Updated Tank");
            AquariumResponse expectedResponse = createTestAquariumResponse(AQUARIUM_ID, "Updated Tank");

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(existingAquarium));
            when(aquariumRepository.update(any(Aquarium.class))).thenReturn(updatedAquarium);
            when(entityMapper.mapToAquariumResponse(updatedAquarium)).thenReturn(expectedResponse);

            // When
            AquariumResponse result = aquariumService.updateAquarium(AQUARIUM_ID, updateRequest, OWNER_ID);

            // Then
            assertEquals(expectedResponse, result);
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verify(aquariumRepository).update(any(Aquarium.class));
            verify(entityMapper).mapToAquariumResponse(updatedAquarium);
        }

        @Test
        @DisplayName("Should throw NotFoundException when updating non-existent aquarium")
        void shouldThrowNotFoundExceptionWhenUpdatingNonExistentAquarium() {
            // Given
            AquariumRequest updateRequest = createTestAquariumRequest("Updated Tank");
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> aquariumService.updateAquarium(AQUARIUM_ID, updateRequest, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Aquarium"));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when updating aquarium not owned by user")
        void shouldThrowBusinessRuleExceptionWhenUpdatingAquariumNotOwnedByUser() {
            // Given
            AquariumRequest updateRequest = createTestAquariumRequest("Updated Tank");
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            aquarium.assignToOwner(OTHER_OWNER_ID); // Different owner

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class,
                () -> aquariumService.updateAquarium(AQUARIUM_ID, updateRequest, OWNER_ID)
            );
            
            assertEquals("Access denied: You do not own this aquarium", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete Aquarium")
    class DeleteAquarium {

        @Test
        @DisplayName("Should delete aquarium successfully")
        void shouldDeleteAquariumSuccessfully() {
            // Given
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));

            // When
            assertDoesNotThrow(() -> aquariumService.deleteAquarium(AQUARIUM_ID, OWNER_ID));

            // Then
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verify(aquariumRepository).deleteById(AQUARIUM_ID);
        }

        @Test
        @DisplayName("Should throw NotFoundException when deleting non-existent aquarium")
        void shouldThrowNotFoundExceptionWhenDeletingNonExistentAquarium() {
            // Given
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> aquariumService.deleteAquarium(AQUARIUM_ID, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Aquarium"));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when deleting aquarium not owned by user")
        void shouldThrowBusinessRuleExceptionWhenDeletingAquariumNotOwnedByUser() {
            // Given
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            aquarium.assignToOwner(OTHER_OWNER_ID); // Different owner

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class,
                () -> aquariumService.deleteAquarium(AQUARIUM_ID, OWNER_ID)
            );
            
            assertEquals("Access denied: You do not own this aquarium", exception.getMessage());
        }
    }

    // Helper methods for creating test objects
    private Aquarium createTestAquarium(Long id, String name) {
        Aquarium aquarium = Aquarium.create(
            name, 100.0, 50.0, 60.0,
            SubstrateType.SAND, WaterType.FRESHWATER,
            "Blue", "Test description", AquariumState.SETUP
        );
        aquarium.assignToOwner(OWNER_ID);
        return Aquarium.reconstruct(
            id, name, aquarium.getDimensions(),
            SubstrateType.SAND, WaterType.FRESHWATER, 24.0,
            AquariumState.SETUP, LocalDateTime.now(),
            "Blue", "Test description", LocalDateTime.now(),
            null, OWNER_ID
        );
    }

    private AquariumResponse createTestAquariumResponse(Long id, String name) {
        return new AquariumResponse(
            id, name, 100.0, 50.0, 60.0,
            SubstrateType.SAND, WaterType.FRESHWATER, 24.0,
            AquariumState.SETUP, LocalDateTime.now(),
            "Blue", "Test description", LocalDateTime.now(),
            OWNER_ID, null
        );
    }

    private AquariumRequest createTestAquariumRequest(String name) {
        return new AquariumRequest(
            name, 100.0, 50.0, 60.0,
            SubstrateType.SAND, WaterType.FRESHWATER,
            "Blue", "Test description", AquariumState.SETUP
        );
    }

    private Owner createTestOwner(Long id) {
        return Owner.create("Test", "Owner", "test@example.com", "password123");
    }
}
