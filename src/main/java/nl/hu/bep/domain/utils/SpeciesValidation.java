package nl.hu.bep.domain.utils;

import nl.hu.bep.domain.enums.WaterType;

/**
 * Shared validation and creation patterns for all species - eliminates duplication.
 * Template method pattern for consistent species creation.
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
    
    /**
     * Template method for species creation - ensures consistent validation
     */
    public static void validateCommonFields(String species, String color, int count, 
                                           boolean isSchooling, WaterType waterType, 
                                           Long ownerId, String name, String description) {
        validateSpeciesCreation(species, waterType, ownerId, count);
        // Additional validations can be added here
        if (color != null && color.trim().isEmpty()) {
            throw new IllegalArgumentException("Color cannot be empty if provided");
        }
        if (name != null && name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty if provided");
        }
    }
    
    /**
     * Common date creation logic
     */
    public static java.time.LocalDateTime createTimestamp() {
        return java.time.LocalDateTime.now();
    }
} 