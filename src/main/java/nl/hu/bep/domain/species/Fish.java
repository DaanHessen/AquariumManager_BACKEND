package nl.hu.bep.domain.species;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Fish extends Inhabitant {
    private boolean isAggressiveEater;
    private boolean requiresSpecialFood;
    private boolean isSnailEater;

    @Builder
    public Fish(Long id, String name, String species, Long ownerId, String color,
                Integer count, Boolean isSchooling, WaterType waterType,
                String description, LocalDateTime dateCreated, Long aquariumId,
                Boolean isAggressiveEater, Boolean requiresSpecialFood, Boolean isSnailEater) {
        super(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
        this.isAggressiveEater = isAggressiveEater != null ? isAggressiveEater : false;
        this.requiresSpecialFood = requiresSpecialFood != null ? requiresSpecialFood : false;
        this.isSnailEater = isSnailEater != null ? isSnailEater : false;
        validateFishSpecificRules();
    }

    @Override
    public String getType() {
        return "Fish";
    }

    @Override
    public InhabitantProperties getTypeSpecificProperties() {
        return new InhabitantProperties(isAggressiveEater, requiresSpecialFood, isSnailEater);
    }

    public void updateProperties(boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        this.isAggressiveEater = isAggressiveEater;
        this.requiresSpecialFood = requiresSpecialFood;
        this.isSnailEater = isSnailEater;
        validateFishSpecificRules();
    }

    @Override
    public boolean isCompatibleWith(Inhabitant other) {
        if (this.isSnailEater && "Snail".equals(other.getInhabitantType())) {
            return false; // fish that eat snails shouldn't be with snails
        }

        if (!(other instanceof Fish otherFish)) return true;

        // Aggressive eaters are incompatible with non-aggressive fish of the same or smaller size
        if (this.isAggressiveEater && !otherFish.isAggressiveEater && this.getCount() >= otherFish.getCount()) {
            return false;
        }

        return true;
    }

    private void validateFishSpecificRules() {
        return;
    }

    @Override
    public String getInhabitantType() {
        return "Fish";
    }

    @Override
    public Boolean getAggressiveEater() {
        return this.isAggressiveEater;
    }

    @Override
    public Boolean getRequiresSpecialFood() {
        return this.requiresSpecialFood;
    }

    @Override
    public Boolean getSnailEater() {
        return this.isSnailEater;
    }
}
