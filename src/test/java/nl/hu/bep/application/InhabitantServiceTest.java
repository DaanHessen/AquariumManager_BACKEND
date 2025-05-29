package nl.hu.bep.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import org.mockito.MockedStatic;

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

import nl.hu.bep.application.exception.ApplicationException;
import nl.hu.bep.application.mapper.EntityMappingService;
import nl.hu.bep.data.AquariumRepository;
import nl.hu.bep.data.InhabitantRepository;
import nl.hu.bep.data.OwnerRepository;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.Owner;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.presentation.dto.InhabitantRequest;
import nl.hu.bep.presentation.dto.InhabitantResponse;
import nl.hu.bep.presentation.dto.AquariumResponse;

@ExtendWith(MockitoExtension.class)
class InhabitantServiceTest {

  @Mock
  private InhabitantRepository inhabitantRepository;

  @Mock
  private AquariumRepository aquariumRepository;

  @Mock
  private OwnerRepository ownerRepository;

  @Mock
  private EntityMappingService mappingService;

  @InjectMocks
  private InhabitantService inhabitantService;

  private Inhabitant testInhabitant;
  private InhabitantResponse testInhabitantResponse;
  private Aquarium testAquarium;
  private Owner testOwner;
  private InhabitantRequest testRequest;

  @BeforeEach
  void setUp() {
    testOwner = Owner.create("Test", "Owner", "test@example.com", "password");
    
    try {
      java.lang.reflect.Field idField = Owner.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testOwner, 1L);
    } catch (Exception e) {
      fail("Failed to set owner ID: " + e.getMessage());
    }

    testInhabitant = Inhabitant.createFromType(
        "FISH", "Guppy", "Red", 5, true, WaterType.FRESH, 
        false, false, false, 
        1L, "GuppyName");

    try {
      java.lang.reflect.Field idField = Inhabitant.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testInhabitant, 1L);
    } catch (Exception e) {
      fail("Failed to set inhabitant ID: " + e.getMessage());
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

    testRequest = new InhabitantRequest(
        "Guppy", "Red", 5, true, WaterType.FRESH, "FISH", null, 
        false, false, false, "GuppyName");

    testInhabitantResponse = new InhabitantResponse(
        1L,
        "Guppy", "Red",
        5,
        true,
        WaterType.FRESH,
        1L,
        "FISH",
        false,
        false,
        false);
  }

  @Test
  @DisplayName("getAllInhabitants should return list of inhabitant responses")
  void testGetAllInhabitants() {
    Long ownerId = 1L;
    when(inhabitantRepository.findByOwnerId(ownerId)).thenReturn(List.of(testInhabitant));
    when(mappingService.mapInhabitant(any(Inhabitant.class))).thenReturn(mock(InhabitantResponse.class));

    List<InhabitantResponse> result = inhabitantService.getAllInhabitants(ownerId);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(inhabitantRepository, times(1)).findByOwnerId(ownerId);
    verify(mappingService, times(1)).mapInhabitant(testInhabitant);
  }

  @Test
  @DisplayName("getInhabitantById should return inhabitant when found")
  void testGetInhabitantByIdFound() {
    when(inhabitantRepository.findById(1L)).thenReturn(Optional.of(testInhabitant));
    when(mappingService.mapInhabitant(testInhabitant)).thenReturn(testInhabitantResponse);

    InhabitantResponse result = inhabitantService.getInhabitantById(1L);

    assertEquals(testInhabitantResponse, result);
    verify(inhabitantRepository).findById(1L);
    verify(mappingService).mapInhabitant(testInhabitant);
  }

  @Test
  @DisplayName("getInhabitantById should throw exception when not found")
  void testGetInhabitantByIdNotFound() {
    when(inhabitantRepository.findById(999L)).thenReturn(Optional.empty());

    ApplicationException.NotFoundException exception = assertThrows(
        ApplicationException.NotFoundException.class,
        () -> inhabitantService.getInhabitantById(999L));

    assertTrue(exception.getMessage().contains("Inhabitant with ID 999"));
    verify(inhabitantRepository).findById(999L);
  }

  @Test
  @DisplayName("getInhabitantsByAquarium should return inhabitants for an aquarium")
  void testGetInhabitantsByAquarium() {
    testAquarium.addToInhabitants(testInhabitant);
    when(aquariumRepository.findByIdWithInhabitants(1L)).thenReturn(Optional.of(testAquarium));
    when(mappingService.mapInhabitant(testInhabitant)).thenReturn(testInhabitantResponse);

    List<InhabitantResponse> result = inhabitantService.getInhabitantsByAquarium(1L);

    assertEquals(1, result.size());
    assertEquals(testInhabitantResponse, result.get(0));
    verify(aquariumRepository).findByIdWithInhabitants(1L);
    verify(mappingService).mapInhabitant(testInhabitant);
  }

  @Test
  @DisplayName("createInhabitant should create and return new inhabitant")
  void testCreateInhabitant() {
    Long ownerId = 1L;
    InhabitantRequest request = mock(InhabitantRequest.class);
    when(request.type()).thenReturn("Fish");
    when(request.species()).thenReturn("Guppy");
    when(request.color()).thenReturn("Orange");
    when(request.count()).thenReturn(5);
    when(request.getSchoolingValue()).thenReturn(true);
    when(request.waterType()).thenReturn(WaterType.FRESH);
    when(request.getAggressiveEaterValue()).thenReturn(false);
    when(request.getRequiresSpecialFoodValue()).thenReturn(false);
    when(request.getSnailEaterValue()).thenReturn(false);
    when(request.aquariumId()).thenReturn(null);

    when(inhabitantRepository.save(any(Inhabitant.class))).thenReturn(testInhabitant);
    when(mappingService.mapInhabitant(testInhabitant)).thenReturn(testInhabitantResponse);

    InhabitantResponse result = inhabitantService.createInhabitant(request, ownerId);

    assertEquals(testInhabitantResponse, result);
    verify(inhabitantRepository).save(any(Inhabitant.class));
    verify(mappingService).mapInhabitant(testInhabitant);
  }

  @Test
  @DisplayName("createInhabitant with aquarium should add to aquarium")
  void testCreateInhabitantWithAquarium() {
    Long ownerId = 1L;
    Long aquariumId = 1L;
    InhabitantRequest requestWithAquarium = new InhabitantRequest(
        "Betta", "Blue", 1, false, WaterType.FRESH, "FISH", aquariumId, 
        false, false, false, "BettaName");
        
    when(aquariumRepository.findByIdWithInhabitants(aquariumId)).thenReturn(Optional.of(testAquarium));
    doNothing().when(testAquarium).verifyOwnership(ownerId);
    
    try (MockedStatic<Inhabitant> mockedStatic = mockStatic(Inhabitant.class)) {
         mockedStatic.when(() -> Inhabitant.createFromType(anyString(), anyString(), anyString(), anyInt(), 
                                                         anyBoolean(), any(WaterType.class), anyBoolean(), 
                                                         anyBoolean(), anyBoolean(), anyLong(), anyString()))
                    .thenReturn(testInhabitant);

        when(inhabitantRepository.save(any(Inhabitant.class))).thenReturn(testInhabitant);
        when(inhabitantRepository.findById(anyLong())).thenReturn(Optional.of(testInhabitant)); 
        when(mappingService.mapInhabitant(any(Inhabitant.class))).thenReturn(mock(InhabitantResponse.class));
        when(aquariumRepository.save(any(Aquarium.class))).thenReturn(testAquarium);
        
        InhabitantResponse response = inhabitantService.createInhabitant(requestWithAquarium, ownerId);
        
        assertNotNull(response);
        verify(inhabitantRepository, times(1)).save(testInhabitant);
        verify(aquariumRepository, times(1)).save(testAquarium);
        verify(testAquarium, times(1)).addToInhabitants(testInhabitant);
    }
  }

  @Test
  @DisplayName("deleteInhabitant should delete existing inhabitant")
  void testDeleteInhabitant() {
    when(inhabitantRepository.existsById(1L)).thenReturn(true);
    when(inhabitantRepository.findById(1L)).thenReturn(Optional.of(testInhabitant));
    doNothing().when(inhabitantRepository).deleteById(1L);

    inhabitantService.deleteInhabitant(1L);

    verify(inhabitantRepository).existsById(1L);
    verify(inhabitantRepository).findById(1L);
    verify(inhabitantRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteInhabitant should remove from aquarium if assigned")
  void testDeleteInhabitantFromAquarium() {
    testAquarium.addToInhabitants(testInhabitant);

    when(inhabitantRepository.existsById(1L)).thenReturn(true);
    when(inhabitantRepository.findById(1L)).thenReturn(Optional.of(testInhabitant));
    when(aquariumRepository.findByIdWithInhabitants(1L)).thenReturn(Optional.of(testAquarium));
    doNothing().when(inhabitantRepository).deleteById(1L);

    inhabitantService.deleteInhabitant(1L);

    verify(inhabitantRepository).existsById(1L);
    verify(inhabitantRepository).findById(1L);
    verify(aquariumRepository).findByIdWithInhabitants(1L);
    verify(aquariumRepository).save(testAquarium);
    verify(inhabitantRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Add inhabitant to aquarium should add and return updated aquarium")
  void testAddInhabitantToAquarium() {
    AquariumResponse expectedResponse = mock(AquariumResponse.class);

    Owner testOwner = Owner.create("Test", "Owner", "test@example.com");
    try {
      java.lang.reflect.Field idField = Owner.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(testOwner, 1L);
    } catch (Exception e) {
      fail("Failed to set owner ID: " + e.getMessage());
    }

    Aquarium spyAquarium = spy(testAquarium);

    spyAquarium.assignToOwner(testOwner);

    assertEquals(testOwner, spyAquarium.getOwner());
    assertTrue(testOwner.getOwnedAquariums().contains(spyAquarium));

    when(spyAquarium.isOwnedBy(any(Long.class))).thenReturn(true);

    when(aquariumRepository.findByIdWithInhabitants(1L)).thenReturn(Optional.of(spyAquarium));
    when(inhabitantRepository.findById(1L)).thenReturn(Optional.of(testInhabitant));
    when(inhabitantRepository.save(any(Inhabitant.class))).thenReturn(testInhabitant);
    when(aquariumRepository.save(any(Aquarium.class))).thenReturn(spyAquarium);
    when(aquariumRepository.findByIdWithAllCollections(1L)).thenReturn(Optional.of(spyAquarium));
    when(mappingService.mapAquariumDetailed(any(Aquarium.class))).thenReturn(expectedResponse);

    AquariumResponse result = inhabitantService.addInhabitant(
        1L, 1L, Collections.emptyMap(), 1L);

    assertEquals(expectedResponse, result);
    verify(aquariumRepository).findByIdWithInhabitants(1L);
    verify(inhabitantRepository).findById(1L);
    verify(inhabitantRepository).save(any(Inhabitant.class));
    verify(aquariumRepository).save(any(Aquarium.class));
    verify(aquariumRepository).findByIdWithAllCollections(1L);
    verify(mappingService).mapAquariumDetailed(any(Aquarium.class));
    verify(spyAquarium).verifyOwnership(eq(1L));
  }

  @Test
  void testCreateInhabitantWithoutAquarium() {
    Long ownerId = 1L;
    try (MockedStatic<Inhabitant> mockedStatic = mockStatic(Inhabitant.class)) {
        mockedStatic.when(() -> Inhabitant.createFromType(anyString(), anyString(), anyString(), anyInt(), 
                                                         anyBoolean(), any(WaterType.class), anyBoolean(), 
                                                         anyBoolean(), anyBoolean(), anyLong(), anyString()))
                    .thenReturn(testInhabitant);

        when(inhabitantRepository.save(any(Inhabitant.class))).thenReturn(testInhabitant);
        when(mappingService.mapInhabitant(any(Inhabitant.class))).thenReturn(mock(InhabitantResponse.class));
        
        InhabitantResponse response = inhabitantService.createInhabitant(testRequest, ownerId);
        
        assertNotNull(response);
        verify(inhabitantRepository, times(1)).save(testInhabitant);
    }
  }
}