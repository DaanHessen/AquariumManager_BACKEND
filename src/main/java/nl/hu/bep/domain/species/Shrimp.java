package nl.hu.bep.domain.species;

import lombok.*;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.utils.Validator;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a shrimp in an aquarium.
 * Clean POJO implementation following DDD principles.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
public class Shrimp extends Inhabitant {

    // Private constructor for creating new entities
    private Shrimp(String name, String species, Long ownerId, Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling, Optional<WaterType> waterType, Optional<String> description) {
        super(name, species, ownerId, color, count, isSchooling, waterType, description);
    }

    // Private constructor for repository reconstruction
    private Shrimp(Long id, String name, String species, Long ownerId, String color, int count, boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId) {
        super(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
    }

    // Static factory method for creating a new Shrimp instance
    public static Shrimp create(String name, String species, Long ownerId, Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling, Optional<WaterType> waterType, Optional<String> description) {
        return new Shrimp(name, species, ownerId, color, count, isSchooling, waterType, description);
    }

    // Static factory method for repository reconstruction
    public static Shrimp reconstruct(Long id, String name, String species, Long ownerId, String color, int count, boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId) {
        return new Shrimp(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
    }

    @Override
    public String getType() {
        return "Shrimp";
    }

    @Override
    public InhabitantProperties getTypeSpecificProperties() {
        return InhabitantProperties.defaults(); // Shrimp has no special properties
    }
}
