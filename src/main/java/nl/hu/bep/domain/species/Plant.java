package nl.hu.bep.domain.species;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;

@Entity
@DiscriminatorValue("Plant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Setter(AccessLevel.PRIVATE)
public class Plant extends Inhabitant {
    public static Plant create(String species, String color, int count, boolean isSchooling, WaterType waterType, Long ownerId, String name, String description) {
        Plant plant = new Plant();
        plant.initializeInhabitant(species, color, count, isSchooling, waterType);
        plant.setOwnerIdInternal(ownerId);
        if (name != null) {
            plant.setNameInternal(name);
        }
        if (description != null) {
            plant.updateDescription(description);
        }
        return plant;
    }
}
