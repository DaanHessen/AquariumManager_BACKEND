package nl.hu.bep.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class AquariumManagerTest {

  @Test
  @DisplayName("create should initialize AquariumManager with correct values")
  void testCreateAquariumManager() {
    LocalDate installDate = LocalDate.now();
    AquariumManager manager = AquariumManager.create(installDate);

    assertNotNull(manager);
    assertEquals(installDate, manager.getInstallationDate());
    assertTrue(manager.getOwners().isEmpty());
    assertTrue(manager.getAquariums().isEmpty());
    assertTrue(manager.getInhabitants().isEmpty());
  }

  @Test
  @DisplayName("Owner management should work correctly")
  void testOwnerManagement() {
    AquariumManager manager = AquariumManager.create(LocalDate.now());
    Owner owner1 = Owner.create("John", "Doe", "john@example.com");
    Owner owner2 = Owner.create("Jane", "Smith", "jane@example.com");

    setId(manager, 1L);
    setId(owner1, 1L);
    setId(owner2, 2L);

    manager.addToOwners(owner1);
    manager.addToOwners(owner2);

    assertEquals(2, manager.getOwners().size());
    assertTrue(manager.getOwners().contains(owner1));
    assertTrue(manager.getOwners().contains(owner2));
    assertEquals(manager, owner1.getAquariumManager());
    assertEquals(manager, owner2.getAquariumManager());

    manager.removeFromOwners(owner1);

    assertEquals(1, manager.getOwners().size());
    assertFalse(manager.getOwners().contains(owner1));
    assertTrue(manager.getOwners().contains(owner2));
    assertNull(owner1.getAquariumManager());
    assertEquals(manager, owner2.getAquariumManager());
  }

  @Test
  @DisplayName("Aquarium management should work correctly")
  void testAquariumManagement() {
    AquariumManager manager = AquariumManager.create(LocalDate.now());
    Aquarium aquarium = Aquarium.create(
        "Test Aquarium", 100.0, 40.0, 50.0,
        nl.hu.bep.domain.enums.SubstrateType.GRAVEL,
        nl.hu.bep.domain.enums.WaterType.FRESH);

    setId(aquarium, 1L);
    setId(manager, 1L);

    manager.addToAquariums(aquarium);

    assertEquals(1, manager.getAquariums().size());
    assertTrue(manager.getAquariums().contains(aquarium));
    assertEquals(manager, aquarium.getAquariumManager());

    manager.removeFromAquariums(aquarium);

    assertTrue(manager.getAquariums().isEmpty());
    assertNull(aquarium.getAquariumManager());
  }

  @Test
  @DisplayName("Inhabitant management should work correctly")
  void testInhabitantManagement() {
    AquariumManager manager = AquariumManager.create(LocalDate.now());
    Aquarium aquarium = Aquarium.create("Test", 10, 10, 10, nl.hu.bep.domain.enums.SubstrateType.SAND, nl.hu.bep.domain.enums.WaterType.FRESH);
    Owner owner = Owner.create("owner", "pass", "owner@mail.com", "password");
    try {
        java.lang.reflect.Field idField = Owner.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(owner, 1L);
    } catch (Exception e) { fail("Failed to set owner ID: " + e.getMessage()); }
    aquarium.assignToOwner(owner);
    
    Inhabitant inhabitant = Inhabitant.createFromType("fish", "Guppy", "Red", 5, true, nl.hu.bep.domain.enums.WaterType.FRESH, false, false, false, owner.getId(), null);
    aquarium.addToInhabitants(inhabitant);

    setId(manager, 1L);
    setId(inhabitant, 1L);

    manager.addToInhabitants(inhabitant);

    assertEquals(1, manager.getInhabitants().size());
    assertTrue(manager.getInhabitants().contains(inhabitant));
    assertEquals(manager, inhabitant.getAquariumManager());

    manager.removeFromInhabitants(inhabitant);

    assertTrue(manager.getInhabitants().isEmpty());
    assertNull(inhabitant.getAquariumManager());
  }

  private void setId(Object entity, Long id) {
    try {
      String className = entity.getClass().getSimpleName();
      Class<?> clazz = entity.getClass();

      java.lang.reflect.Field idField = null;
      while (clazz != null && idField == null) {
        try {
          idField = clazz.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
          clazz = clazz.getSuperclass();
        }
      }

      if (idField == null) {
        throw new NoSuchFieldException("id field not found in class hierarchy");
      }

      idField.setAccessible(true);
      idField.set(entity, id);
    } catch (Exception e) {
      fail("Failed to set ID: " + e.getMessage());
    }
  }
}