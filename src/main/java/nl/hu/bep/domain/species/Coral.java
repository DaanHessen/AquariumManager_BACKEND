package nl.hu.bep.domain.species;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;

import java.time.LocalDateTime;

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
            return false; // no coral in freshwater
        }
        if (other.getWaterType() != WaterType.SALTWATER) {
            return false; // coral is only compatible with other saltwater inhabitants
        }
        return true;
    }

    @Override
    public String getInhabitantType() {
        return "Coral";
    }

    @Override
    public Boolean getAggressiveEater() {
        return false; // ever seen corals eat aggressive? me neither
    }

    @Override
    public Boolean getRequiresSpecialFood() {
        return true; // idk what they eat but I am making it special
    }

    @Override
    public Boolean getSnailEater() {
        return false; // corals do not eat snails
    }
}
