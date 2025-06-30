package nl.hu.bep.domain.utils;

import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException.BusinessRuleException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesValidationTest {

    @Test
    void testValidateSpeciesCreationWithValidData() {
        assertDoesNotThrow(() -> {
            SpeciesValidation.validateSpeciesCreation("Goldfish", WaterType.FRESHWATER, 1L, 5);
        });
    }

    @Test
    @Disabled
    void testValidateSpeciesCreationWithNullSpecies() {
        assertThrows(BusinessRuleException.class, () -> {
            SpeciesValidation.validateSpeciesCreation(null, WaterType.FRESHWATER, 1L, 5);
        });
    }

    @Test
    @Disabled
    void testValidateSpeciesCreationWithEmptySpecies() {
        assertThrows(BusinessRuleException.class, () -> {
            SpeciesValidation.validateSpeciesCreation("", WaterType.FRESHWATER, 1L, 5);
        });
    }

    @Test
    @Disabled
    void testValidateSpeciesCreationWithNullWaterType() {
        assertThrows(BusinessRuleException.class, () -> {
            SpeciesValidation.validateSpeciesCreation("Goldfish", null, 1L, 5);
        });
    }

    @Test
    @Disabled
    void testValidateSpeciesCreationWithNullOwnerId() {
        assertThrows(BusinessRuleException.class, () -> {
            SpeciesValidation.validateSpeciesCreation("Goldfish", WaterType.FRESHWATER, null, 5);
        });
    }

    @Test
    @Disabled
    void testValidateSpeciesCreationWithZeroCount() {
        assertThrows(BusinessRuleException.class, () -> {
            SpeciesValidation.validateSpeciesCreation("Goldfish", WaterType.FRESHWATER, 1L, 0);
        });
    }

    @Test
    @Disabled
    void testValidateSpeciesCreationWithNegativeCount() {
        assertThrows(BusinessRuleException.class, () -> {
            SpeciesValidation.validateSpeciesCreation("Goldfish", WaterType.FRESHWATER, 1L, -1);
        });
    }

    @Test
    void testValidateCommonFieldsWithValidData() {
        assertDoesNotThrow(() -> {
            SpeciesValidation.validateCommonFields(
                "Goldfish", "Orange", 5, true, WaterType.FRESHWATER, 1L, "My Fish", "Beautiful fish"
            );
        });
    }

    @Test
    void testValidateCommonFieldsWithEmptyColor() {
        assertThrows(BusinessRuleException.class, () -> {
            SpeciesValidation.validateCommonFields(
                "Goldfish", "", 5, true, WaterType.FRESHWATER, 1L, "My Fish", "Beautiful fish"
            );
        });
    }

    @Test
    void testValidateCommonFieldsWithEmptyName() {
        assertThrows(BusinessRuleException.class, () -> {
            SpeciesValidation.validateCommonFields(
                "Goldfish", "Orange", 5, true, WaterType.FRESHWATER, 1L, "", "Beautiful fish"
            );
        });
    }

    @Test
    void testValidateCommonFieldsWithNullColor() {
        assertDoesNotThrow(() -> {
            SpeciesValidation.validateCommonFields(
                "Goldfish", null, 5, true, WaterType.FRESHWATER, 1L, "My Fish", "Beautiful fish"
            );
        });
    }

    @Test
    void testValidateCommonFieldsWithNullName() {
        assertDoesNotThrow(() -> {
            SpeciesValidation.validateCommonFields(
                "Goldfish", "Orange", 5, true, WaterType.FRESHWATER, 1L, null, "Beautiful fish"
            );
        });
    }

    @Test
    void testCreateTimestamp() {
        LocalDateTime before = LocalDateTime.now();
        LocalDateTime timestamp = SpeciesValidation.createTimestamp();
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(timestamp);
        assertTrue(timestamp.isAfter(before.minusSeconds(1)));
        assertTrue(timestamp.isBefore(after.plusSeconds(1)));
    }
}
