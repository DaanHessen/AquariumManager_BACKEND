package nl.hu.bep.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WaterType Enum Unit Tests")
class WaterTypeTest {

    @Test
    @DisplayName("Should have correct number of values")
    void shouldHaveCorrectNumberOfValues() {
        // When
        WaterType[] values = WaterType.values();

        // Then
        assertEquals(2, values.length);
    }

    @Test
    @DisplayName("Should contain FRESHWATER value")
    void shouldContainFreshwaterValue() {
        // When & Then
        assertNotNull(WaterType.FRESHWATER);
        assertEquals("FRESHWATER", WaterType.FRESHWATER.name());
    }

    @Test
    @DisplayName("Should contain SALTWATER value")
    void shouldContainSaltwaterValue() {
        // When & Then
        assertNotNull(WaterType.SALTWATER);
        assertEquals("SALTWATER", WaterType.SALTWATER.name());
    }

    @Test
    @DisplayName("Should support valueOf for FRESHWATER")
    void shouldSupportValueOfForFreshwater() {
        // When
        WaterType waterType = WaterType.valueOf("FRESHWATER");

        // Then
        assertEquals(WaterType.FRESHWATER, waterType);
    }

    @Test
    @DisplayName("Should support valueOf for SALTWATER")
    void shouldSupportValueOfForSaltwater() {
        // When
        WaterType waterType = WaterType.valueOf("SALTWATER");

        // Then
        assertEquals(WaterType.SALTWATER, waterType);
    }

    @Test
    @DisplayName("Should throw exception for invalid valueOf")
    void shouldThrowExceptionForInvalidValueOf() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> WaterType.valueOf("INVALID"));
    }

    @Test
    @DisplayName("Should have proper ordinal values")
    void shouldHaveProperOrdinalValues() {
        // When & Then
        assertEquals(0, WaterType.FRESHWATER.ordinal());
        assertEquals(1, WaterType.SALTWATER.ordinal());
    }
}
