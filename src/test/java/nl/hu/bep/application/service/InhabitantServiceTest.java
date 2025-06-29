package nl.hu.bep.application.service;

import nl.hu.bep.data.interfaces.InhabitantRepository;
import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.dto.request.InhabitantRequest;
import nl.hu.bep.presentation.dto.response.InhabitantResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InhabitantService Unit Tests")
class InhabitantServiceTest {

    @Mock
    private InhabitantRepository inhabitantRepository;

    @Mock
    private AquariumRepository aquariumRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private InhabitantService inhabitantService;

    private static final Long OWNER_ID = 1L;
    private static final Long OTHER_OWNER_ID = 2L;
    private static final Long INHABITANT_ID = 10L;
    private static final Long AQUARIUM_ID = 20L;

    @Nested
    @DisplayName("Get All Inhabitants")
    class GetAllInhabitants {

        @Test
        @DisplayName("Should return all inhabitants for owner")
        void shouldReturnAllInhabitantsForOwner() {
            // Given
            List<Inhabitant> inhabitants = List.of(
                createTestInhabitant(1L, "Goldfish"),
                createTestInhabitant(2L, "Betta")
            );
            List<InhabitantResponse> expectedResponses = List.of(
                createTestInhabitantResponse(1L, "Goldfish"),
                createTestInhabitantResponse(2L, "Betta")
            );

            when(inhabitantRepository.findByOwnerId(OWNER_ID)).thenReturn(inhabitants);
            when(entityMapper.mapToInhabitantResponse(any(Inhabitant.class)))
                .thenReturn(expectedResponses.get(0), expectedResponses.get(1));

            // When
            List<InhabitantResponse> result = inhabitantService.getAllInhabitants(OWNER_ID);

            // Then
            assertEquals(2, result.size());
            verify(inhabitantRepository).findByOwnerId(OWNER_ID);
            verify(entityMapper, times(2)).mapToInhabitantResponse(any(Inhabitant.class));
        }

        @Test
        @DisplayName("Should return empty list when owner has no inhabitants")
        void shouldReturnEmptyListWhenOwnerHasNoInhabitants() {
            // Given
            when(inhabitantRepository.findByOwnerId(OWNER_ID)).thenReturn(List.of());

            // When
            List<InhabitantResponse> result = inhabitantService.getAllInhabitants(OWNER_ID);

            // Then
            assertTrue(result.isEmpty());
            verify(inhabitantRepository).findByOwnerId(OWNER_ID);
            verify(entityMapper, never()).mapToInhabitantResponse(any(Inhabitant.class));
        }
    }

    @Nested
    @DisplayName("Get Single Inhabitant")
    class GetSingleInhabitant {

        @Test
        @DisplayName("Should return inhabitant when owner has access")
        void shouldReturnInhabitantWhenOwnerHasAccess() {
            // Given
            Inhabitant inhabitant = createTestInhabitant(INHABITANT_ID, "Test Fish");
            InhabitantResponse expectedResponse = createTestInhabitantResponse(INHABITANT_ID, "Test Fish");

            when(inhabitantRepository.findById(INHABITANT_ID)).thenReturn(Optional.of(inhabitant));
            when(entityMapper.mapToInhabitantResponse(inhabitant)).thenReturn(expectedResponse);

            // When
            InhabitantResponse result = inhabitantService.getInhabitant(INHABITANT_ID, OWNER_ID);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse, result);
            verify(inhabitant).validateOwnership(OWNER_ID);
        }

        @Test
        @DisplayName("Should throw NotFoundException when inhabitant doesn't exist")
        void shouldThrowNotFoundExceptionWhenInhabitantDoesNotExist() {
            // Given
            when(inhabitantRepository.findById(INHABITANT_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> inhabitantService.getInhabitant(INHABITANT_ID, OWNER_ID)
            );

            assertTrue(exception.getMessage().contains("Inhabitant"));
            assertTrue(exception.getMessage().contains(INHABITANT_ID.toString()));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when owner doesn't have access")
        void shouldThrowBusinessRuleExceptionWhenOwnerDoesNotHaveAccess() {
            // Given
            Inhabitant inhabitant = createTestInhabitant(INHABITANT_ID, "Test Fish");
            doThrow(new ApplicationException.BusinessRuleException("Access denied: You do not own this inhabitant"))
                .when(inhabitant).validateOwnership(OTHER_OWNER_ID);

            when(inhabitantRepository.findById(INHABITANT_ID)).thenReturn(Optional.of(inhabitant));

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class,
                () -> inhabitantService.getInhabitant(INHABITANT_ID, OTHER_OWNER_ID)
            );

            assertTrue(exception.getMessage().contains("Access denied"));
        }
    }

    @Nested
    @DisplayName("Create Inhabitant")
    class CreateInhabitant {

        @Test
        @DisplayName("Should create inhabitant successfully without aquarium assignment")
        void shouldCreateInhabitantSuccessfullyWithoutAquariumAssignment() {
            // Given
            InhabitantRequest request = createTestInhabitantRequest("New Fish", null);
            Inhabitant inhabitant = createTestInhabitant(INHABITANT_ID, "New Fish");
            InhabitantResponse expectedResponse = createTestInhabitantResponse(INHABITANT_ID, "New Fish");

            when(inhabitantRepository.insert(any(Inhabitant.class))).thenReturn(inhabitant);
            when(entityMapper.mapToInhabitantResponse(inhabitant)).thenReturn(expectedResponse);

            // When
            InhabitantResponse result = inhabitantService.createInhabitant(request, OWNER_ID);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse, result);
            verify(inhabitantRepository).insert(any(Inhabitant.class));
            verify(aquariumRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Should create inhabitant successfully with aquarium assignment")
        void shouldCreateInhabitantSuccessfullyWithAquariumAssignment() {
            // Given
            InhabitantRequest request = createTestInhabitantRequest("New Fish", AQUARIUM_ID);
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            Inhabitant inhabitant = createTestInhabitant(INHABITANT_ID, "New Fish");
            InhabitantResponse expectedResponse = createTestInhabitantResponse(INHABITANT_ID, "New Fish");

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));
            when(inhabitantRepository.insert(any(Inhabitant.class))).thenReturn(inhabitant);
            when(entityMapper.mapToInhabitantResponse(inhabitant)).thenReturn(expectedResponse);

            // When
            InhabitantResponse result = inhabitantService.createInhabitant(request, OWNER_ID);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse, result);
            verify(aquarium).validateOwnership(OWNER_ID);
            verify(inhabitantRepository).insert(any(Inhabitant.class));
        }

        @Test
        @DisplayName("Should throw NotFoundException when assigned aquarium doesn't exist")
        void shouldThrowNotFoundExceptionWhenAssignedAquariumDoesNotExist() {
            // Given
            InhabitantRequest request = createTestInhabitantRequest("New Fish", AQUARIUM_ID);

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> inhabitantService.createInhabitant(request, OWNER_ID)
            );

            assertTrue(exception.getMessage().contains("Aquarium"));
            assertTrue(exception.getMessage().contains(AQUARIUM_ID.toString()));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when owner doesn't own assigned aquarium")
        void shouldThrowBusinessRuleExceptionWhenOwnerDoesNotOwnAssignedAquarium() {
            // Given
            InhabitantRequest request = createTestInhabitantRequest("New Fish", AQUARIUM_ID);
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            
            doThrow(new ApplicationException.BusinessRuleException("Access denied: You do not own this aquarium"))
                .when(aquarium).validateOwnership(OWNER_ID);

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class,
                () -> inhabitantService.createInhabitant(request, OWNER_ID)
            );

            assertTrue(exception.getMessage().contains("Access denied"));
        }
    }

    @Nested
    @DisplayName("Update Inhabitant")
    class UpdateInhabitant {

        @Test
        @DisplayName("Should update inhabitant successfully")
        void shouldUpdateInhabitantSuccessfully() {
            // Given
            InhabitantRequest request = createTestInhabitantRequest("Updated Fish", AQUARIUM_ID);
            Inhabitant inhabitant = createTestInhabitant(INHABITANT_ID, "Original Fish");
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            InhabitantResponse expectedResponse = createTestInhabitantResponse(INHABITANT_ID, "Updated Fish");

            when(inhabitantRepository.findById(INHABITANT_ID)).thenReturn(Optional.of(inhabitant));
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));
            when(inhabitantRepository.update(inhabitant)).thenReturn(inhabitant);
            when(entityMapper.mapToInhabitantResponse(inhabitant)).thenReturn(expectedResponse);

            // When
            InhabitantResponse result = inhabitantService.updateInhabitant(INHABITANT_ID, request, OWNER_ID);

            // Then
            assertNotNull(result);
            assertEquals(expectedResponse, result);
            verify(inhabitant).validateOwnership(OWNER_ID);
            verify(inhabitant).assignToAquarium(AQUARIUM_ID, OWNER_ID);
            verify(inhabitant).update(any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should update inhabitant and remove from aquarium when aquarium is null")
        void shouldUpdateInhabitantAndRemoveFromAquariumWhenAquariumIsNull() {
            // Given
            InhabitantRequest request = createTestInhabitantRequest("Updated Fish", null);
            Inhabitant inhabitant = createTestInhabitant(INHABITANT_ID, "Original Fish");
            InhabitantResponse expectedResponse = createTestInhabitantResponse(INHABITANT_ID, "Updated Fish");

            when(inhabitantRepository.findById(INHABITANT_ID)).thenReturn(Optional.of(inhabitant));
            when(inhabitantRepository.update(inhabitant)).thenReturn(inhabitant);
            when(entityMapper.mapToInhabitantResponse(inhabitant)).thenReturn(expectedResponse);

            // When
            InhabitantResponse result = inhabitantService.updateInhabitant(INHABITANT_ID, request, OWNER_ID);

            // Then
            assertNotNull(result);
            verify(inhabitant).removeFromAquarium(OWNER_ID);
            verify(aquariumRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Should throw NotFoundException when inhabitant doesn't exist")
        void shouldThrowNotFoundExceptionWhenInhabitantDoesNotExist() {
            // Given
            InhabitantRequest request = createTestInhabitantRequest("Updated Fish", null);

            when(inhabitantRepository.findById(INHABITANT_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> inhabitantService.updateInhabitant(INHABITANT_ID, request, OWNER_ID)
            );

            assertTrue(exception.getMessage().contains("Inhabitant"));
        }
    }

    @Nested
    @DisplayName("Delete Inhabitant")
    class DeleteInhabitant {

        @Test
        @DisplayName("Should delete inhabitant successfully")
        void shouldDeleteInhabitantSuccessfully() {
            // Given
            Inhabitant inhabitant = createTestInhabitant(INHABITANT_ID, "Test Fish");

            when(inhabitantRepository.findById(INHABITANT_ID)).thenReturn(Optional.of(inhabitant));

            // When
            assertDoesNotThrow(() -> inhabitantService.deleteInhabitant(INHABITANT_ID, OWNER_ID));

            // Then
            verify(inhabitant).validateOwnership(OWNER_ID);
            verify(inhabitantRepository).deleteById(INHABITANT_ID);
        }

        @Test
        @DisplayName("Should throw NotFoundException when inhabitant doesn't exist")
        void shouldThrowNotFoundExceptionWhenInhabitantDoesNotExist() {
            // Given
            when(inhabitantRepository.findById(INHABITANT_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> inhabitantService.deleteInhabitant(INHABITANT_ID, OWNER_ID)
            );

            assertTrue(exception.getMessage().contains("Inhabitant"));
        }
    }

    @Nested
    @DisplayName("Get Inhabitants By Aquarium")
    class GetInhabitantsByAquarium {

        @Test
        @DisplayName("Should return inhabitants for given aquarium")
        void shouldReturnInhabitantsForGivenAquarium() {
            // Given
            List<Inhabitant> inhabitants = List.of(
                createTestInhabitant(1L, "Fish 1"),
                createTestInhabitant(2L, "Fish 2")
            );
            List<InhabitantResponse> expectedResponses = List.of(
                createTestInhabitantResponse(1L, "Fish 1"),
                createTestInhabitantResponse(2L, "Fish 2")
            );

            when(inhabitantRepository.findByAquariumId(AQUARIUM_ID)).thenReturn(inhabitants);
            when(entityMapper.mapToInhabitantResponse(any(Inhabitant.class)))
                .thenReturn(expectedResponses.get(0), expectedResponses.get(1));

            // When
            List<InhabitantResponse> result = inhabitantService.getInhabitantsByAquarium(AQUARIUM_ID);

            // Then
            assertEquals(2, result.size());
            verify(inhabitantRepository).findByAquariumId(AQUARIUM_ID);
            verify(entityMapper, times(2)).mapToInhabitantResponse(any(Inhabitant.class));
        }
    }

    // Helper methods for creating test objects
    private Inhabitant createTestInhabitant(Long id, String name) {
        Inhabitant inhabitant = mock(Inhabitant.class);
        // Only stub what's actually used in tests
        return inhabitant;
    }

    private InhabitantResponse createTestInhabitantResponse(Long id, String name) {
        return new InhabitantResponse(
            id,
            "FISH",
            "Test Species",
            "Blue",
            1,
            false,
            WaterType.FRESHWATER,
            name,
            "Test description",
            java.time.LocalDateTime.now(),
            OWNER_ID,
            null,
            false,
            false,
            false
        );
    }

    private InhabitantRequest createTestInhabitantRequest(String name, Long aquariumId) {
        return new InhabitantRequest(
            "Test Species",
            "Blue",
            "Test description",
            1,
            false,
            WaterType.FRESHWATER,
            "FISH",
            aquariumId,
            false,
            false,
            false,
            name,
            1,
            "MALE",
            7.0,
            25.0,
            100.0,
            1,
            0.0
        );
    }

    private Aquarium createTestAquarium(Long id, String name) {
        Aquarium aquarium = mock(Aquarium.class);
        // Only stub what's actually used in tests
        return aquarium;
    }
}
