package nl.hu.bep.domain.species;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.base.SpeciesValidation;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.exception.domain.DomainException;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a fish in an aquarium.
 * Rich domain model with business logic following DDD principles.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Setter(AccessLevel.PRIVATE)
public class Fish extends Inhabitant {
    private boolean isAggressiveEater;
    private boolean requiresSpecialFood;
    private boolean isSnailEater;

    // Private constructor for creating new entities
    private Fish(String name, String species, Long ownerId, Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling, Optional<WaterType> waterType, Optional<String> description, InhabitantProperties properties) {
        super(name, species, ownerId, color, count, isSchooling, waterType, description);
        this.isAggressiveEater = properties.isAggressiveEater();
        this.requiresSpecialFood = properties.isRequiresSpecialFood();
        this.isSnailEater = properties.isSnailEater();
    }
    
    // Private constructor for repository reconstruction
    private Fish(Long id, String name, String species, Long ownerId, String color, int count, boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId, boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        super(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
        this.isAggressiveEater = isAggressiveEater;
        this.requiresSpecialFood = requiresSpecialFood;
        this.isSnailEater = isSnailEater;
    }

    // Static factory method for creating a new Fish instance
    public static Fish create(String name, String species, Long ownerId, Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling, Optional<WaterType> waterType, Optional<String> description, InhabitantProperties properties) {
        // Here you could add fish-specific validation if needed
        return new Fish(name, species, ownerId, color, count, isSchooling, waterType, description, properties);
    }

    // Static factory method for repository reconstruction
    public static Fish reconstruct(Long id, String name, String species, Long ownerId, String color, int count, boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId, boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        return new Fish(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId, isAggressiveEater, requiresSpecialFood, isSnailEater);
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
        validateFishSpecificRules(isAggressiveEater, requiresSpecialFood, isSnailEater, this.getCount());
        this.isAggressiveEater = isAggressiveEater;
        this.requiresSpecialFood = requiresSpecialFood;
        this.isSnailEater = isSnailEater;
    }
    
    // Rich domain behavior - business logic methods
    public boolean isCompatibleWith(Fish otherFish) {
        // Aggressive eaters are not compatible with non-aggressive fish
        if (this.isAggressiveEater && !otherFish.isAggressiveEater) {
            return false;
        }
        
        // Snail eaters might eat other small fish
        if (this.isSnailEater && otherFish.getCount() < 5) {
            return false;
        }
        
        // Water type must match
        return this.getWaterType() == otherFish.getWaterType();
    }
    
    public boolean requiresSpecialCare() {
        return requiresSpecialFood || isAggressiveEater;
    }
    
    public int getRecommendedSchoolSize() {
        if (!isSchooling()) {
            return 1;
        }
        
        // Aggressive fish need smaller schools
        if (isAggressiveEater) {
            return Math.max(3, getCount());
        }
        
        // Regular schooling fish
        return Math.max(5, getCount());
    }
    
    public String getFeedingInstructions() {
        if (requiresSpecialFood && isAggressiveEater) {
            return "Requires special diet and separate feeding to prevent aggression";
        } else if (requiresSpecialFood) {
            return "Requires specialized food - follow species-specific guidelines";
        } else if (isAggressiveEater) {
            return "Feed separately or monitor during feeding to prevent aggressive behavior";
        } else {
            return "Standard community feeding schedule appropriate";
        }
    }
    
    // Private business rule validation
    private static void validateFishSpecificRules(boolean isAggressiveEater, boolean requiresSpecialFood, 
                                                 boolean isSnailEater, int count) {
        // Business rule: Aggressive eaters in large groups can be problematic
        if (isAggressiveEater && count > 10) {
            throw new DomainException("Aggressive fish should not be kept in groups larger than 10");
        }
        
        // Business rule: Snail eaters need adequate space
        if (isSnailEater && count > 15) {
            throw new DomainException("Snail-eating fish should not be kept in groups larger than 15");
        }
        
        // Business rule: Special food requirements for large groups
        if (requiresSpecialFood && count > 20) {
            throw new DomainException("Fish requiring special food should not exceed 20 individuals for proper care");
        }
    }
}
