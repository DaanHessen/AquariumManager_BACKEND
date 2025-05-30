package nl.hu.bep.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nl.hu.bep.application.exception.ApplicationException;
import nl.hu.bep.application.mapper.EntityMappingService;
import nl.hu.bep.data.AccessoryRepository;
import nl.hu.bep.data.AquariumRepository;
import nl.hu.bep.domain.Accessory;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.Owner;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.presentation.dto.AccessoryRequest;
import nl.hu.bep.presentation.dto.AccessoryResponse;
import nl.hu.bep.presentation.dto.AquariumResponse;

@ExtendWith(MockitoExtension.class)
class AccessoryServiceTest {

  @Mock
  private AccessoryRepository accessoryRepository;

  @Mock
  private AquariumRepository aquariumRepository;

  @Mock
  private EntityMappingService mappingService;

  @InjectMocks
  private AccessoryService accessoryService;

  private Accessory testAccessory;
  private AccessoryResponse testAccessoryResponse;
  private Aquarium testAquarium;
  private Owner testOwner;

  @BeforeEach
  void setUp() {
    testOwner = Owner.create("Test", "Owner", "test@example.com");
    try {
      java.lang.reflect.Field idField = Owner.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testOwner, 1L);
    } catch (Exception e) {
      fail("Failed to set owner ID: " + e.getMessage());
    }

    testAccessory = Accessory.createFromType(
        "filter", "SuperFilter 3000", "SF3000-12345",
        true, 200,
        false, null, null,
        0.0, 0.0, 0.0,
        1L);
    try {
      java.lang.reflect.Field idField = Accessory.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testAccessory, 1L);
    } catch (Exception e) {
      fail("Failed to set accessory ID: " + e.getMessage());
    }

    testAquarium = Aquarium.create(
        "Test Aquarium", 100.0, 40.0, 50.0,
        SubstrateType.GRAVEL, WaterType.FRESH);
    testAquarium.activateAquarium();

    testAquarium.assignToOwner(testOwner);

    try {
      java.lang.reflect.Field idField = Aquarium.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testAquarium, 1L);
    } catch (Exception e) {
      fail("Failed to set aquarium ID: " + e.getMessage());
    }

    testAccessoryResponse = new AccessoryResponse(
        1L,
        "SuperFilter 3000",
        "SF3000-12345",
        "Filter",
        true,
        200,
        null,
        null,
        null,
        null,
        null,
        null);
  }

  @Test
  @DisplayName("getAllAccessories should return list of accessory responses")
  void testGetAllAccessories() {
    List<Accessory> accessories = Arrays.asList(testAccessory);
    Long testOwnerId = 1L; // Use the ID of the testOwner set up in @BeforeEach
    // Use findByOwnerId
    when(accessoryRepository.findByOwnerId(testOwnerId)).thenReturn(accessories); 
    when(mappingService.mapAccessory(testAccessory)).thenReturn(testAccessoryResponse);

    // Pass ownerId to the service call
    List<AccessoryResponse> result = accessoryService.getAllAccessories(testOwnerId);

    assertEquals(1, result.size());
    assertEquals(testAccessoryResponse, result.get(0));
    // Verify findByOwnerId call
    verify(accessoryRepository).findByOwnerId(testOwnerId);
    verify(mappingService).mapAccessory(testAccessory);
  }

  @Test
  @DisplayName("getAccessoryById should return accessory when found")
  void testGetAccessoryByIdFound() {
    when(accessoryRepository.findById(1L)).thenReturn(Optional.of(testAccessory));
    when(mappingService.mapAccessory(testAccessory)).thenReturn(testAccessoryResponse);

    AccessoryResponse result = accessoryService.getAccessoryById(1L);

    assertEquals(testAccessoryResponse, result);
    verify(accessoryRepository).findById(1L);
    verify(mappingService).mapAccessory(testAccessory);
  }

  @Test
  @DisplayName("getAccessoryById should throw exception when not found")
  void testGetAccessoryByIdNotFound() {
    when(accessoryRepository.findById(999L)).thenReturn(Optional.empty());

    ApplicationException.NotFoundException exception = assertThrows(
        ApplicationException.NotFoundException.class,
        () -> accessoryService.getAccessoryById(999L));

    assertTrue(exception.getMessage().contains("Accessory with ID 999"));
    verify(accessoryRepository).findById(999L);
  }

  @Test
  @DisplayName("getAccessoriesByAquarium should return accessories for an aquarium")
  void testGetAccessoriesByAquarium() {
    testAquarium.addToAccessories(testAccessory);
    when(aquariumRepository.findByIdWithAccessories(1L)).thenReturn(Optional.of(testAquarium));
    when(mappingService.mapAccessory(testAccessory)).thenReturn(testAccessoryResponse);

    List<AccessoryResponse> result = accessoryService.getAccessoriesByAquarium(1L);

    assertEquals(1, result.size());
    assertEquals(testAccessoryResponse, result.get(0));
    verify(aquariumRepository).findByIdWithAccessories(1L);
    verify(mappingService).mapAccessory(testAccessory);
  }

  @Test
  @DisplayName("createAccessory should create and return new accessory")
  void testCreateAccessory() {
    AccessoryRequest request = mock(AccessoryRequest.class);
    when(request.type()).thenReturn("filter");
    when(request.model()).thenReturn("SuperFilter 3000");
    when(request.serialNumber()).thenReturn("SF3000-12345");
    when(request.getIsExternalValue()).thenReturn(true);
    when(request.getCapacityLitersValue()).thenReturn(200);
    when(request.getIsLEDValue()).thenReturn(false);
    when(request.getTimeOnValue()).thenReturn(null);
    when(request.getTimeOffValue()).thenReturn(null);
    when(request.getMinTemperatureValue()).thenReturn(0.0);
    when(request.getMaxTemperatureValue()).thenReturn(0.0);
    when(request.getCurrentTemperatureValue()).thenReturn(0.0);
    when(request.aquariumId()).thenReturn(null);

    when(accessoryRepository.save(any(Accessory.class))).thenReturn(testAccessory);
    when(mappingService.mapAccessory(testAccessory)).thenReturn(testAccessoryResponse);

    AccessoryResponse result = accessoryService.createAccessory(request, null);

    assertEquals(testAccessoryResponse, result);
    verify(accessoryRepository).save(any(Accessory.class));
    verify(mappingService).mapAccessory(testAccessory);
  }

  @Test
  @DisplayName("createAccessory with aquarium should add to aquarium")
  void testCreateAccessoryWithAquarium() {
    AccessoryRequest request = mock(AccessoryRequest.class);
    when(request.type()).thenReturn("filter");
    when(request.model()).thenReturn("SuperFilter 3000");
    when(request.serialNumber()).thenReturn("SF3000-12345");
    when(request.getIsExternalValue()).thenReturn(true);
    when(request.getCapacityLitersValue()).thenReturn(200);
    when(request.getIsLEDValue()).thenReturn(false);
    when(request.getTimeOnValue()).thenReturn(null);
    when(request.getTimeOffValue()).thenReturn(null);
    when(request.getMinTemperatureValue()).thenReturn(0.0);
    when(request.getMaxTemperatureValue()).thenReturn(0.0);
    when(request.getCurrentTemperatureValue()).thenReturn(0.0);
    when(request.aquariumId()).thenReturn(1L);

    when(aquariumRepository.findByIdWithAccessories(1L)).thenReturn(Optional.of(testAquarium));
    when(accessoryRepository.save(any(Accessory.class))).thenReturn(testAccessory);
    when(accessoryRepository.findById(any())).thenReturn(Optional.of(testAccessory));
    when(mappingService.mapAccessory(testAccessory)).thenReturn(testAccessoryResponse);

    AccessoryResponse result = accessoryService.createAccessory(request, 1L);

    assertEquals(testAccessoryResponse, result);
    verify(aquariumRepository).findByIdWithAccessories(1L);
    verify(aquariumRepository).save(testAquarium);
    verify(accessoryRepository).save(any(Accessory.class));
    verify(mappingService).mapAccessory(testAccessory);
  }

  @Test
  @DisplayName("Add accessory to aquarium should add and return updated aquarium")
  void testAddAccessoryToAquarium() {
    AquariumResponse expectedResponse = mock(AquariumResponse.class);

    Aquarium spyAquarium = spy(testAquarium);
    when(spyAquarium.isOwnedBy(anyLong())).thenReturn(true);

    when(aquariumRepository.findByIdWithAccessories(1L)).thenReturn(Optional.of(spyAquarium));
    when(accessoryRepository.findById(1L)).thenReturn(Optional.of(testAccessory));
    when(accessoryRepository.save(testAccessory)).thenReturn(testAccessory);
    when(aquariumRepository.save(spyAquarium)).thenReturn(spyAquarium);
    when(aquariumRepository.findByIdWithAllCollections(1L)).thenReturn(Optional.of(spyAquarium));
    when(mappingService.mapAquariumDetailed(spyAquarium)).thenReturn(expectedResponse);

    AquariumResponse result = accessoryService.addAccessory(1L, 1L, 1L);

    assertEquals(expectedResponse, result);
    verify(aquariumRepository).findByIdWithAccessories(1L);
    verify(accessoryRepository).findById(1L);
    verify(accessoryRepository).save(testAccessory);
    verify(aquariumRepository).save(any(Aquarium.class));
    verify(aquariumRepository).findByIdWithAllCollections(1L);
    verify(mappingService).mapAquariumDetailed(any(Aquarium.class));
  }

  @Test
  @DisplayName("deleteAccessory should delete existing accessory")
  void testDeleteAccessory() {
    when(accessoryRepository.existsById(1L)).thenReturn(true);
    when(accessoryRepository.findById(1L)).thenReturn(Optional.of(testAccessory));
    doNothing().when(accessoryRepository).deleteById(1L);

    accessoryService.deleteAccessory(1L);

    verify(accessoryRepository).existsById(1L);
    verify(accessoryRepository).findById(1L);
    verify(accessoryRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteAccessory should remove from aquarium if assigned")
  void testDeleteAccessoryFromAquarium() {
    testAquarium.addToAccessories(testAccessory);

    when(accessoryRepository.existsById(1L)).thenReturn(true);
    when(accessoryRepository.findById(1L)).thenReturn(Optional.of(testAccessory));
    when(aquariumRepository.findByIdWithAccessories(1L)).thenReturn(Optional.of(testAquarium));
    doNothing().when(accessoryRepository).deleteById(1L);

    accessoryService.deleteAccessory(1L);

    verify(accessoryRepository).existsById(1L);
    verify(accessoryRepository).findById(1L);
    verify(aquariumRepository).findByIdWithAccessories(1L);
    verify(aquariumRepository).save(testAquarium);
    verify(accessoryRepository).deleteById(1L);
  }
}