package nl.hu.bep.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nl.hu.bep.application.mapper.EntityMappingService;
import nl.hu.bep.data.AccessoryRepository;
import nl.hu.bep.data.AquariumManagerRepository;
import nl.hu.bep.data.AquariumRepository;
import nl.hu.bep.data.OrnamentRepository;
import nl.hu.bep.data.OwnerRepository;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.Owner;
import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.AquariumRequest;
import nl.hu.bep.presentation.dto.AquariumResponse;

@ExtendWith(MockitoExtension.class)
class AquariumServiceTest {

  @Mock
  private AquariumRepository aquariumRepository;

  @Mock
  private AccessoryRepository accessoryRepository;

  @Mock
  private OrnamentRepository ornamentRepository;

  @Mock
  private OwnerRepository ownerRepository;

  @Mock
  private AquariumManagerRepository aquariumManagerRepository;

  @Mock
  private EntityMappingService mappingService;

  @InjectMocks
  private AquariumService aquariumService;

  private Aquarium testAquarium;
  private AquariumResponse testAquariumResponse;
  private Owner testOwner;

  @BeforeEach
  void setUp() {
    testAquarium = Aquarium.create(
        "Test Aquarium", 100.0, 40.0, 50.0,
        SubstrateType.GRAVEL, WaterType.FRESH);

    try {
      java.lang.reflect.Field idField = Aquarium.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testAquarium, 1L);
    } catch (Exception e) {
      fail("Failed to set aquarium ID: " + e.getMessage());
    }

    testOwner = Owner.create("Test", "Owner", "test@example.com");
    try {
      java.lang.reflect.Field idField = Owner.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testOwner, 1L);
    } catch (Exception e) {
      fail("Failed to set owner ID: " + e.getMessage());
    }

    testAquariumResponse = new AquariumResponse(
        1L,
        "Test Aquarium",
        100.0,
        40.0,
        50.0,
        200.0,
        SubstrateType.GRAVEL,
        WaterType.FRESH,
        AquariumState.SETUP,
        null, // currentStateStartTime
        0L,   // currentStateDurationMinutes
        1L,
        "test@example.com",
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        null, // color
        null, // description  
        null  // dateCreated
    );
  }

  @Test
  @DisplayName("getAllAquariums should return list of aquarium responses")
  void testGetAllAquariums() {
    List<Aquarium> aquariums = Arrays.asList(testAquarium);
    Long testOwnerId = 1L; // Define a test owner ID
    // Update mock for the new repository method
    when(aquariumRepository.findByOwnerIdWithCollections(testOwnerId)).thenReturn(aquariums);
    when(mappingService.mapAquarium(testAquarium)).thenReturn(testAquariumResponse);

    // Pass ownerId to the service call
    List<AquariumResponse> result = aquariumService.getAllAquariums(testOwnerId);

    assertEquals(1, result.size());
    assertEquals(testAquariumResponse, result.get(0));
    // Verify the new repository method call
    verify(aquariumRepository).findByOwnerIdWithCollections(testOwnerId);
    verify(mappingService).mapAquarium(testAquarium);
  }

  @Test
  @DisplayName("getAquariumById should return aquarium when found")
  void testGetAquariumByIdFound() {
    when(aquariumRepository.findByIdWithAllCollections(1L)).thenReturn(Optional.of(testAquarium));
    when(mappingService.mapAquariumDetailed(testAquarium)).thenReturn(testAquariumResponse);

    AquariumResponse result = aquariumService.getAquariumById(1L);

    assertEquals(testAquariumResponse, result);
    verify(aquariumRepository).findByIdWithAllCollections(1L);
    verify(mappingService).mapAquariumDetailed(testAquarium);
  }

  @Test
  @DisplayName("getAquariumById should throw exception when not found")
  void testGetAquariumByIdNotFound() {
    when(aquariumRepository.findByIdWithAllCollections(999L)).thenReturn(Optional.empty());

    ApplicationException.NotFoundException exception = assertThrows(
        ApplicationException.NotFoundException.class,
        () -> aquariumService.getAquariumById(999L));

    assertTrue(exception.getMessage().contains("Aquarium with ID 999"));
    verify(aquariumRepository).findByIdWithAllCollections(999L);
  }

  @Test
  @DisplayName("createAquarium should create and return new aquarium")
  void testCreateAquarium() {
    AquariumRequest request = new AquariumRequest(
        "New Aquarium",
        120.0,
        50.0,
        60.0,
        SubstrateType.SAND,
        WaterType.FRESH,
        null);

    Aquarium newAquarium = Aquarium.create(
        request.name(),
        request.length(),
        request.width(),
        request.height(),
        request.substrate(),
        request.waterType());

    when(aquariumRepository.save(any(Aquarium.class))).thenReturn(newAquarium);
    when(aquariumRepository.findByIdWithAllCollections(any())).thenReturn(Optional.of(newAquarium));
    when(mappingService.mapAquarium(newAquarium)).thenReturn(testAquariumResponse);

    AquariumResponse result = aquariumService.createAquarium(request, null);

    assertEquals(testAquariumResponse, result);
    verify(aquariumRepository).save(any(Aquarium.class));
    verify(aquariumRepository).findByIdWithAllCollections(any());
    verify(mappingService).mapAquarium(any(Aquarium.class));
  }

  @Test
  @DisplayName("createAquarium with owner should assign owner")
  void testCreateAquariumWithOwner() {
    AquariumRequest request = new AquariumRequest(
        "New Aquarium",
        120.0,
        50.0,
        60.0,
        SubstrateType.SAND,
        WaterType.FRESH,
        null);

    Aquarium newAquarium = Aquarium.create(
        request.name(),
        request.length(),
        request.width(),
        request.height(),
        request.substrate(),
        request.waterType());

    when(ownerRepository.findByIdWithAquariums(1L)).thenReturn(Optional.of(testOwner));
    when(aquariumRepository.save(any(Aquarium.class))).thenReturn(newAquarium);
    when(aquariumRepository.findByIdWithAllCollections(any())).thenReturn(Optional.of(newAquarium));
    when(mappingService.mapAquarium(any(Aquarium.class))).thenReturn(testAquariumResponse);

    AquariumResponse result = aquariumService.createAquarium(request, 1L);

    assertEquals(testAquariumResponse, result);
    verify(ownerRepository).findByIdWithAquariums(1L);
    verify(aquariumRepository).save(any(Aquarium.class));
    verify(aquariumRepository).findByIdWithAllCollections(any());
    verify(mappingService).mapAquarium(any(Aquarium.class));
  }

  @Test
  @DisplayName("deleteAquarium should delete existing aquarium")
  void testDeleteAquarium() {
    when(aquariumRepository.existsById(1L)).thenReturn(true);
    doNothing().when(aquariumRepository).deleteById(1L);

    aquariumService.deleteAquarium(1L);

    verify(aquariumRepository).existsById(1L);
    verify(aquariumRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteAquarium should throw exception for non-existent aquarium")
  void testDeleteAquariumNotFound() {
    when(aquariumRepository.existsById(999L)).thenReturn(false);

    ApplicationException.NotFoundException exception = assertThrows(
        ApplicationException.NotFoundException.class,
        () -> aquariumService.deleteAquarium(999L));

    assertTrue(exception.getMessage().contains("Aquarium with ID 999"));
    verify(aquariumRepository).existsById(999L);
    verify(aquariumRepository, never()).deleteById(anyLong());
  }
}