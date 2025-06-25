package nl.hu.bep.application.factory;

import nl.hu.bep.domain.species.*;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;

import java.util.Optional;

public class InhabitantFactory {
    
    public Inhabitant createInhabitant(String type, String species, String name, Long ownerId, 
                                     Optional<String> color, Optional<Integer> count, 
                                     Optional<Boolean> isSchooling, Optional<WaterType> waterType, 
                                     Optional<String> description, Inhabitant.InhabitantProperties properties) {
        
        return switch (type.toLowerCase()) {
            case "fish" -> Fish.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .isAggressiveEater(properties != null ? properties.isAggressiveEater : false)
                    .requiresSpecialFood(properties != null ? properties.requiresSpecialFood : false)
                    .isSnailEater(properties != null ? properties.isSnailEater : false)
                    .build();
                    
            case "plant" -> Plant.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .build();
                    
            case "snail" -> Snail.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .isSnailEater(properties != null ? properties.isSnailEater : false)
                    .build();
                    
            case "shrimp" -> Shrimp.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .build();
                    
            case "crayfish" -> Crayfish.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .build();
                    
            case "coral" -> Coral.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .build();
                    
            default -> throw new ApplicationException.ValidationException("Invalid inhabitant type: " + type);
        };
    }
}
