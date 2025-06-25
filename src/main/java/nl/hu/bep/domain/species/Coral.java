package nl.hu.bep.domain.species;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Represents coral in an aquarium.
 * Clean POJO implementation following DDD principles.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
public class Coral extends Inhabitant {

    @Builder
    public Coral(Long id, String name, String species, Long ownerId, String color,
                 Integer count, Boolean isSchooling, WaterType waterType,
                 String description, LocalDateTime dateCreated, Long aquariumId) {
        super(id, name, species, ownerId, color, count, isSchooling, WaterType.SALTWATER, description, dateCreated, aquariumId);
        // Ensure coral-specific validation if any
        if (this.getWaterType() != WaterType.SALTWATER) {
            throw new IllegalArgumentException("Coral must be in saltwater.");
        }
    }

    @Override
    public String getType() {
        return "Coral";
    }

    @Override
    public InhabitantProperties getTypeSpecificProperties() {
        return InhabitantProperties.defaults();
    }

    public boolean isCompatibleWith(Inhabitant other) {
        if (this.getWaterType() != WaterType.SALTWATER) {
            return false; // Coral is only for saltwater aquariums
        }
        if (other.getWaterType() != WaterType.SALTWATER) {
            return false; // Coral is only compatible with other saltwater inhabitants
        }
        // More specific coral compatibility rules can be added here
        return true;
    }

    // Polymorphic methods to eliminate instanceof checks
    @Override
    public String getInhabitantType() {
        return "Coral";
    }

    @Override
    public Boolean getAggressiveEater() {
        return false; // Coral don't eat in the traditional sense
    }

    @Override
    public Boolean getRequiresSpecialFood() {
        return true; // Coral often require special feeding/lighting
    }

    @Override
    public Boolean getSnailEater() {
        return false; // Coral don't eat
    }

    // Factory methods
    public static Coral create(String species, String name, Long ownerId, Optional<String> color, Optional<Integer> count,
                             Optional<Boolean> isSchooling, Optional<WaterType> waterType,
                             Optional<String> description, InhabitantProperties properties) {
        return Coral.builder()
                .name(name)
                .species(species)
                .ownerId(ownerId)
                .color(color.orElse(null))
                .count(count.orElse(null))
                .isSchooling(isSchooling.orElse(null))
                .waterType(WaterType.SALTWATER) // Coral is always saltwater
                .description(description.orElse(null))
                .build();
    }

    public static Coral reconstruct(long id, String name, String species, int count,
                                  boolean isSchooling, WaterType waterType, Long ownerId, String color,
                                  String description, LocalDateTime dateCreated, Long aquariumId,
                                  boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        return Coral.builder()
                .id(id)
                .name(name)
                .species(species)
                .count(count)
                .isSchooling(isSchooling)
                .waterType(WaterType.SALTWATER) // Coral is always saltwater
                .ownerId(ownerId)
                .color(color)
                .description(description)
                .dateCreated(dateCreated)
                .aquariumId(aquariumId)
                .build();
    }
}
