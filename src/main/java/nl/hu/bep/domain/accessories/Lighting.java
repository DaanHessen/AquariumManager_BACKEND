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
import java.time.LocalTime;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("LIGHTING")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Setter(AccessLevel.PRIVATE)
public class Lighting extends Accessory {
  @NotNull
  @Column(name = "is_led")
  private boolean isLed;

  @NotNull
  @Column(name = "time_off")
  private LocalTime turnOffTime;

  @NotNull
  @Column(name = "time_on")
  private LocalTime turnOnTime;

  public Lighting(String model, String serialNumber, boolean isLed, LocalTime turnOffTime, LocalTime turnOnTime, Long ownerId) {
    super(model, serialNumber, ownerId);
    this.isLed = isLed;
    this.turnOffTime = Validator.notNull(turnOffTime, "Turn off time");
    this.turnOnTime = Validator.notNull(turnOnTime, "Turn on time");
  }

  public void updateProperties(boolean isLed, LocalTime turnOffTime, LocalTime turnOnTime) {
    this.isLed = isLed;
    this.turnOffTime = Validator.notNull(turnOffTime, "Turn off time");
    this.turnOnTime = Validator.notNull(turnOnTime, "Turn on time");
  }
}
