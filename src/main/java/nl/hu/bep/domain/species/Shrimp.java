package nl.hu.bep.domain.species;

import lombok.*;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.base.SpeciesValidation;

/**
 * Represents a shrimp in an aquarium.
 * Clean POJO implementation without JPA dependencies.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shrimp extends Inhabitant {

    // Private constructor for factory method
    private Shrimp(String species, String color, int count, boolean isSchooling,
                  WaterType waterType, Long ownerId, String name, String description) {
        super(species, color, count, isSchooling, waterType, ownerId, name, description);
    }

    // Factory method with validation
    public static Shrimp create(String species, String color, int count, boolean isSchooling,
            WaterType waterType, Long ownerId, String name, String description) {
        
        // Use shared validation - no more duplication!
        SpeciesValidation.validateSpeciesCreation(species, waterType, ownerId, count);
        
        return new Shrimp(species, color, count, isSchooling, waterType, ownerId, name, description);
    }

    @Override
    public String getType() {
        return "Shrimp";
    }
}
