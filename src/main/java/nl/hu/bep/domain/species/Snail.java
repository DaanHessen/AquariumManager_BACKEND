package nl.hu.bep.domain.species;

import lombok.*;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.utils.Validator;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a snail in an aquarium.
 * Clean POJO implementation following DDD principles.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Setter(AccessLevel.PRIVATE)
public class Snail extends Inhabitant {
    private boolean isSnailEater;

    // Private constructor for creating new entities
    private Snail(String name, String species, Long ownerId, Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling, Optional<WaterType> waterType, Optional<String> description, InhabitantProperties properties) {
        super(name, species, ownerId, color, count, isSchooling, waterType, description);
        this.isSnailEater = properties.isSnailEater();
    }

    // Private constructor for repository reconstruction
    private Snail(Long id, String name, String species, Long ownerId, String color, int count, boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId, boolean isSnailEater) {
        super(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
        this.isSnailEater = isSnailEater;
    }

    // Static factory method for creating a new Snail instance
    public static Snail create(String name, String species, Long ownerId, Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling, Optional<WaterType> waterType, Optional<String> description, InhabitantProperties properties) {
        return new Snail(name, species, ownerId, color, count, isSchooling, waterType, description, properties);
    }

    // Static factory method for repository reconstruction
    public static Snail reconstruct(Long id, String name, String species, Long ownerId, String color, int count, boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId, boolean isSnailEater) {
        return new Snail(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId, isSnailEater);
    }

    @Override
    public String getType() {
        return "Snail";
    }

    @Override
    public InhabitantProperties getTypeSpecificProperties() {
        return new InhabitantProperties(false, false, isSnailEater);
    }

    // Business logic methods
    public void updateProperties(boolean isSnailEater) {
        this.isSnailEater = isSnailEater;
    }
}
