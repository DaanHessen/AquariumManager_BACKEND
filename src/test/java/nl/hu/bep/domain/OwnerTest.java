package nl.hu.bep.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nl.hu.bep.domain.enums.Role;

class OwnerTest {

  @Test
  @DisplayName("create should initialize Owner with correct values")
  void testCreateOwner() {
    String firstName = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";

    Owner owner = Owner.create(firstName, lastName, email);

    assertNotNull(owner);
    assertEquals(firstName, owner.getFirstName());
    assertEquals(lastName, owner.getLastName());
    assertEquals(email, owner.getEmail());
    assertEquals(Role.OWNER, owner.getRole());
    assertTrue(owner.getOwnedAquariums().isEmpty());
    assertNull(owner.getAquariumManager());
  }

  @Test
  @DisplayName("create with password should set hashed password")
  void testCreateOwnerWithPassword() {
    String firstName = "Jane";
    String lastName = "Smith";
    String email = "jane.smith@example.com";
    String password = "securePassword123";

    Owner owner = Owner.create(firstName, lastName, email, password);

    assertNotNull(owner);
    assertNotNull(owner.getPassword());
    assertNotEquals(password, owner.getPassword());
  }

  @Test
  @DisplayName("updateEmail should change email")
  void testUpdateEmail() {
    Owner owner = Owner.create("Test", "User", "test@example.com");
    String newEmail = "updated@example.com";

    owner.updateEmail(newEmail);

    assertEquals(newEmail, owner.getEmail());
  }

  @Test
  @DisplayName("updateRole should change role")
  void testUpdateRole() {
    Owner owner = Owner.create("Test", "User", "test@example.com");
    assertEquals(Role.OWNER, owner.getRole());

    owner.updateRole("ADMIN");

    assertEquals(Role.ADMIN, owner.getRole());
  }

  @Test
  @DisplayName("updateLastLogin should set current time")
  void testUpdateLastLogin() {
    Owner owner = Owner.create("Test", "User", "test@example.com");
    assertNull(owner.getLastLogin());

    LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);
    owner.updateLastLogin();
    LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);

    assertNotNull(owner.getLastLogin());
    assertTrue(owner.getLastLogin().isAfter(beforeUpdate) || owner.getLastLogin().equals(beforeUpdate));
    assertTrue(owner.getLastLogin().isBefore(afterUpdate) || owner.getLastLogin().equals(afterUpdate));
  }

  @Test
  @DisplayName("Aquariums management should work correctly")
  void testAquariumsManagement() {
    Owner owner = Owner.create("Test", "User", "test@example.com");
    Aquarium aquarium = Aquarium.create(
        "Test Aquarium", 100.0, 40.0, 50.0,
        nl.hu.bep.domain.enums.SubstrateType.GRAVEL,
        nl.hu.bep.domain.enums.WaterType.FRESH);

    owner.addToAquariums(aquarium);

    assertEquals(1, owner.getOwnedAquariums().size());
    assertTrue(owner.getOwnedAquariums().contains(aquarium));
    assertEquals(owner, aquarium.getOwner());

    owner.removeFromAquariums(aquarium);

    assertTrue(owner.getOwnedAquariums().isEmpty());
    assertNull(aquarium.getOwner());
  }

  @Test
  @DisplayName("AquariumManager assignment should work correctly")
  void testManagerAssignment() {
    Owner owner = Owner.create("Test", "User", "test@example.com");
    AquariumManager manager = new AquariumManager();
    try {
      java.lang.reflect.Field idField = AquariumManager.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(manager, 1L);
    } catch (Exception e) {
      fail("Failed to set manager ID: " + e.getMessage());
    }

    owner.assignToManager(manager);

    assertEquals(manager, owner.getAquariumManager());
    assertTrue(manager.getOwners().contains(owner));

    owner.unassignFromManager();

    assertNull(owner.getAquariumManager());
    assertFalse(manager.getOwners().contains(owner));
  }
}