package nl.hu.bep.domain;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.domain.accessories.Filter;
import nl.hu.bep.domain.accessories.Lighting;
import nl.hu.bep.domain.accessories.Thermostat;
import java.time.LocalTime;

@Entity
@Table(name = "accessories")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "accessory_type")
@Getter
@EqualsAndHashCode(exclude = { "aquarium" })
@ToString(exclude = { "aquarium" })
@NoArgsConstructor
@AllArgsConstructor
public abstract class Accessory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 1, max = 50)
  private String model;

  @NotNull
  @Size(min = 1, max = 50)
  private String serialNumber;

  @NotNull
  @Column(name = "owner_id")
  private Long ownerId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aquarium_id")
  private Aquarium aquarium;

  @Column(name = "color")
  private String color;

  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "date_created", updatable = false)
  private java.time.LocalDateTime dateCreated;

  public Accessory(String model, String serialNumber, Long ownerId) {
    this.model = Validator.notEmpty(model, "Accessory model");
    this.serialNumber = Validator.notEmpty(serialNumber, "Accessory serial number");
    this.ownerId = Validator.notNull(ownerId, "Owner ID");
  }

  void setAquarium(Aquarium aquarium) {
    this.aquarium = aquarium;
  }

  public void updateModel(String model) {
    this.model = Validator.notEmpty(model, "Accessory model");
  }

  public void updateSerialNumber(String serialNumber) {
    this.serialNumber = Validator.notEmpty(serialNumber, "Accessory serial number");
  }

  public void updateProperties(String model, String serialNumber) {
    if (model != null) {
      updateModel(model);
    }

    if (serialNumber != null) {
      updateSerialNumber(serialNumber);
    }
  }

  public void updateColor(String color) {
    this.color = color;
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  public Accessory update(String model, String serialNumber, String color, String description) {
    if (model != null) {
      updateModel(model);
    }

    if (serialNumber != null) {
      updateSerialNumber(serialNumber);
    }
    
    if (color != null) {
      updateColor(color);
    }
    
    if (description != null) {
      updateDescription(description);
    }

    return this;
  }

  public static Accessory createFromType(String type, String model, String serialNumber,
      boolean isExternal, int capacityLiters,
      boolean isLED, LocalTime timeOn, LocalTime timeOff,
      double minTemperature, double maxTemperature, double currentTemperature,
      Long ownerId, String color, String description) {

    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Accessory type is required");
    }

    Accessory accessory;

    switch (type.toLowerCase()) {
      case "filter":
        accessory = new Filter(model, serialNumber, isExternal, capacityLiters, ownerId);
        break;
      case "light":
        accessory = new Lighting(model, serialNumber, isLED, timeOff, timeOn, ownerId);
        break;
      case "heater":
        accessory = new Thermostat(model, serialNumber, minTemperature, maxTemperature, currentTemperature, ownerId);
        break;
      default:
        throw new IllegalArgumentException("Unsupported accessory type: " + type);
    }

    accessory.color = color;
    accessory.description = description;
    accessory.dateCreated = java.time.LocalDateTime.now();
    return accessory;
  }
}
