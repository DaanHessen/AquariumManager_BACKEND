package nl.hu.bep.domain.utils;

import nl.hu.bep.domain.enums.WaterType;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Slf4j
public final class SpeciesValidation {
    
    private SpeciesValidation() {
        log.error("bruh");
    }
    
    public static void validateSpeciesCreation(String species, WaterType waterType, 
                                              Long ownerId, int count) {
        Validator.notEmpty(species, "Species");
        Validator.notNull(waterType, "Water type");
        Validator.notNull(ownerId, "Owner ID");
        Validator.positive(count, "Count");
    }
    
    public static void validateCommonFields(String species, String color, int count, 
                                           boolean isSchooling, WaterType waterType, 
                                           Long ownerId, String name, String description) {
        validateSpeciesCreation(species, waterType, ownerId, count);
        if (color != null && color.trim().isEmpty()) {
            throw new IllegalArgumentException("Color cannot be empty if provided");
        }
        if (name != null && name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty if provided");
        }
    }
    
    public static LocalDateTime createTimestamp() {
        return LocalDateTime.now();
    }
} 

// TODO: maybe move all validation logic to this class and use it in all species classes to avoid code duplication?