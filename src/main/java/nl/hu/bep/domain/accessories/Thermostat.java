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
import jakarta.persistence.DiscriminatorValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@DiscriminatorValue("THERMOSTAT")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Setter(AccessLevel.PRIVATE)
public class Thermostat extends Accessory {
  @NotNull
  @Positive
  private double minTemperature;

  @NotNull
  @Positive
  private double maxTemperature;

  @NotNull
  private double currentTemperature;

  public Thermostat(String model, String serialNumber, double minTemperature, double maxTemperature,
      double currentTemperature, Long ownerId) {
    super(model, serialNumber, ownerId);
    this.minTemperature = Validator.positive(minTemperature, "Minimum temperature");
    this.maxTemperature = Validator.positive(maxTemperature, "Maximum temperature");
    this.currentTemperature = Validator.positive(currentTemperature, "Current temperature");
  }

  public void updateProperties(double minTemperature, double maxTemperature, double currentTemperature) {
    this.minTemperature = Validator.positive(minTemperature, "Minimum temperature");
    this.maxTemperature = Validator.positive(maxTemperature, "Maximum temperature");
    this.currentTemperature = currentTemperature;
  }

  public void adjustMinTemperature(double minTemperature) {
    this.minTemperature = Validator.positive(minTemperature, "Minimum temperature");
  }

  public void adjustMaxTemperature(double maxTemperature) {
    this.maxTemperature = Validator.positive(maxTemperature, "Maximum temperature");
  }
}
