package nl.hu.bep.domain.species;

import lombok.*;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.base.SpeciesValidation;

/**
 * Represents a coral in an aquarium.
 * Clean POJO implementation without JPA dependencies.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coral extends Inhabitant {

    // Private constructor for factory method
    private Coral(String species, String color, int count, boolean isSchooling,
                 WaterType waterType, Long ownerId, String name, String description) {
        super(species, color, count, isSchooling, waterType, ownerId, name, description);
    }

    // Factory method with validation
    public static Coral create(String species, String color, int count, boolean isSchooling, 
            WaterType waterType, Long ownerId, String name, String description) {
        
        // Use shared validation - no more duplication!
        SpeciesValidation.validateSpeciesCreation(species, waterType, ownerId, count);
        
        return new Coral(species, color, count, isSchooling, waterType, ownerId, name, description);
    }

    @Override
    public String getType() {
        return "Coral";
    }
}
