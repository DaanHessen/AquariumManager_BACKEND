package nl.hu.bep.domain.species;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;

import java.time.LocalDateTime;

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
        return InhabitantProperties.defaults();
    }

    public boolean isCompatibleWith(Inhabitant other) {
        return true; // dont care
    }

    @Override
    public String getInhabitantType() {
        return "Plant";
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
