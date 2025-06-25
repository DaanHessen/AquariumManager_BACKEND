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
 * Represents a snail in an aquarium.
 * Clean POJO implementation following DDD principles.
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Snail extends Inhabitant {
    private boolean isSnailEater;

    @Builder
    public Snail(Long id, String name, String species, Long ownerId, String color, Integer count, Boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId, Boolean isSnailEater) {
        super(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
        this.isSnailEater = isSnailEater != null ? isSnailEater : false;
    }

    @Override
    public String getType() {
        return "Snail";
    }

    @Override
    public InhabitantProperties getTypeSpecificProperties() {
        return new InhabitantProperties(false, false, isSnailEater);
    }

    public boolean isCompatibleWith(Inhabitant other) {
        if (other instanceof Fish fish && fish.getSnailEater()) {
            return false; // Snail-eating fish are not compatible with snails
        }
        return true; // Snails are generally peaceful
    }

    // Polymorphic methods to eliminate instanceof checks
    @Override
    public String getInhabitantType() {
        return "Snail";
    }

    @Override
    public Boolean getAggressiveEater() {
        return false; // Snails are not aggressive eaters
    }

    @Override
    public Boolean getRequiresSpecialFood() {
        return false; // Snails typically don't require special food
    }

    @Override
    public Boolean getSnailEater() {
        return this.isSnailEater;
    }

    // Factory methods
    public static Snail create(String species, String name, Long ownerId, Optional<String> color, Optional<Integer> count,
                            Optional<Boolean> isSchooling, Optional<WaterType> waterType,
                            Optional<String> description, InhabitantProperties properties) {
        return Snail.builder()
                .name(name)
                .species(species)
                .ownerId(ownerId)
                .color(color.orElse(null))
                .count(count.orElse(null))
                .isSchooling(isSchooling.orElse(null))
                .waterType(waterType.orElse(null))
                .description(description.orElse(null))
                .isSnailEater(properties != null ? properties.isSnailEater : false)
                .build();
    }

    public static Snail reconstruct(long id, String name, String species, int count,
                                 boolean isSchooling, WaterType waterType, Long ownerId, String color,
                                 String description, LocalDateTime dateCreated, Long aquariumId,
                                 boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        return Snail.builder()
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
                .isSnailEater(isSnailEater)
                .build();
    }
}
