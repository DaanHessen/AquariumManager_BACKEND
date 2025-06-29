package nl.hu.bep.application.service;

import nl.hu.bep.data.interfaces.OrnamentRepository;
import nl.hu.bep.data.interfaces.OwnerRepository;
import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.domain.Ornament;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.dto.request.OrnamentRequest;
import nl.hu.bep.presentation.dto.response.OrnamentResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
@DisplayName("OrnamentService Unit Tests")
class OrnamentServiceTest {

    private OrnamentService ornamentService;

    @Mock
    private OrnamentRepository ornamentRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private AquariumRepository aquariumRepository;

    @Mock
    private EntityMapper entityMapper;

    private static final Long OWNER_ID = 1L;
    private static final Long AQUARIUM_ID = 10L;
    private static final Long ORNAMENT_ID = 20L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ornamentService = new OrnamentService(ornamentRepository, ownerRepository, aquariumRepository, entityMapper);
    }

    @Nested
    @DisplayName("Get All Ornaments")
    class GetAllOrnaments {

        @Test
        @DisplayName("Should return all ornaments for owner")
        void shouldReturnAllOrnamentsForOwner() {
            // Given
            Ornament ornament1 = createTestOrnament(1L, "Castle 1", OWNER_ID);
            Ornament ornament2 = createTestOrnament(2L, "Castle 2", OWNER_ID);
            List<Ornament> ornaments = Arrays.asList(ornament1, ornament2);
            
            OrnamentResponse response1 = createTestOrnamentResponse(1L, "Castle 1", OWNER_ID);
            OrnamentResponse response2 = createTestOrnamentResponse(2L, "Castle 2", OWNER_ID);

            when(ornamentRepository.findByOwnerId(OWNER_ID)).thenReturn(ornaments);
            when(entityMapper.mapToOrnamentResponse(ornament1)).thenReturn(response1);
            when(entityMapper.mapToOrnamentResponse(ornament2)).thenReturn(response2);

            // When
            List<OrnamentResponse> result = ornamentService.getAllOrnaments(OWNER_ID);

            // Then
            assertEquals(2, result.size());
            verify(ornamentRepository).findByOwnerId(OWNER_ID);
            verify(entityMapper, times(2)).mapToOrnamentResponse(any(Ornament.class));
        }

        @Test
        @DisplayName("Should return empty list when owner has no ornaments")
        void shouldReturnEmptyListWhenOwnerHasNoOrnaments() {
            // Given
            when(ornamentRepository.findByOwnerId(OWNER_ID)).thenReturn(Collections.emptyList());

            // When
            List<OrnamentResponse> result = ornamentService.getAllOrnaments(OWNER_ID);

            // Then
            assertTrue(result.isEmpty());
            verify(ornamentRepository).findByOwnerId(OWNER_ID);
        }
    }

    @Nested
    @DisplayName("Get Ornaments by Aquarium")
    class GetOrnamentsByAquarium {

        @Test
        @DisplayName("Should return ornaments for aquarium")
        void shouldReturnOrnamentsForAquarium() {
            // Given
            Ornament ornament = createTestOrnament(ORNAMENT_ID, "Castle", OWNER_ID);
            List<Ornament> ornaments = Arrays.asList(ornament);
            OrnamentResponse response = createTestOrnamentResponse(ORNAMENT_ID, "Castle", OWNER_ID);

            when(ornamentRepository.findByAquariumId(AQUARIUM_ID)).thenReturn(ornaments);
            when(entityMapper.mapToOrnamentResponse(ornament)).thenReturn(response);

            // When
            List<OrnamentResponse> result = ornamentService.getOrnamentsByAquarium(AQUARIUM_ID);

            // Then
            assertEquals(1, result.size());
            verify(ornamentRepository).findByAquariumId(AQUARIUM_ID);
            verify(entityMapper).mapToOrnamentResponse(ornament);
        }

        @Test
        @DisplayName("Should return empty list when aquarium has no ornaments")
        void shouldReturnEmptyListWhenAquariumHasNoOrnaments() {
            // Given
            when(ornamentRepository.findByAquariumId(AQUARIUM_ID)).thenReturn(Collections.emptyList());

            // When
            List<OrnamentResponse> result = ornamentService.getOrnamentsByAquarium(AQUARIUM_ID);

            // Then
            assertTrue(result.isEmpty());
            verify(ornamentRepository).findByAquariumId(AQUARIUM_ID);
        }
    }

    @Nested
    @DisplayName("Get Single Ornament")
    class GetSingleOrnament {

        @Test
        @DisplayName("Should return ornament when owner has access")
        void shouldReturnOrnamentWhenOwnerHasAccess() {
            // Given
            Ornament ornament = createTestOrnament(ORNAMENT_ID, "Castle", OWNER_ID);
            OrnamentResponse expectedResponse = createTestOrnamentResponse(ORNAMENT_ID, "Castle", OWNER_ID);

            when(ornamentRepository.findById(ORNAMENT_ID)).thenReturn(Optional.of(ornament));
            when(entityMapper.mapToOrnamentResponse(ornament)).thenReturn(expectedResponse);

            // When
            OrnamentResponse result = ornamentService.getOrnament(ORNAMENT_ID, OWNER_ID);

            // Then
            assertEquals(expectedResponse, result);
            verify(ornamentRepository).findById(ORNAMENT_ID);
            verify(entityMapper).mapToOrnamentResponse(ornament);
        }

        @Test
        @DisplayName("Should throw NotFoundException when ornament doesn't exist")
        void shouldThrowNotFoundExceptionWhenOrnamentDoesNotExist() {
            // Given
            when(ornamentRepository.findById(ORNAMENT_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> ornamentService.getOrnament(ORNAMENT_ID, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Ornament"));
        }
    }

    @Nested
    @DisplayName("Create Ornament")
    class CreateOrnament {

        @Test
        @DisplayName("Should create ornament successfully")
        void shouldCreateOrnamentSuccessfully() {
            // Given
            OrnamentRequest request = createTestOrnamentRequest("New Castle", AQUARIUM_ID, OWNER_ID);
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            Ornament createdOrnament = createTestOrnament(ORNAMENT_ID, "New Castle", OWNER_ID);
            OrnamentResponse expectedResponse = createTestOrnamentResponse(ORNAMENT_ID, "New Castle", OWNER_ID);

            when(ownerRepository.findById(OWNER_ID)).thenReturn(Optional.of(mock(nl.hu.bep.domain.Owner.class)));
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));
            when(ornamentRepository.insert(any(Ornament.class))).thenReturn(createdOrnament);
            when(entityMapper.mapToOrnamentResponse(createdOrnament)).thenReturn(expectedResponse);

            // When
            OrnamentResponse result = ornamentService.createOrnament(request, OWNER_ID);

            // Then
            assertEquals(expectedResponse, result);
            verify(ownerRepository).findById(OWNER_ID);
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verify(ornamentRepository).insert(any(Ornament.class));
            verify(entityMapper).mapToOrnamentResponse(createdOrnament);
        }

        @Test
        @Disabled
        @DisplayName("Should throw NotFoundException when aquarium doesn't exist")
        void shouldThrowNotFoundExceptionWhenAquariumDoesNotExist() {
            // Given
            OrnamentRequest request = createTestOrnamentRequest("New Castle", AQUARIUM_ID, OWNER_ID);
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> ornamentService.createOrnament(request, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Aquarium"));
        }
    }

    @Nested
    @DisplayName("Update Ornament")
    class UpdateOrnament {

        @Test
        @Disabled
        @DisplayName("Should update ornament successfully")
        void shouldUpdateOrnamentSuccessfully() {
            // Given
            OrnamentRequest updateRequest = createTestOrnamentRequest("Updated Castle", AQUARIUM_ID, OWNER_ID);
            Ornament existingOrnament = createTestOrnament(ORNAMENT_ID, "Old Castle", OWNER_ID);
            Ornament updatedOrnament = createTestOrnament(ORNAMENT_ID, "Updated Castle", OWNER_ID);
            OrnamentResponse expectedResponse = createTestOrnamentResponse(ORNAMENT_ID, "Updated Castle", OWNER_ID);

            when(ornamentRepository.findById(ORNAMENT_ID)).thenReturn(Optional.of(existingOrnament));
            when(ornamentRepository.update(any(Ornament.class))).thenReturn(updatedOrnament);
            when(entityMapper.mapToOrnamentResponse(updatedOrnament)).thenReturn(expectedResponse);

            // When
            OrnamentResponse result = ornamentService.updateOrnament(ORNAMENT_ID, updateRequest, OWNER_ID);

            // Then
            assertEquals(expectedResponse, result);
            verify(ornamentRepository).findById(ORNAMENT_ID);
            verify(ornamentRepository).update(any(Ornament.class));
            verify(entityMapper).mapToOrnamentResponse(updatedOrnament);
        }

        @Test
        @DisplayName("Should throw NotFoundException when ornament doesn't exist")
        void shouldThrowNotFoundExceptionWhenOrnamentDoesNotExist() {
            // Given
            OrnamentRequest updateRequest = createTestOrnamentRequest("Updated Castle", AQUARIUM_ID, OWNER_ID);
            when(ornamentRepository.findById(ORNAMENT_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> ornamentService.updateOrnament(ORNAMENT_ID, updateRequest, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Ornament"));
        }
    }

    @Nested
    @DisplayName("Delete Ornament")
    class DeleteOrnament {

        @Test
        @DisplayName("Should delete ornament successfully")
        void shouldDeleteOrnamentSuccessfully() {
            // Given
            Ornament ornament = createTestOrnament(ORNAMENT_ID, "Castle", OWNER_ID);
            when(ornamentRepository.findById(ORNAMENT_ID)).thenReturn(Optional.of(ornament));

            // When
            assertDoesNotThrow(() -> ornamentService.deleteOrnament(ORNAMENT_ID, OWNER_ID));

            // Then
            verify(ornamentRepository).findById(ORNAMENT_ID);
            verify(ornamentRepository).deleteById(ORNAMENT_ID);
        }

        @Test
        @DisplayName("Should throw NotFoundException when ornament doesn't exist")
        void shouldThrowNotFoundExceptionWhenOrnamentDoesNotExist() {
            // Given
            when(ornamentRepository.findById(ORNAMENT_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> ornamentService.deleteOrnament(ORNAMENT_ID, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Ornament"));
        }
    }

    // Helper methods for creating test objects
    private Ornament createTestOrnament(Long id, String name, Long ownerId) {
        return Ornament.reconstruct(
            id, name, "Decorative castle", "Gray", "Stone", true, ownerId, AQUARIUM_ID, LocalDateTime.now()
        );
    }

    private OrnamentResponse createTestOrnamentResponse(Long id, String name, Long ownerId) {
        return new OrnamentResponse(
            id, name, "Stone", "Gray", "Decorative castle", true, LocalDateTime.now(), ownerId, AQUARIUM_ID
        );
    }

    private OrnamentRequest createTestOrnamentRequest(String name, Long aquariumId, Long ownerId) {
        return new OrnamentRequest(
            name, "Gray", "Stone", "Decorative castle", true, aquariumId
        );
    }

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
}
