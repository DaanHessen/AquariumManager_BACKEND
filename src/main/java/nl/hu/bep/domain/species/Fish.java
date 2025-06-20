package nl.hu.bep.domain.species;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.utils.Validator;

/**
 * Represents a fish in an aquarium.
 * Clean POJO implementation following DDD principles.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Setter(AccessLevel.PRIVATE)
public class Fish extends Inhabitant {
    private boolean isAggressiveEater;
    private boolean requiresSpecialFood;
    private boolean isSnailEater;

    // Private constructor for factory method
    private Fish(String species, String color, int count, boolean isSchooling,
                WaterType waterType, Long ownerId, String name, String description,
                boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        super(species, color, count, isSchooling, waterType, ownerId, name, description);
        this.isAggressiveEater = isAggressiveEater;
        this.requiresSpecialFood = requiresSpecialFood;
        this.isSnailEater = isSnailEater;
    }

    public static Fish create(String species, String color, int count, boolean isSchooling,
            boolean isAggressiveEater, boolean requiresSpecialFood,
            WaterType waterType, boolean isSnailEater, Long ownerId, String name, String description) {
        Validator.notEmpty(species, "Species");
        Validator.notNull(waterType, "Water type");
        Validator.notNull(ownerId, "Owner ID");

        return new Fish(species, color, count, isSchooling, waterType, ownerId, name, description,
                       isAggressiveEater, requiresSpecialFood, isSnailEater);
    }

    @Override
    public String getType() {
        return "Fish";
    }

    public void updateProperties(boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        this.isAggressiveEater = isAggressiveEater;
        this.requiresSpecialFood = requiresSpecialFood;
        this.isSnailEater = isSnailEater;
    }
}
