package nl.hu.bep.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role Enum Unit Tests")
class RoleTest {

    @Test
    @DisplayName("Should have correct number of values")
    void shouldHaveCorrectNumberOfValues() {
        // When
        Role[] values = Role.values();

        // Then
        assertEquals(2, values.length);
    }

    @Test
    @DisplayName("Should contain OWNER value")
    void shouldContainOwnerValue() {
        // When & Then
        assertNotNull(Role.OWNER);
        assertEquals("OWNER", Role.OWNER.name());
    }

    @Test
    @DisplayName("Should contain ADMIN value")
    void shouldContainAdminValue() {
        // When & Then
        assertNotNull(Role.ADMIN);
        assertEquals("ADMIN", Role.ADMIN.name());
    }

    @Test
    @DisplayName("Should support valueOf for all roles")
    void shouldSupportValueOfForAllRoles() {
        // When & Then
        assertEquals(Role.OWNER, Role.valueOf("OWNER"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }

    @Test
    @DisplayName("Should throw exception for invalid valueOf")
    void shouldThrowExceptionForInvalidValueOf() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> Role.valueOf("INVALID"));
    }

    @Test
    @DisplayName("Should have proper ordinal values")
    void shouldHaveProperOrdinalValues() {
        // When & Then
        assertEquals(0, Role.OWNER.ordinal());
        assertEquals(1, Role.ADMIN.ordinal());
    }

    @Test
    @DisplayName("Should verify role hierarchy conceptually")
    void shouldVerifyRoleHierarchyConceptually() {
        // Given - This test documents the expected role hierarchy
        // OWNER - Regular user who owns aquariums
        // ADMIN - Administrative user with elevated privileges
        
        // When & Then - Verify both roles exist for authorization system
        assertNotNull(Role.OWNER);
        assertNotNull(Role.ADMIN);
        
        // Verify ADMIN has higher ordinal (assuming higher privilege)
        assertTrue(Role.ADMIN.ordinal() > Role.OWNER.ordinal());
    }
}
