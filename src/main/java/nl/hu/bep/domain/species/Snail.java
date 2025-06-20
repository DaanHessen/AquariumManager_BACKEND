package nl.hu.bep.domain.species;

import lombok.*;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.base.SpeciesValidation;

/**
 * Represents a snail in an aquarium.
 * Contains snail-specific properties.
 * Clean POJO implementation without JPA dependencies.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Snail extends Inhabitant {
    private boolean isSnailEater;

    // Private constructor for factory method
    private Snail(String species, String color, int count, boolean isSchooling,
                 WaterType waterType, Long ownerId, String name, String description,
                 boolean isSnailEater) {
        super(species, color, count, isSchooling, waterType, ownerId, name, description);
        this.isSnailEater = isSnailEater;
    }

    // Factory method with validation
    public static Snail create(String species, String color, int count, boolean isSchooling, boolean isSnailEater,
            WaterType waterType, Long ownerId, String name, String description) {
        
        // Use shared validation - no more duplication!
        SpeciesValidation.validateSpeciesCreation(species, waterType, ownerId, count);
        
        return new Snail(species, color, count, isSchooling, waterType, ownerId, name, description, isSnailEater);
    }

    @Override
    public String getType() {
        return "Snail";
    }

    // Business logic methods
    public void updateProperties(boolean isSnailEater) {
        this.isSnailEater = isSnailEater;
    }
}
