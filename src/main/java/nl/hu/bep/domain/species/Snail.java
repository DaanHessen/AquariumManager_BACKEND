package nl.hu.bep.domain.species;

import nl.hu.bep.domain.enums.WaterType;

import jakarta.persistence.Entity;
import nl.hu.bep.domain.Inhabitant;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

@Entity
@DiscriminatorValue("Snail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Setter(AccessLevel.PRIVATE)
@Slf4j
public class Snail extends Inhabitant {
  @NotNull
  @Column(name = "is_snail_eater")
  private boolean isSnailEater;

  public static Snail create(String species, String color, int count, boolean isSchooling, boolean isSnailEater,
      WaterType waterType, Long ownerId, String name, String description) {
    Snail snail = new Snail();
    snail.initializeInhabitant(species, color, count, isSchooling, waterType);
    snail.isSnailEater = isSnailEater;
    snail.setOwnerIdInternal(ownerId);
    if (name != null) {
      snail.setNameInternal(name);
    }
    if (description != null) {
      snail.updateDescription(description);
    }
    log.info("Creating new Snail: species={}, color={}, count={}, isSchooling={}, isSnailEater={}, waterType={}",
        species, color, count, isSchooling, isSnailEater, waterType);
    return snail;
  }

  public void updateProperties(boolean isSnailEater) {
    this.isSnailEater = isSnailEater;
  }
}
