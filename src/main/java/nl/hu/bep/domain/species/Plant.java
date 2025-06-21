package nl.hu.bep.domain.species;

import lombok.*;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.utils.Validator;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a plant in an aquarium.
 * Clean POJO implementation following DDD principles.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
public class Plant extends Inhabitant {

    // Private constructor for creating new entities
    private Plant(String name, String species, Long ownerId, Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling, Optional<WaterType> waterType, Optional<String> description) {
        super(name, species, ownerId, color, count, isSchooling, waterType, description);
    }

    // Private constructor for repository reconstruction
    private Plant(Long id, String name, String species, Long ownerId, String color, int count, boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId) {
        super(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
    }

    // Static factory method for creating a new Plant instance
    public static Plant create(String name, String species, Long ownerId, Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling, Optional<WaterType> waterType, Optional<String> description) {
        return new Plant(name, species, ownerId, color, count, isSchooling, waterType, description);
    }

    // Static factory method for repository reconstruction
    public static Plant reconstruct(Long id, String name, String species, Long ownerId, String color, int count, boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId) {
        return new Plant(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
    }

    @Override
    public String getType() {
        return "Plant";
    }

    @Override
    public InhabitantProperties getTypeSpecificProperties() {
        return InhabitantProperties.defaults(); // Plants have no special properties
    }
}
