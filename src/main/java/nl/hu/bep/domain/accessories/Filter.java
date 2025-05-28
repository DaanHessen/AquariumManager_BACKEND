package nl.hu.bep.domain.accessories;

import nl.hu.bep.domain.Accessory;
import nl.hu.bep.domain.utils.Validator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Setter;
import lombok.AccessLevel;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("FILTER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Setter(AccessLevel.PRIVATE)
public class Filter extends Accessory {
  @NotNull
  private boolean isExternal;

  @NotNull
  @Positive
  private int capacityLiters;

  public Filter(String model, String serialNumber, boolean isExternal, int capacityLiters, Long ownerId) {
    super(model, serialNumber, ownerId);
    this.isExternal = isExternal;
    this.capacityLiters = Validator.positive(capacityLiters, "Filter capacity");
  }

  public void updateProperties(boolean isExternal, int capacityLiters) {
    this.isExternal = isExternal;
    this.capacityLiters = Validator.positive(capacityLiters, "Filter capacity");
  }
}
