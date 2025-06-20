package nl.hu.bep.domain.base;

import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.utils.Validator;

/**
 * Shared validation for all species - eliminates duplication.
 * Every species was repeating the same validation logic.
 */
public final class SpeciesValidation {
    
    private SpeciesValidation() {} // Utility class
    
    /**
     * Standard validation ALL species need - no more duplication!
     */
    public static void validateSpeciesCreation(String species, WaterType waterType, 
                                              Long ownerId, int count) {
        Validator.notEmpty(species, "Species");
        Validator.notNull(waterType, "Water type");
        Validator.notNull(ownerId, "Owner ID");
        Validator.positive(count, "Count");
    }
} 