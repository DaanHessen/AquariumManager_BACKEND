package nl.hu.bep.domain;

import nl.hu.bep.domain.base.AssignableEntity;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.exception.ApplicationException.BusinessRuleException;
import nl.hu.bep.domain.accessories.Filter;
import nl.hu.bep.domain.accessories.Lighting;
import nl.hu.bep.domain.accessories.Thermostat;

import lombok.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"aquariumId"})
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Accessory extends AssignableEntity {
  private Long id;
  private String model;
  private String serialNumber;
  private Long ownerId;
  private Long aquariumId;
  private String color;
  private String description;
  private LocalDateTime dateCreated;

  protected Accessory(String model, String serialNumber, Long ownerId) {
    this.model = Validator.notEmpty(model, "Accessory model");
    this.serialNumber = Validator.notEmpty(serialNumber, "Accessory serial number");
    this.ownerId = Validator.notNull(ownerId, "Owner ID");
    this.dateCreated = LocalDateTime.now();
  }

  public void updateModel(String model) {
    this.model = Validator.notEmpty(model, "Accessory model");
  }

  public void updateSerialNumber(String serialNumber) {
    this.serialNumber = Validator.notEmpty(serialNumber, "Accessory serial number");
  }

  public void updateColor(String color) {
    this.color = color;
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  public Accessory update(String model, String serialNumber, String color, String description) {
    if (model != null) updateModel(model);
    if (serialNumber != null) updateSerialNumber(serialNumber);
    if (color != null) updateColor(color);
    if (description != null) updateDescription(description);
    return this;
  }

  public void assignToAquarium(Long aquariumId, Long requestingOwnerId) {
      if (!this.ownerId.equals(requestingOwnerId)) {
          throw new BusinessRuleException("Only the accessory owner can assign it to an aquarium");
      }
      this.aquariumId = aquariumId;
  }

  public void removeFromAquarium(Long requestingOwnerId) {
      if (!this.ownerId.equals(requestingOwnerId)) {
          throw new BusinessRuleException("Only the accessory owner can remove it from an aquarium");
      }
      this.aquariumId = null;
  }

  public void validateOwnership(Long requestingOwnerId) {
      if (requestingOwnerId == null) {
          throw new BusinessRuleException("Requesting Owner ID cannot be null");
      }
      if (this.ownerId == null || !this.ownerId.equals(requestingOwnerId)) {
          throw new BusinessRuleException("Entity does not belong to the current user");
      }
  }

  public abstract String getAccessoryType();

  public abstract boolean isExternal();
  public abstract int getCapacityLiters();
  public abstract boolean isLed();
  public abstract LocalTime getTurnOnTime();
  public abstract LocalTime getTurnOffTime();
  public abstract double getMinTemperature();
  public abstract double getMaxTemperature();
  public abstract double getCurrentTemperature();

  public static Accessory reconstruct(String type, Long id, String model, String serialNumber, 
                                     Long ownerId, Long aquariumId, String color, String description,
                                     LocalDateTime dateCreated, boolean isExternal, int capacityLiters,
                                     boolean isLED, LocalTime timeOn, LocalTime timeOff,
                                     double minTemperature, double maxTemperature, double currentTemperature) {
    
    if (type == null || type.isEmpty()) {
      throw new BusinessRuleException("Accessory type is required for reconstruction");
    }

    Accessory accessory = switch (type.toLowerCase()) {
      case "filter" -> new Filter(model, serialNumber, isExternal, capacityLiters, ownerId);
      case "light", "lighting" -> new Lighting(model, serialNumber, isLED, timeOff, timeOn, ownerId);
      case "heater", "thermostat" -> new Thermostat(model, serialNumber, minTemperature, maxTemperature, currentTemperature, ownerId);
      default -> throw new BusinessRuleException("Unsupported accessory type: " + type);
    };

    accessory.id = id;
    accessory.aquariumId = aquariumId;
    accessory.color = color;
    accessory.description = description;
    accessory.dateCreated = dateCreated;
    
    return accessory;
  }

  public static Accessory createFromType(String type, String model, String serialNumber,
      boolean isExternal, int capacityLiters,
      boolean isLED, LocalTime timeOn, LocalTime timeOff,
      double minTemperature, double maxTemperature, double currentTemperature,
      Long ownerId, String color, String description) {

    if (type == null || type.isEmpty()) {
      throw new BusinessRuleException("Accessory type is required");
    }

    Accessory accessory = switch (type.toLowerCase()) {
      case "filter" -> {
        if (capacityLiters <= 0) {
          throw new BusinessRuleException("Filter capacity must be provided and positive");
        }
        yield new Filter(model, serialNumber, isExternal, capacityLiters, ownerId);
      }
      case "light", "lighting" -> new Lighting(model, serialNumber, isLED, timeOff, timeOn, ownerId);
      case "heater", "thermostat" -> {
        if (minTemperature <= 0) {
          throw new BusinessRuleException("Minimum temperature must be positive");
        }
        if (maxTemperature <= 0) {
          throw new BusinessRuleException("Maximum temperature must be positive");
        }
        if (minTemperature >= maxTemperature) {
          throw new BusinessRuleException("Minimum temperature must be less than maximum temperature");
        }
        yield new Thermostat(model, serialNumber, minTemperature, maxTemperature, currentTemperature, ownerId);
      }
      default -> throw new BusinessRuleException("Unsupported accessory type: " + type);
    };

    accessory.color = color;
    accessory.description = description;
    accessory.dateCreated = LocalDateTime.now();
    return accessory;
  }
}
