package nl.hu.bep.domain;

import lombok.*;
import nl.hu.bep.domain.base.AssignableEntity;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.domain.accessories.Filter;
import nl.hu.bep.domain.accessories.Lighting;
import nl.hu.bep.domain.accessories.Thermostat;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Abstract base class for all aquarium accessories.

 */
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
  private Long aquariumId; // ID-based relationship
  private String color;
  private String description;
  private LocalDateTime dateCreated;

  // Protected constructor for subclasses
  protected Accessory(String model, String serialNumber, Long ownerId) {
    this.model = Validator.notEmpty(model, "Accessory model");
    this.serialNumber = Validator.notEmpty(serialNumber, "Accessory serial number");
    this.ownerId = Validator.notNull(ownerId, "Owner ID");
    this.dateCreated = LocalDateTime.now();
  }

  // Business methods
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

  // Comprehensive update method
  public Accessory update(String model, String serialNumber, String color, String description) {
    if (model != null) updateModel(model);
    if (serialNumber != null) updateSerialNumber(serialNumber);
    if (color != null) updateColor(color);
    if (description != null) updateDescription(description);
    return this;
  }

  // Secure aquarium assignment methods with domain validation
  public void assignToAquarium(Long aquariumId, Long requestingOwnerId) {
      // Domain security: Only owner can assign accessory to aquarium
      if (!this.ownerId.equals(requestingOwnerId)) {
          throw new IllegalArgumentException("Only the accessory owner can assign it to an aquarium");
      }
      // Direct assignment with validation passed
      this.aquariumId = aquariumId;
  }

  public void removeFromAquarium(Long requestingOwnerId) {
      // Domain security: Only owner can remove accessory from aquarium
      if (!this.ownerId.equals(requestingOwnerId)) {
          throw new IllegalArgumentException("Only the accessory owner can remove it from an aquarium");
      }
      // Direct removal with validation passed
      this.aquariumId = null;
  }

  // Abstract method for accessory type
  public abstract String getAccessoryType();

  // Public method for repository reconstruction only
  public static Accessory reconstruct(String type, Long id, String model, String serialNumber, 
                                     Long ownerId, Long aquariumId, String color, String description,
                                     LocalDateTime dateCreated, boolean isExternal, int capacityLiters,
                                     boolean isLED, LocalTime timeOn, LocalTime timeOff,
                                     double minTemperature, double maxTemperature, double currentTemperature) {
    
    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Accessory type is required for reconstruction");
    }

    Accessory accessory = switch (type.toLowerCase()) {
      case "filter" -> new Filter(model, serialNumber, isExternal, capacityLiters, ownerId);
      case "light", "lighting" -> new Lighting(model, serialNumber, isLED, timeOff, timeOn, ownerId);
      case "heater", "thermostat" -> new Thermostat(model, serialNumber, minTemperature, maxTemperature, currentTemperature, ownerId);
      default -> throw new IllegalArgumentException("Unsupported accessory type: " + type);
    };

    // Set reconstructed properties
    accessory.id = id;
    accessory.aquariumId = aquariumId;
    accessory.color = color;
    accessory.description = description;
    accessory.dateCreated = dateCreated;
    
    return accessory;
  }

  // Factory method for creating accessories from type
  public static Accessory createFromType(String type, String model, String serialNumber,
      boolean isExternal, int capacityLiters,
      boolean isLED, LocalTime timeOn, LocalTime timeOff,
      double minTemperature, double maxTemperature, double currentTemperature,
      Long ownerId, String color, String description) {

    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Accessory type is required");
    }

    Accessory accessory = switch (type.toLowerCase()) {
      case "filter" -> new Filter(model, serialNumber, isExternal, capacityLiters, ownerId);
      case "light", "lighting" -> new Lighting(model, serialNumber, isLED, timeOff, timeOn, ownerId);
      case "heater", "thermostat" -> new Thermostat(model, serialNumber, minTemperature, maxTemperature, currentTemperature, ownerId);
      default -> throw new IllegalArgumentException("Unsupported accessory type: " + type);
    };

    accessory.color = color;
    accessory.description = description;
    accessory.dateCreated = LocalDateTime.now();
    return accessory;
  }
}
