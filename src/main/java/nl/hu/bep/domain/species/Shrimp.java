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
@DiscriminatorValue("SHRIMP")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Setter(AccessLevel.PRIVATE)
@Slf4j
public class Shrimp extends Inhabitant {

  public static Shrimp create(String species, String color, int count, boolean isSchooling,
      WaterType waterType, Long ownerId, String name, String description) {
    Shrimp shrimp = new Shrimp();
    shrimp.initializeInhabitant(species, color, count, isSchooling, waterType);
    shrimp.setOwnerIdInternal(ownerId);
    if (name != null) {
      shrimp.setNameInternal(name);
    }
    if (description != null) {
      shrimp.updateDescription(description);
    }
    log.info("Creating new Shrimp: species={}, color={}, count={}, isSchooling={}, waterType={}",
        species, color, count, isSchooling, waterType);
    return shrimp;
  }
}
