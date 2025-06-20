package nl.hu.bep.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nl.hu.bep.application.mapper.EntityMappingService;
import nl.hu.bep.data.AquariumRepository;
import nl.hu.bep.data.OrnamentRepository;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.Ornament;
import nl.hu.bep.domain.Owner;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.OrnamentRequest;
import nl.hu.bep.presentation.dto.OrnamentResponse;
import nl.hu.bep.presentation.dto.AquariumResponse;

@ExtendWith(MockitoExtension.class)
class OrnamentServiceTest {

  @Mock
  private OrnamentRepository ornamentRepository;

  @Mock
  private AquariumRepository aquariumRepository;

  @Mock
  private EntityMappingService mappingService;

  @InjectMocks
  private OrnamentService ornamentService;

  private Ornament testOrnament;
  private OrnamentResponse testOrnamentResponse;
  private Aquarium testAquarium;
  private Owner testOwner;

  @BeforeEach
  void setUp() {
    testOrnament = new Ornament(
        "Castle", "A decorative castle", "Gray", false, 1L, "Stone");

    try {
      java.lang.reflect.Field idField = Ornament.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testOrnament, 1L);
    } catch (Exception e) {
      fail("Failed to set ornament ID: " + e.getMessage());
    }

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

    testOwner = Owner.create("Test", "Owner", "test@example.com", "password");
    try {
      java.lang.reflect.Field idField = Owner.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testOwner, 1L);

      testAquarium.assignToOwner(testOwner);
    } catch (Exception e) {
      fail("Failed to set owner ID or assign aquarium: " + e.getMessage());
    }

    testOrnamentResponse = new OrnamentResponse(
        1L,
        "Castle",
        "Gray",
        "Stone",
        "A decorative castle",
        testOrnament.getDateCreated(),
        false);
  }

  @Test
  @DisplayName("getAllOrnaments should return list of ornament responses")
  void testGetAllOrnaments() {
    List<Ornament> ornaments = Arrays.asList(testOrnament);
    Long testOwnerId = 1L; // Use the ID of the testOwner set up in @BeforeEach
    // Update mock for the new repository method
    when(ornamentRepository.findByAquariumOwnerId(testOwnerId)).thenReturn(ornaments);
    when(mappingService.mapOrnament(testOrnament)).thenReturn(testOrnamentResponse);

    // Pass ownerId to the service call
    List<OrnamentResponse> result = ornamentService.getAllOrnaments(testOwnerId);

    assertEquals(1, result.size());
    assertEquals(testOrnamentResponse, result.get(0));
    // Verify the new repository method call
    verify(ornamentRepository).findByAquariumOwnerId(testOwnerId);
    verify(mappingService).mapOrnament(testOrnament);
  }

  @Test
  @DisplayName("getOrnamentById should return ornament when found")
  void testGetOrnamentByIdFound() {
    when(ornamentRepository.findById(1L)).thenReturn(Optional.of(testOrnament));
    when(mappingService.mapOrnament(testOrnament)).thenReturn(testOrnamentResponse);

    OrnamentResponse result = ornamentService.getOrnamentById(1L);

    assertEquals(testOrnamentResponse, result);
    verify(ornamentRepository).findById(1L);
    verify(mappingService).mapOrnament(testOrnament);
  }

  @Test
  @DisplayName("getOrnamentById should throw exception when not found")
  void testGetOrnamentByIdNotFound() {
    when(ornamentRepository.findById(999L)).thenReturn(Optional.empty());

    ApplicationException.NotFoundException exception = assertThrows(
        ApplicationException.NotFoundException.class,
        () -> ornamentService.getOrnamentById(999L));

    assertTrue(exception.getMessage().contains("Ornament with ID 999"));
    verify(ornamentRepository).findById(999L);
  }

  @Test
  @DisplayName("getOrnamentsByAquarium should return ornaments for an aquarium")
  void testGetOrnamentsByAquarium() {
    testAquarium.addToOrnaments(testOrnament);
    when(aquariumRepository.findByIdWithOrnaments(1L)).thenReturn(Optional.of(testAquarium));
    when(mappingService.mapOrnament(testOrnament)).thenReturn(testOrnamentResponse);

    List<OrnamentResponse> result = ornamentService.getOrnamentsByAquarium(1L);

    assertEquals(1, result.size());
    assertEquals(testOrnamentResponse, result.get(0));
    verify(aquariumRepository).findByIdWithOrnaments(1L);
    verify(mappingService).mapOrnament(testOrnament);
  }

  @Test
  @DisplayName("createOrnament should create and return new ornament")
  void testCreateOrnament() {
    OrnamentRequest request = mock(OrnamentRequest.class);
    when(request.name()).thenReturn("Castle");
    when(request.description()).thenReturn("A decorative castle");
    when(request.color()).thenReturn("Gray");
    lenient().when(request.isAirPumpCompatible()).thenReturn(false);
    lenient().when(request.getIsAirPumpCompatibleValue()).thenReturn(false);
    when(request.aquariumId()).thenReturn(null);

    when(ornamentRepository.save(any(Ornament.class))).thenReturn(testOrnament);
    when(mappingService.mapOrnament(testOrnament)).thenReturn(testOrnamentResponse);

    OrnamentResponse result = ornamentService.createOrnament(request, null);

    assertEquals(testOrnamentResponse, result);
    verify(ornamentRepository).save(any(Ornament.class));
    verify(mappingService).mapOrnament(testOrnament);
  }

  @Test
  @DisplayName("createOrnament with aquarium should add to aquarium")
  void testCreateOrnamentWithAquarium() {
    OrnamentRequest request = mock(OrnamentRequest.class);
    when(request.name()).thenReturn("Castle");
    when(request.description()).thenReturn("A decorative castle");
    when(request.color()).thenReturn("Gray");
    lenient().when(request.isAirPumpCompatible()).thenReturn(false);
    lenient().when(request.getIsAirPumpCompatibleValue()).thenReturn(false);
    when(request.aquariumId()).thenReturn(1L);

    when(aquariumRepository.findByIdWithOrnaments(1L)).thenReturn(Optional.of(testAquarium));
    when(ornamentRepository.save(any(Ornament.class))).thenReturn(testOrnament);
    when(ornamentRepository.findById(any())).thenReturn(Optional.of(testOrnament));
    when(mappingService.mapOrnament(testOrnament)).thenReturn(testOrnamentResponse);

    OrnamentResponse result = ornamentService.createOrnament(request, 1L);

    assertEquals(testOrnamentResponse, result);
    verify(aquariumRepository).findByIdWithOrnaments(1L);
    verify(aquariumRepository).save(testAquarium);
    verify(ornamentRepository).save(any(Ornament.class));
    verify(mappingService).mapOrnament(testOrnament);
  }

  @Test
  @DisplayName("Add ornament to aquarium should add and return updated aquarium")
  void testAddOrnamentToAquarium() {
    AquariumResponse expectedResponse = mock(AquariumResponse.class);
    Map<String, Object> properties = Collections.emptyMap();

    when(aquariumRepository.findByIdWithOrnaments(1L)).thenReturn(Optional.of(testAquarium));
    when(ornamentRepository.findById(1L)).thenReturn(Optional.of(testOrnament));
    when(ornamentRepository.save(testOrnament)).thenReturn(testOrnament);
    when(aquariumRepository.save(testAquarium)).thenReturn(testAquarium);
    when(aquariumRepository.findByIdWithAllCollections(1L)).thenReturn(Optional.of(testAquarium));
    when(mappingService.mapAquariumDetailed(testAquarium)).thenReturn(expectedResponse);

    AquariumResponse result = ornamentService.addOrnament(1L, 1L, 1L, properties);

    assertEquals(expectedResponse, result);
    verify(aquariumRepository).findByIdWithOrnaments(1L);
    verify(ornamentRepository).findById(1L);
    verify(ornamentRepository).save(testOrnament);
    verify(aquariumRepository).save(testAquarium);
    verify(aquariumRepository).findByIdWithAllCollections(1L);
    verify(mappingService).mapAquariumDetailed(testAquarium);
  }

  @Test
  @DisplayName("deleteOrnament should delete existing ornament")
  void testDeleteOrnament() {
    when(ornamentRepository.existsById(1L)).thenReturn(true);
    when(ornamentRepository.findById(1L)).thenReturn(Optional.of(testOrnament));
    doNothing().when(ornamentRepository).deleteById(1L);

    ornamentService.deleteOrnament(1L);

    verify(ornamentRepository).existsById(1L);
    verify(ornamentRepository).findById(1L);
    verify(ornamentRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteOrnament should remove from aquarium if assigned")
  void testDeleteOrnamentFromAquarium() {
    testAquarium.addToOrnaments(testOrnament);

    when(ornamentRepository.existsById(1L)).thenReturn(true);
    when(ornamentRepository.findById(1L)).thenReturn(Optional.of(testOrnament));
    when(aquariumRepository.findByIdWithOrnaments(1L)).thenReturn(Optional.of(testAquarium));
    doNothing().when(ornamentRepository).deleteById(1L);

    ornamentService.deleteOrnament(1L);

    verify(ornamentRepository).existsById(1L);
    verify(ornamentRepository).findById(1L);
    verify(aquariumRepository).findByIdWithOrnaments(1L);
    verify(aquariumRepository).save(testAquarium);
    verify(ornamentRepository).deleteById(1L);
  }
}