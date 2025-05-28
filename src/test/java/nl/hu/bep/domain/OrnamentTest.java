package nl.hu.bep.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nl.hu.bep.domain.enums.WaterType;

class OrnamentTest {

  @Test
  @DisplayName("create should initialize Ornament with correct values")
  void testCreateOrnament() {
    String name = "Castle";
    String description = "A decorative castle for aquarium";
    String color = "Gray";
    boolean isAirPumpCompatible = false;
    Long ownerId = 1L;
    String material = "Stone";

    Ornament ornament = new Ornament(name, description, color, isAirPumpCompatible, ownerId, material);

    assertNotNull(ornament);
    assertEquals(name, ornament.getName());
    assertEquals(description, ornament.getDescription());
    assertEquals(color, ornament.getColor());
    assertEquals(isAirPumpCompatible, ornament.isAirPumpCompatible());
    assertEquals(ownerId, ornament.getOwnerId());
    assertEquals(material, ornament.getMaterial());
    assertNotNull(ornament.getDateCreated());
    assertNull(ornament.getAquarium());
  }

  @Test
  @DisplayName("update method should update properties correctly")
  void testUpdateOrnament() {
    Ornament ornament = new Ornament(
        "Pirate Ship", "A sunken pirate ship decoration", "Black", false, 1L, "Wood");

    String newName = "Sunken Ship";
    String newDescription = "A more realistic sunken ship";
    String newColor = "Brown";
    boolean newIsAirPumpCompatible = true;
    String newMaterial = "Metal";

    ornament.update(newName, newDescription, newColor, newIsAirPumpCompatible, newMaterial);

    assertEquals(newName, ornament.getName());
    assertEquals(newDescription, ornament.getDescription());
    assertEquals(newColor, ornament.getColor());
    assertEquals(newIsAirPumpCompatible, ornament.isAirPumpCompatible());
    assertEquals(newMaterial, ornament.getMaterial());
    assertEquals(1L, ornament.getOwnerId());
  }

  @Test
  @DisplayName("Aquarium assignment should work correctly")
  void testAquariumAssignment() {
    Ornament ornament = new Ornament(
        "Log", "A decorative log for hiding", "Brown", false, 1L, "Wood");

    Aquarium aquarium = Aquarium.create(
        "Test Aquarium", 100.0, 40.0, 50.0,
        nl.hu.bep.domain.enums.SubstrateType.GRAVEL, WaterType.FRESH);

    aquarium.addToOrnaments(ornament);

    assertEquals(aquarium, ornament.getAquarium());
    assertTrue(aquarium.getOrnaments().contains(ornament));

    aquarium.removeFromOrnaments(ornament);

    assertNull(ornament.getAquarium());
    assertFalse(aquarium.getOrnaments().contains(ornament));
  }

  @Test
  @DisplayName("AirPump compatibility should be stored correctly")
  void testAirPumpCompatibility() {
    Ornament incompatibleOrnament = new Ornament(
        "Plant", "Decorative plant", "Green", false, 1L, "Plastic");

    Ornament compatibleOrnament = new Ornament(
        "Bubbling Rock", "A rock with air pump attachment", "Gray", true, 1L, "Stone");

    assertFalse(incompatibleOrnament.isAirPumpCompatible());
    assertTrue(compatibleOrnament.isAirPumpCompatible());
  }
}