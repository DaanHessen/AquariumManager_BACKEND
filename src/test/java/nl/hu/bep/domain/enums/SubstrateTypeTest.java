package nl.hu.bep.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SubstrateType Enum Unit Tests")
class SubstrateTypeTest {

    @Test
    @DisplayName("Should have correct number of values")
    void shouldHaveCorrectNumberOfValues() {
        // When
        SubstrateType[] values = SubstrateType.values();

        // Then
        assertEquals(3, values.length);
    }

    @Test
    @DisplayName("Should contain SAND value")
    void shouldContainSandValue() {
        // When & Then
        assertNotNull(SubstrateType.SAND);
        assertEquals("SAND", SubstrateType.SAND.name());
    }

    @Test
    @DisplayName("Should contain GRAVEL value")
    void shouldContainGravelValue() {
        // When & Then
        assertNotNull(SubstrateType.GRAVEL);
        assertEquals("GRAVEL", SubstrateType.GRAVEL.name());
    }

    @Test
    @DisplayName("Should contain SOIL value")
    void shouldContainSoilValue() {
        // When & Then
        assertNotNull(SubstrateType.SOIL);
        assertEquals("SOIL", SubstrateType.SOIL.name());
    }

    @Test
    @DisplayName("Should support valueOf for all substrate types")
    void shouldSupportValueOfForAllSubstrateTypes() {
        // When & Then
        assertEquals(SubstrateType.SAND, SubstrateType.valueOf("SAND"));
        assertEquals(SubstrateType.GRAVEL, SubstrateType.valueOf("GRAVEL"));
        assertEquals(SubstrateType.SOIL, SubstrateType.valueOf("SOIL"));
    }

    @Test
    @DisplayName("Should throw exception for invalid valueOf")
    void shouldThrowExceptionForInvalidValueOf() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> SubstrateType.valueOf("INVALID"));
    }

    @Test
    @DisplayName("Should have proper ordinal values")
    void shouldHaveProperOrdinalValues() {
        // When & Then
        assertEquals(0, SubstrateType.SAND.ordinal());
        assertEquals(1, SubstrateType.GRAVEL.ordinal());
        assertEquals(2, SubstrateType.SOIL.ordinal());
    }

    @Test
    @DisplayName("Should verify substrate characteristics conceptually")
    void shouldVerifySubstrateCharacteristicsConceptually() {
        // Given - This test documents the expected substrate characteristics
        // SAND - Fine particles, good for bottom dwellers
        // GRAVEL - Medium particles, general purpose
        // SOIL - Nutrient-rich, good for planted tanks
        
        // When & Then - Verify all substrate types exist for aquarium setup
        assertNotNull(SubstrateType.SAND);
        assertNotNull(SubstrateType.GRAVEL);
        assertNotNull(SubstrateType.SOIL);
    }
}
