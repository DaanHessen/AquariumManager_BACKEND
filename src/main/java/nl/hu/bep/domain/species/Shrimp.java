package nl.hu.bep.domain.species;

import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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
        return InhabitantProperties.defaults();
    }

    public boolean isCompatibleWith(Inhabitant other) {
        if (other instanceof Fish fish && fish.getAggressiveEater()) {
            return false; // aggressive fish munch on shrimp
        }
        return true;
    }

    @Override
    public String getInhabitantType() {
        return "Shrimp";
    }

    @Override
    public Boolean getAggressiveEater() {
        return false;
    }

    @Override
    public Boolean getRequiresSpecialFood() {
        return false;
    }

    @Override
    public Boolean getSnailEater() {
        return false;
    }
}
