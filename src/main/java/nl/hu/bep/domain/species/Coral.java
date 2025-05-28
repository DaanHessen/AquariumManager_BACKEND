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
@DiscriminatorValue("Coral")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Setter(AccessLevel.PRIVATE)
public class Coral extends Inhabitant {
    public static Coral create(String species, String color, int count, boolean isSchooling, WaterType waterType, Long ownerId, String name, String description) {
        Coral coral = new Coral();
        coral.initializeInhabitant(species, color, count, isSchooling, waterType);
        coral.setOwnerIdInternal(ownerId);
        if (name != null) {
            coral.setNameInternal(name);
        }
        if (description != null) {
            coral.updateDescription(description);
        }
        return coral;
    }
}
