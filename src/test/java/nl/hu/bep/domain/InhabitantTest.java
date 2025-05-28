package nl.hu.bep.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.species.Fish;

class InhabitantTest {

  @Test
  @DisplayName("Inhabitant.createFromType should create correct Fish instance")
  void testCreateFishInhabitant() {
    String type = "Fish";
    String species = "Guppy";
    String color = "Orange";
    int count = 5;
    boolean isSchooling = true;
    WaterType waterType = WaterType.FRESH;
    boolean isAggressiveEater = false;
    boolean requiresSpecialFood = true;
    boolean isSnailEater = false;
    long ownerId = 1L;
    String name = "Finny";

    Inhabitant inhabitant = Inhabitant.createFromType(
        type, species, color, count, isSchooling, waterType,
        isAggressiveEater, requiresSpecialFood, isSnailEater, ownerId, name);

    assertNotNull(inhabitant);
    assertTrue(inhabitant instanceof Fish);
    assertEquals(species, inhabitant.getSpecies());
    assertEquals(color, inhabitant.getColor());
    assertEquals(count, inhabitant.getCount());
    assertEquals(isSchooling, inhabitant.isSchooling());
    assertEquals(waterType, inhabitant.getWaterType());
    assertEquals(ownerId, inhabitant.getOwnerId());
    assertEquals(name, inhabitant.getName());

    Fish fish = (Fish) inhabitant;
    assertEquals(isAggressiveEater, fish.isAggressiveEater());
    assertEquals(requiresSpecialFood, fish.isRequiresSpecialFood());
    assertEquals(isSnailEater, fish.isSnailEater());
  }

  @Test
  @DisplayName("Inhabitant update method should update properties correctly")
  void testUpdateInhabitant() {
    Inhabitant inhabitant = Inhabitant.createFromType(
        "Fish", "Tetra", "Blue", 3,
        true, WaterType.FRESH, false, false, false, 1L, null);

    String newSpecies = "Neon Tetra";
    String newColor = "Red";
    int newCount = 10;
    boolean newIsSchooling = false;
    WaterType newWaterType = WaterType.SALT;
    String updatedName = "Bluey";

    inhabitant.update(newSpecies, newColor, newCount, newIsSchooling, newWaterType, updatedName);

    assertEquals(newSpecies, inhabitant.getSpecies());
    assertEquals(newColor, inhabitant.getColor());
    assertEquals(newCount, inhabitant.getCount());
    assertEquals(newIsSchooling, inhabitant.isSchooling());
    assertEquals(newWaterType, inhabitant.getWaterType());
    assertEquals(updatedName, inhabitant.getName());
    assertEquals(1L, inhabitant.getOwnerId());
  }

  @Test
  @DisplayName("Fish properties should be updated correctly")
  void testFishUpdateProperties() {
    Fish fish = (Fish) Inhabitant.createFromType(
        "Fish", "Goldfish", "Gold", 1,
        false, WaterType.FRESH, false, false, false, 1L, "Goldy");

    boolean newIsAggressiveEater = true;
    boolean newRequiresSpecialFood = true;
    boolean newIsSnailEater = true;

    fish.updateProperties(newIsAggressiveEater, newRequiresSpecialFood, newIsSnailEater);

    assertEquals(newIsAggressiveEater, fish.isAggressiveEater());
    assertEquals(newRequiresSpecialFood, fish.isRequiresSpecialFood());
    assertEquals(newIsSnailEater, fish.isSnailEater());
    assertEquals(1L, fish.getOwnerId());
    assertEquals("Goldy", fish.getName());
  }

  @Test
  @DisplayName("Aquarium assignment should work correctly")
  void testAquariumAssignment() {
    Inhabitant inhabitant = Inhabitant.createFromType(
        "Fish", "Guppy", "Orange", 5,
        true, WaterType.FRESH, false, true, false, 1L, "Orangey");

    Aquarium aquarium = Aquarium.create(
        "Test Aquarium", 100.0, 40.0, 50.0,
        nl.hu.bep.domain.enums.SubstrateType.GRAVEL, WaterType.FRESH);

    aquarium.activateAquarium();
    aquarium.addToInhabitants(inhabitant);

    assertEquals(aquarium, inhabitant.getAquarium());
    assertTrue(aquarium.getInhabitants().contains(inhabitant));

    aquarium.removeFromInhabitants(inhabitant);

    assertNull(inhabitant.getAquarium());
    assertFalse(aquarium.getInhabitants().contains(inhabitant));
    assertEquals(1L, inhabitant.getOwnerId());
    assertEquals("Orangey", inhabitant.getName());
  }
}