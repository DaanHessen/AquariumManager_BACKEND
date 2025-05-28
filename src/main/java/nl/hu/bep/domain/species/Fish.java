package nl.hu.bep.domain.species;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.utils.Validator;

@Entity
@DiscriminatorValue("Fish")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Setter(AccessLevel.PRIVATE)
public class Fish extends Inhabitant {
    @Column(name = "is_aggressive_eater")
    private boolean isAggressiveEater;

    @Column(name = "requires_special_food")
    private boolean requiresSpecialFood;

    @Column(name = "is_snail_eater")
    private boolean isSnailEater;

    public static Fish create(String species, String color, int count, boolean isSchooling,
            boolean isAggressiveEater, boolean requiresSpecialFood,
            WaterType waterType, boolean isSnailEater, Long ownerId, String name, String description) {
        Validator.notEmpty(species, "Species");
        Validator.notNull(waterType, "Water type");

        Fish fish = new Fish();
        fish.initializeInhabitant(species, color, count, isSchooling, waterType);
        fish.isAggressiveEater = isAggressiveEater;
        fish.requiresSpecialFood = requiresSpecialFood;
        fish.isSnailEater = isSnailEater;

        fish.setOwnerIdInternal(ownerId);
        if (name != null) {
            fish.setNameInternal(name);
        }
        if (description != null) {
            fish.updateDescription(description);
        }

        return fish;
    }

    public void updateProperties(boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        this.isAggressiveEater = isAggressiveEater;
        this.requiresSpecialFood = requiresSpecialFood;
        this.isSnailEater = isSnailEater;
    }
}
