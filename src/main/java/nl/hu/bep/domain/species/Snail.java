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
            return false; // can't have fish eating snails now
        }
        return true;
    }

    @Override
    public String getInhabitantType() {
        return "Snail";
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
        return this.isSnailEater;
    }
}
