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
 * Represents a plant in an aquarium.
 * Clean POJO implementation following DDD principles.
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Plant extends Inhabitant {

    @Builder
    public Plant(Long id, String name, String species, Long ownerId, String color, Integer count, Boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId) {
        super(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
    }

    @Override
    public String getType() {
        return "Plant";
    }

    @Override
    public InhabitantProperties getTypeSpecificProperties() {
        return InhabitantProperties.defaults(); // Plants have no special properties
    }

    public boolean isCompatibleWith(Inhabitant other) {
        // Herbivorous fish might eat plants. For now, we assume compatibility.
        // This could be extended with more specific rules.
        return true;
    }

    // Polymorphic methods to eliminate instanceof checks
    @Override
    public String getInhabitantType() {
        return "Plant";
    }

    @Override
    public Boolean getAggressiveEater() {
        return false; // Plants don't eat
    }

    @Override
    public Boolean getRequiresSpecialFood() {
        return false; // Plants photosynthesize
    }

    @Override
    public Boolean getSnailEater() {
        return false; // Plants don't eat
    }

    // Factory methods
    public static Plant create(String species, String name, Long ownerId, Optional<String> color, Optional<Integer> count,
                               Optional<Boolean> isSchooling, Optional<WaterType> waterType,
                               Optional<String> description, InhabitantProperties properties) {
        return Plant.builder()
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

    public static Plant reconstruct(long id, String name, String species, int count,
                                    boolean isSchooling, WaterType waterType, Long ownerId, String color,
                                    String description, LocalDateTime dateCreated, Long aquariumId,
                                    boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        return Plant.builder()
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
