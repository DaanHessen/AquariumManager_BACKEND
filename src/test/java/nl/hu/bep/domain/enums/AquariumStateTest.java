package nl.hu.bep.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AquariumState Enum Unit Tests")
class AquariumStateTest {

    @Test
    @DisplayName("Should have correct number of values")
    void shouldHaveCorrectNumberOfValues() {
        // When
        AquariumState[] values = AquariumState.values();

        // Then
        assertEquals(4, values.length);
    }

    @Test
    @DisplayName("Should contain SETUP value")
    void shouldContainSetupValue() {
        // When & Then
        assertNotNull(AquariumState.SETUP);
        assertEquals("SETUP", AquariumState.SETUP.name());
    }

    @Test
    @DisplayName("Should contain RUNNING value")
    void shouldContainRunningValue() {
        // When & Then
        assertNotNull(AquariumState.RUNNING);
        assertEquals("RUNNING", AquariumState.RUNNING.name());
    }

    @Test
    @DisplayName("Should contain MAINTENANCE value")
    void shouldContainMaintenanceValue() {
        // When & Then
        assertNotNull(AquariumState.MAINTENANCE);
        assertEquals("MAINTENANCE", AquariumState.MAINTENANCE.name());
    }

    @Test
    @DisplayName("Should contain INACTIVE value")
    void shouldContainInactiveValue() {
        // When & Then
        assertNotNull(AquariumState.INACTIVE);
        assertEquals("INACTIVE", AquariumState.INACTIVE.name());
    }

    @Test
    @DisplayName("Should support valueOf for all states")
    void shouldSupportValueOfForAllStates() {
        // When & Then
        assertEquals(AquariumState.SETUP, AquariumState.valueOf("SETUP"));
        assertEquals(AquariumState.RUNNING, AquariumState.valueOf("RUNNING"));
        assertEquals(AquariumState.MAINTENANCE, AquariumState.valueOf("MAINTENANCE"));
        assertEquals(AquariumState.INACTIVE, AquariumState.valueOf("INACTIVE"));
    }

    @Test
    @DisplayName("Should throw exception for invalid valueOf")
    void shouldThrowExceptionForInvalidValueOf() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> AquariumState.valueOf("INVALID"));
    }

    @Test
    @DisplayName("Should have proper ordinal values")
    void shouldHaveProperOrdinalValues() {
        // When & Then
        assertEquals(0, AquariumState.SETUP.ordinal());
        assertEquals(1, AquariumState.RUNNING.ordinal());
        assertEquals(2, AquariumState.MAINTENANCE.ordinal());
        assertEquals(3, AquariumState.INACTIVE.ordinal());
    }

    @Test
    @DisplayName("Should verify state progression logic conceptually")
    void shouldVerifyStateProgressionLogicConceptually() {
        // Given - This test documents the expected state transitions
        // SETUP -> RUNNING (aquarium is ready)
        // RUNNING -> MAINTENANCE (periodic maintenance)
        // MAINTENANCE -> RUNNING (maintenance complete)
        // Any state -> INACTIVE (when aquarium is shut down)
        
        // When & Then - Verify states exist for logical progression
        assertNotNull(AquariumState.SETUP);    // Initial state
        assertNotNull(AquariumState.RUNNING);  // Active operational state
        assertNotNull(AquariumState.MAINTENANCE); // Temporary maintenance state
        assertNotNull(AquariumState.INACTIVE); // Final/paused state
    }
}
