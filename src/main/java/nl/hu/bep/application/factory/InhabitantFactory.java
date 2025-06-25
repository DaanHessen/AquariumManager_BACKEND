package nl.hu.bep.application.factory;

import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.species.*;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * Application-layer factory for creating different types of inhabitants.
 * This replaces the problematic static factory method on the abstract Inhabitant class.
 */
@ApplicationScoped
public class InhabitantFactory {
    
    public Inhabitant createInhabitant(String type, String species, String name, Long ownerId, 
                                     Optional<String> color, Optional<Integer> count, 
                                     Optional<Boolean> isSchooling, Optional<WaterType> waterType, 
                                     Optional<String> description, Inhabitant.InhabitantProperties properties) {
        
        return switch (type.toLowerCase()) {
            case "fish" -> Fish.create(species, name, ownerId, color, count, isSchooling, waterType, description, properties);
            case "plant" -> Plant.create(species, name, ownerId, color, count, isSchooling, waterType, description, properties);
            case "snail" -> Snail.create(species, name, ownerId, color, count, isSchooling, waterType, description, properties);
            case "shrimp" -> Shrimp.create(species, name, ownerId, color, count, isSchooling, waterType, description, properties);
            case "crayfish" -> Crayfish.create(species, name, ownerId, color, count, isSchooling, waterType, description, properties);
            case "coral" -> Coral.create(species, name, ownerId, color, count, isSchooling, waterType, description, properties);
            default -> throw new ApplicationException.ValidationException("Invalid inhabitant type: " + type);
        };
    }
}
