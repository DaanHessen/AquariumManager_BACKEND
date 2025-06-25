package nl.hu.bep.domain.species;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.domain.DomainException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Optional;

/**
 * Represents a fish in an aquarium.
 * Rich domain model with business logic following DDD principles.
 */
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

    // Business logic methods
    public void updateProperties(boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        this.isAggressiveEater = isAggressiveEater;
        this.requiresSpecialFood = requiresSpecialFood;
        this.isSnailEater = isSnailEater;
        validateFishSpecificRules();
    }

    // Rich domain behavior - business logic methods
    @Override
    public boolean isCompatibleWith(Inhabitant other) {
        // Snail eaters are incompatible with snails
        if (this.isSnailEater && "Snail".equals(other.getInhabitantType())) {
            return false;
        }

        if (!(other instanceof Fish otherFish)) return true; // Fish are compatible with non-fish (except snails when snail eater)

        // Aggressive eaters are incompatible with non-aggressive fish of the same or smaller size
        if (this.isAggressiveEater && !otherFish.isAggressiveEater && this.getCount() >= otherFish.getCount()) {
            return false;
        }

        return true;
    }

    // Factory methods
    public static Fish create(String species, String name, Long ownerId, Optional<String> color, Optional<Integer> count, 
                            Optional<Boolean> isSchooling, Optional<WaterType> waterType, 
                            Optional<String> description, InhabitantProperties properties) {
        return Fish.builder()
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
    }

    public static Fish reconstruct(long id, String name, String species, int count, 
                                 boolean isSchooling, WaterType waterType, Long ownerId, String color, 
                                 String description, LocalDateTime dateCreated, Long aquariumId, 
                                 boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        return Fish.builder()
                .id(id)
                .name(name)
                .species(species)
                .count(count)
                .isSchooling(isSchooling)
                .waterType(waterType)
                .ownerId(ownerId)
                .color(color)
                .description(description)
                .dateCreated(dateCreated)
                .aquariumId(aquariumId)
                .isAggressiveEater(isAggressiveEater)
                .requiresSpecialFood(requiresSpecialFood)
                .isSnailEater(isSnailEater)
                .build();
    }

    private void validateFishSpecificRules() {
        // Example validation: Snail eaters cannot be schooling fish
        if (this.isSnailEater && this.isSchooling()) {
            throw new DomainException("Snail-eating fish cannot be schooling fish.");
        }
    }

    // Polymorphic methods to eliminate instanceof checks
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
