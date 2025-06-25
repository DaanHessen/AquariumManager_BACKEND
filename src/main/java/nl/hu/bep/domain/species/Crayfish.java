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
public class Crayfish extends Inhabitant {

    @Builder
    public Crayfish(Long id, String name, String species, Long ownerId, String color, Integer count, Boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId) {
        super(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
    }

    @Override
    public String getType() {
        return "Crayfish";
    }

    @Override
    public InhabitantProperties getTypeSpecificProperties() {
        return InhabitantProperties.defaults();
    }

    public boolean isCompatibleWith(Inhabitant other) {
        if (other instanceof Fish) {
            return false; // crayfish don't like fish 
        }
        if (other instanceof Shrimp) {
            return false; // they apparently eat shrimp
        }
        return true;
    }

    @Override
    public String getInhabitantType() {
        return "Crayfish";
    }

    @Override
    public Boolean getAggressiveEater() {
        return true; // they are aggressive eaters
    }

    @Override
    public Boolean getRequiresSpecialFood() {
        return false; // they eat like trashcans
    }

    @Override
    public Boolean getSnailEater() {
        return false; // they do not like escargots
    }
}
