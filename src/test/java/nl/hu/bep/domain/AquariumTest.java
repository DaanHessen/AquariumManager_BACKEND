package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import nl.hu.bep.domain.species.Fish;
import nl.hu.bep.exception.domain.DomainException;

import static org.junit.jupiter.api.Assertions.*;

class AquariumTest {

  @Test
  @DisplayName("Creating an aquarium should initialize with correct values")
  void testCreateAquarium() {
    String name = "Test Aquarium";
    double length = 100.0;
    double width = 40.0;
    double height = 50.0;
    SubstrateType substrate = SubstrateType.GRAVEL;
    WaterType waterType = WaterType.FRESH;

    Aquarium aquarium = Aquarium.create(name, length, width, height, substrate, waterType);

    assertNotNull(aquarium);
    assertEquals(name, aquarium.getName());
    assertEquals(length, aquarium.getDimensions().getLength());
    assertEquals(width, aquarium.getDimensions().getWidth());
    assertEquals(height, aquarium.getDimensions().getHeight());
    assertEquals(substrate, aquarium.getSubstrate());
    assertEquals(waterType, aquarium.getWaterType());
    assertEquals(AquariumState.SETUP, aquarium.getState());
    assertEquals(24.0, aquarium.getTemperature());
    assertTrue(aquarium.getAccessories().isEmpty());
    assertTrue(aquarium.getInhabitants().isEmpty());
    assertTrue(aquarium.getOrnaments().isEmpty());
  }

  @Test
  @DisplayName("Aquarium state transitions should work correctly")
  void testAquariumStateTransitions() {
    Aquarium aquarium = createTestAquarium();

    assertEquals(AquariumState.SETUP, aquarium.getState());
    aquarium.activateAquarium();
    assertEquals(AquariumState.RUNNING, aquarium.getState());

    aquarium.startMaintenance();
    assertEquals(AquariumState.MAINTENANCE, aquarium.getState());

    aquarium.activateAquarium();
    assertEquals(AquariumState.RUNNING, aquarium.getState());

    aquarium.deactivateAquarium();
    assertEquals(AquariumState.INACTIVE, aquarium.getState());
  }

  @Test
  @DisplayName("Invalid state transitions should throw exceptions")
  void testInvalidStateTransitions() {
    Aquarium aquarium = createTestAquarium();
    aquarium.deactivateAquarium();

    DomainException exception = assertThrows(DomainException.class,
        aquarium::activateAquarium);
    assertTrue(exception.getMessage().contains("Cannot activate aquarium from"));

    aquarium.updateState(AquariumState.RUNNING);
    aquarium.startMaintenance();
    assertEquals(AquariumState.MAINTENANCE, aquarium.getState());

    aquarium.updateState(AquariumState.SETUP);

    exception = assertThrows(DomainException.class,
        aquarium::startMaintenance);
    assertTrue(exception.getMessage().contains("Cannot start maintenance"));
  }

  @Test
  @DisplayName("Adding an inhabitant with compatible water type should succeed")
  void testAddCompatibleInhabitant() {
    Aquarium aquarium = createTestAquarium();
    aquarium.activateAquarium();

    Inhabitant inhabitant = Inhabitant.createFromType(
        "Fish", "Guppy", "Orange", 5,
        true, WaterType.FRESH, false, false, false, 1L, null, null);

    aquarium.addToInhabitants(inhabitant);

    assertEquals(1, aquarium.getInhabitants().size());
    assertTrue(aquarium.getInhabitants().contains(inhabitant));
    assertEquals(aquarium, inhabitant.getAquarium());
  }

  @Test
  @DisplayName("Adding an inhabitant with incompatible water type should throw exception")
  void testAddIncompatibleInhabitant() {
    Aquarium aquarium = createTestAquarium();
    aquarium.activateAquarium();

    Inhabitant saltWaterFish = Inhabitant.createFromType(
        "Fish", "Clownfish", "Orange", 2,
        false, WaterType.SALT, false, false, false, 1L, null, null);

    DomainException exception = assertThrows(DomainException.IncompatibleWaterTypeException.class,
        () -> aquarium.addToInhabitants(saltWaterFish));
    assertTrue(exception.getMessage().contains("Incompatible water types"));
  }

  @Test
  @DisplayName("Adding an inhabitant to inactive aquarium should throw exception")
  void testAddInhabitantToInactiveAquarium() {
    Aquarium aquarium = createTestAquarium();
    aquarium.deactivateAquarium();

    Inhabitant inhabitant = Inhabitant.createFromType(
        "Fish", "Guppy", "Orange", 5,
        true, WaterType.FRESH, false, false, false, 1L, null, null);

    DomainException exception = assertThrows(DomainException.class,
        () -> aquarium.addToInhabitants(inhabitant));
    assertTrue(exception.getMessage().contains("Cannot add inhabitants to an inactive aquarium"));
  }

  @Test
  @DisplayName("Calculating volume should return correct value")
  void testGetVolume() {
    double length = 100.0;
    double width = 40.0;
    double height = 50.0;
    Aquarium aquarium = createTestAquarium(length, width, height);

    double expectedVolume = (length * width * height) / 1000.0;

    assertEquals(expectedVolume, aquarium.getVolume());
  }

  @Test
  @DisplayName("Owner assignment should work correctly")
  void testOwnerAssignment() {
    Aquarium aquarium = createTestAquarium();
    Owner owner = Owner.create("Test", "Owner", "test@example.com", "password123");
    try {
      java.lang.reflect.Field idField = Owner.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(owner, 1L);
    } catch (Exception e) {
      fail("Failed to set owner ID: " + e.getMessage());
    }

    aquarium.assignToOwner(owner);

    assertEquals(owner, aquarium.getOwner());
    assertTrue(aquarium.isOwnedBy(1L));

    aquarium.unassignFromOwner();

    assertNull(aquarium.getOwner());
    assertFalse(aquarium.isOwnedBy(1L));
  }

  @Test
  @DisplayName("Update method should update all properties correctly")
  void testUpdate() {
    Aquarium aquarium = createTestAquarium();
    String newName = "Updated Aquarium";
    double newLength = 120.0;
    double newWidth = 60.0;
    double newHeight = 70.0;
    SubstrateType newSubstrate = SubstrateType.SAND;
    WaterType newWaterType = WaterType.SALT;
    AquariumState newState = AquariumState.MAINTENANCE;
    Double newTemperature = 28.5;

    aquarium.update(newName, newLength, newWidth, newHeight,
        newSubstrate, newWaterType, newState, newTemperature);

    assertEquals(newName, aquarium.getName());
    assertEquals(newLength, aquarium.getDimensions().getLength());
    assertEquals(newWidth, aquarium.getDimensions().getWidth());
    assertEquals(newHeight, aquarium.getDimensions().getHeight());
    assertEquals(newSubstrate, aquarium.getSubstrate());
    assertEquals(newWaterType, aquarium.getWaterType());
    assertEquals(newState, aquarium.getState());
    assertEquals(newTemperature, aquarium.getTemperature());
  }

  @Test
  void testCreateAndAddFish() {
    // Arrange
    Aquarium aquarium = Aquarium.create("Test Aqua", 60, 30, 30, SubstrateType.GRAVEL, WaterType.FRESH);
    // Use Owner.create with a password String
    Owner owner = Owner.create("owner", "pass", "owner@mail.com", "password"); 
    try { // Set ID via reflection if needed for the test
        java.lang.reflect.Field idField = Owner.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(owner, 1L);
    } catch (Exception e) { fail("Failed to set owner ID: " + e.getMessage()); }
    aquarium.assignToOwner(owner);

    // Act
    Fish fish = aquarium.createAndAddFish("Neon Tetra", "Blue/Red", 10, true, false, false, WaterType.FRESH, false, null);

    // Assert
    assertNotNull(fish);
    assertEquals(1, aquarium.getInhabitants().size());
    assertTrue(aquarium.getInhabitants().contains(fish));
    assertEquals(aquarium, fish.getAquarium());
    assertEquals(owner.getId(), fish.getOwnerId()); // Verify owner ID is set
    assertNull(fish.getName()); // Verify name is null as it wasn't provided
  }
  
  @Test
  void testCreateAndAddOrnament() {
      // Arrange
      Aquarium aquarium = Aquarium.create("Test Aqua", 60, 30, 30, SubstrateType.GRAVEL, WaterType.FRESH);
      // Use Owner.create with a password String
      Owner owner = Owner.create("owner", "pass", "owner@mail.com", "password"); 
      try { // Set ID via reflection if needed
          java.lang.reflect.Field idField = Owner.class.getDeclaredField("id");
          idField.setAccessible(true);
          idField.set(owner, 1L);
      } catch (Exception e) { fail("Failed to set owner ID: " + e.getMessage()); }
      aquarium.assignToOwner(owner);

      // Act
      Ornament ornament = aquarium.createAndAddOrnament("Castle", "Big", "Gray", true);

      // Assert
      assertNotNull(ornament);
      assertEquals(1, aquarium.getOrnaments().size());
      assertTrue(aquarium.getOrnaments().contains(ornament));
      assertEquals(aquarium, ornament.getAquarium());
      assertEquals(owner.getId(), ornament.getOwnerId()); // Verify owner ID is set
  }

  private Aquarium createTestAquarium() {
    return createTestAquarium(100.0, 40.0, 50.0);
  }

  private Aquarium createTestAquarium(double length, double width, double height) {
    return Aquarium.create(
        "Test Aquarium",
        length,
        width,
        height,
        SubstrateType.GRAVEL,
        WaterType.FRESH);
  }
}