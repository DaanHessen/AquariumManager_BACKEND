package nl.hu.bep.domain.species;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

@Entity
@DiscriminatorValue("CRAYFISH")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Setter(AccessLevel.PRIVATE)
@Slf4j
public class Crayfish extends Inhabitant {

  public static Crayfish create(String species, String color, int count, boolean isSchooling,
      WaterType waterType, Long ownerId, String name, String description) {
    Crayfish crayfish = new Crayfish();
    crayfish.initializeInhabitant(species, color, count, isSchooling, waterType);
    crayfish.setOwnerIdInternal(ownerId);
    if (name != null) {
      crayfish.setNameInternal(name);
    }
    if (description != null) {
      crayfish.updateDescription(description);
    }
    log.info("Creating new Crayfish: species={}, color={}, count={}, isSchooling={}, waterType={}",
        species, color, count, isSchooling, waterType);
    return crayfish;
  }
}
