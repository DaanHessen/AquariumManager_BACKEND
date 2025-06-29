package nl.hu.bep.application.service;

import nl.hu.bep.data.interfaces.AccessoryRepository;
import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.domain.Accessory;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.accessories.Filter;
import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.dto.request.AccessoryRequest;
import nl.hu.bep.presentation.dto.response.AccessoryResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
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
@DisplayName("AccessoryService Unit Tests")
class AccessoryServiceTest {

    @Mock
    private AccessoryRepository accessoryRepository;

    @Mock
    private AquariumRepository aquariumRepository;

    @Mock
    private EntityMapper entityMapper;

    private AccessoryService accessoryService;

    private static final Long OWNER_ID = 1L;
    private static final Long AQUARIUM_ID = 10L;
    private static final Long ACCESSORY_ID = 20L;
    @BeforeEach
    void setUp() {
        accessoryService = new AccessoryService(accessoryRepository, aquariumRepository, entityMapper);
    }

    @Nested
    @DisplayName("Get All Accessories")
    class GetAllAccessories {

        @Test
        @DisplayName("Should return all accessories for owner")
        void shouldReturnAllAccessoriesForOwner() {
            // Given
            Accessory accessory1 = createTestAccessory(1L, "Filter 1");
            Accessory accessory2 = createTestAccessory(2L, "Filter 2");
            List<Accessory> accessories = Arrays.asList(accessory1, accessory2);
            
            AccessoryResponse response1 = createTestAccessoryResponse(1L, "Filter 1");
            AccessoryResponse response2 = createTestAccessoryResponse(2L, "Filter 2");

            when(accessoryRepository.findByOwnerId(OWNER_ID)).thenReturn(accessories);
            when(entityMapper.mapToAccessoryResponse(accessory1)).thenReturn(response1);
            when(entityMapper.mapToAccessoryResponse(accessory2)).thenReturn(response2);

            // When
            List<AccessoryResponse> result = accessoryService.getAllAccessories(OWNER_ID);

            // Then
            assertEquals(2, result.size());
            assertEquals("Filter 1", result.get(0).model());
            assertEquals("Filter 2", result.get(1).model());
            
            verify(accessoryRepository).findByOwnerId(OWNER_ID);
            verify(entityMapper, times(2)).mapToAccessoryResponse(any(Accessory.class));
        }

        @Test
        @DisplayName("Should return empty list when owner has no accessories")
        void shouldReturnEmptyListWhenOwnerHasNoAccessories() {
            // Given
            when(accessoryRepository.findByOwnerId(OWNER_ID)).thenReturn(Collections.emptyList());

            // When
            List<AccessoryResponse> result = accessoryService.getAllAccessories(OWNER_ID);

            // Then
            assertTrue(result.isEmpty());
            verify(accessoryRepository).findByOwnerId(OWNER_ID);
        }
    }

    @Nested
    @DisplayName("Get Accessories by Aquarium")
    class GetAccessoriesByAquarium {

        @Test
        @Disabled
        @DisplayName("Should return accessories for aquarium when owner has access")
        void shouldReturnAccessoriesForAquariumWhenOwnerHasAccess() {
            // Given
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            Accessory accessory = createTestAccessory(ACCESSORY_ID, "Test Filter");
            List<Accessory> accessories = Arrays.asList(accessory);
            AccessoryResponse response = createTestAccessoryResponse(ACCESSORY_ID, "Test Filter");

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));
            when(accessoryRepository.findByAquariumId(AQUARIUM_ID)).thenReturn(accessories);
            when(entityMapper.mapToAccessoryResponse(accessory)).thenReturn(response);

            // When
            List<AccessoryResponse> result = accessoryService.getAccessoriesByAquarium(AQUARIUM_ID);

            // Then
            assertEquals(1, result.size());
            assertEquals("Test Filter", result.get(0).model());
            
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verify(accessoryRepository).findByAquariumId(AQUARIUM_ID);
            verify(entityMapper).mapToAccessoryResponse(accessory);
        }

        @Test
        @Disabled
        @DisplayName("Should throw NotFoundException when aquarium doesn't exist")
        void shouldThrowNotFoundExceptionWhenAquariumDoesNotExist() {
            // Given
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> accessoryService.getAccessoriesByAquarium(AQUARIUM_ID)
            );
            
            assertTrue(exception.getMessage().contains("Aquarium"));
        }
    }

    @Nested
    @DisplayName("Get Single Accessory")
    class GetSingleAccessory {

        @Test
        @DisplayName("Should return accessory when owner has access")
        void shouldReturnAccessoryWhenOwnerHasAccess() {
            // Given
            Accessory accessory = createTestAccessory(ACCESSORY_ID, "Test Filter");
            AccessoryResponse expectedResponse = createTestAccessoryResponse(ACCESSORY_ID, "Test Filter");

            when(accessoryRepository.findById(ACCESSORY_ID)).thenReturn(Optional.of(accessory));
            when(entityMapper.mapToAccessoryResponse(accessory)).thenReturn(expectedResponse);

            // When
            AccessoryResponse result = accessoryService.getAccessory(ACCESSORY_ID, OWNER_ID);

            // Then
            assertEquals(expectedResponse, result);
            verify(accessoryRepository).findById(ACCESSORY_ID);
            verify(entityMapper).mapToAccessoryResponse(accessory);
        }

        @Test
        @DisplayName("Should throw NotFoundException when accessory doesn't exist")
        void shouldThrowNotFoundExceptionWhenAccessoryDoesNotExist() {
            // Given
            when(accessoryRepository.findById(ACCESSORY_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> accessoryService.getAccessory(ACCESSORY_ID, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Accessory"));
        }
    }

    @Nested
    @DisplayName("Create Accessory")
    class CreateAccessory {

        @Test
        @DisplayName("Should create accessory successfully")
        void shouldCreateAccessorySuccessfully() {
            // Given
            AccessoryRequest request = createTestAccessoryRequest("New Filter", AQUARIUM_ID);
            Aquarium aquarium = createTestAquarium(AQUARIUM_ID, "Test Tank");
            Accessory createdAccessory = createTestAccessory(ACCESSORY_ID, "New Filter");
            AccessoryResponse expectedResponse = createTestAccessoryResponse(ACCESSORY_ID, "New Filter");

            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.of(aquarium));
            when(accessoryRepository.insert(any(Accessory.class))).thenReturn(createdAccessory);
            when(entityMapper.mapToAccessoryResponse(createdAccessory)).thenReturn(expectedResponse);

            // When
            AccessoryResponse result = accessoryService.createAccessory(request, OWNER_ID);

            // Then
            assertEquals(expectedResponse, result);
            verify(aquariumRepository).findById(AQUARIUM_ID);
            verify(accessoryRepository).insert(any(Accessory.class));
            verify(entityMapper).mapToAccessoryResponse(createdAccessory);
        }

        @Test
        @DisplayName("Should throw NotFoundException when aquarium doesn't exist")
        void shouldThrowNotFoundExceptionWhenAquariumDoesNotExist() {
            // Given
            AccessoryRequest request = createTestAccessoryRequest("New Filter", AQUARIUM_ID);
            when(aquariumRepository.findById(AQUARIUM_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> accessoryService.createAccessory(request, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Aquarium"));
        }
    }

    @Nested
    @DisplayName("Update Accessory")
    class UpdateAccessory {

        @Test
        @Disabled
        @DisplayName("Should update accessory successfully")
        void shouldUpdateAccessorySuccessfully() {
            // Given
            AccessoryRequest updateRequest = createTestAccessoryRequest("Updated Filter", AQUARIUM_ID);
            Accessory existingAccessory = createTestAccessory(ACCESSORY_ID, "Old Filter");
            Accessory updatedAccessory = createTestAccessory(ACCESSORY_ID, "Updated Filter");
            AccessoryResponse expectedResponse = createTestAccessoryResponse(ACCESSORY_ID, "Updated Filter");

            when(accessoryRepository.findById(ACCESSORY_ID)).thenReturn(Optional.of(existingAccessory));
            when(accessoryRepository.update(any(Accessory.class))).thenReturn(updatedAccessory);
            when(entityMapper.mapToAccessoryResponse(updatedAccessory)).thenReturn(expectedResponse);

            // When
            AccessoryResponse result = accessoryService.updateAccessory(ACCESSORY_ID, updateRequest, OWNER_ID);

            // Then
            assertEquals(expectedResponse, result);
            verify(accessoryRepository).findById(ACCESSORY_ID);
            verify(accessoryRepository).update(any(Accessory.class));
            verify(entityMapper).mapToAccessoryResponse(updatedAccessory);
        }

        @Test
        @DisplayName("Should throw NotFoundException when accessory doesn't exist")
        void shouldThrowNotFoundExceptionWhenAccessoryDoesNotExist() {
            // Given
            AccessoryRequest updateRequest = createTestAccessoryRequest("Updated Filter", AQUARIUM_ID);
            when(accessoryRepository.findById(ACCESSORY_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> accessoryService.updateAccessory(ACCESSORY_ID, updateRequest, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Accessory"));
        }
    }

    @Nested
    @DisplayName("Delete Accessory")
    class DeleteAccessory {

        @Test
        @DisplayName("Should delete accessory successfully")
        void shouldDeleteAccessorySuccessfully() {
            // Given
            Accessory accessory = createTestAccessory(ACCESSORY_ID, "Test Filter");
            when(accessoryRepository.findById(ACCESSORY_ID)).thenReturn(Optional.of(accessory));

            // When
            assertDoesNotThrow(() -> accessoryService.deleteAccessory(ACCESSORY_ID, OWNER_ID));

            // Then
            verify(accessoryRepository).findById(ACCESSORY_ID);
            verify(accessoryRepository).deleteById(ACCESSORY_ID);
        }

        @Test
        @DisplayName("Should throw NotFoundException when accessory doesn't exist")
        void shouldThrowNotFoundExceptionWhenAccessoryDoesNotExist() {
            // Given
            when(accessoryRepository.findById(ACCESSORY_ID)).thenReturn(Optional.empty());

            // When & Then
            ApplicationException.NotFoundException exception = assertThrows(
                ApplicationException.NotFoundException.class,
                () -> accessoryService.deleteAccessory(ACCESSORY_ID, OWNER_ID)
            );
            
            assertTrue(exception.getMessage().contains("Accessory"));
        }
    }

    // Helper methods for creating test objects
    private Accessory createTestAccessory(Long id, String name) {
        return Accessory.reconstruct(
            "Filter", id, name, "SN123", OWNER_ID, AQUARIUM_ID, 
            "Blue", "Test filter", LocalDateTime.now(), 
            true, 100, false, null, null, 0.0, 0.0, 0.0
        );
    }

    private AccessoryResponse createTestAccessoryResponse(Long id, String name) {
        return new AccessoryResponse(
            id, "Filter", name, "SN123", "Blue", "Test filter",
            LocalDateTime.now(), OWNER_ID, AQUARIUM_ID,
            true, 100.0, false, null, null, 0.0, 0.0, 0.0
        );
    }

    private AccessoryRequest createTestAccessoryRequest(String name, Long aquariumId) {
        return new AccessoryRequest(
            name, "SN123", "Filter", aquariumId,
            true, 100, false, "Blue", "Test filter",
            null, null, 0.0, 0.0, 0.0
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
