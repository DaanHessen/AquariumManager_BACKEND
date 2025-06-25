package nl.hu.bep.domain.species;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents a shrimp in an aquarium.
 * Clean POJO implementation following DDD principles.
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Shrimp extends Inhabitant {

    @Builder
    public Shrimp(Long id, String name, String species, Long ownerId, String color, Integer count, Boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId) {
        super(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
    }

    @Override
    public String getType() {
        return "Shrimp";
    }

    @Override
    public InhabitantProperties getTypeSpecificProperties() {
        return InhabitantProperties.defaults(); // Shrimp has no special properties
    }

    public boolean isCompatibleWith(Inhabitant other) {
        if (other instanceof Fish fish && fish.getAggressiveEater()) {
            return false; // Aggressive fish might eat shrimp
        }
        return true; // Generally peaceful
    }

    // Polymorphic methods to eliminate instanceof checks
    @Override
    public String getInhabitantType() {
        return "Shrimp";
    }

    @Override
    public Boolean getAggressiveEater() {
        return false; // Shrimp are not aggressive eaters
    }

    @Override
    public Boolean getRequiresSpecialFood() {
        return false; // Shrimp typically don't require special food
    }

    @Override
    public Boolean getSnailEater() {
        return false; // Shrimp don't eat snails
    }

    // Factory methods
    public static Shrimp create(String species, String name, Long ownerId, Optional<String> color, Optional<Integer> count,
                                Optional<Boolean> isSchooling, Optional<WaterType> waterType,
                                Optional<String> description, InhabitantProperties properties) {
        return Shrimp.builder()
                .name(name)
                .species(species)
                .ownerId(ownerId)
                .color(color.orElse(null))
                .count(count.orElse(null))
                .isSchooling(isSchooling.orElse(null))
                .waterType(waterType.orElse(null))
                .description(description.orElse(null))
                .build();
    }

    public static Shrimp reconstruct(long id, String name, String species, int count,
                                     boolean isSchooling, WaterType waterType, Long ownerId, String color,
                                     String description, LocalDateTime dateCreated, Long aquariumId,
                                     boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        return Shrimp.builder()
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
                .build();
    }
}
