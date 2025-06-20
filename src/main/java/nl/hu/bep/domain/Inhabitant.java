package nl.hu.bep.domain;

import nl.hu.bep.domain.base.AssignableEntity;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.utils.Validator;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import nl.hu.bep.domain.species.Fish;
import nl.hu.bep.domain.species.Snail;
import nl.hu.bep.domain.species.Shrimp;
import nl.hu.bep.domain.species.Crayfish;
import nl.hu.bep.domain.species.Plant;
import nl.hu.bep.domain.species.Coral;

@Entity
@Table(name = "inhabitants")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "inhabitant_type")
@Getter
@Setter(AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = { "aquarium", "aquariumManager" }, callSuper = false)
@ToString(exclude = { "aquarium", "aquariumManager" })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Inhabitant extends AssignableEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "species")
  private String species;

  @Column(name = "color", nullable = true)
  private String color;

  @NotNull
  @Positive
  @Min(1)
  @Column(name = "count")
  private int count;

  @NotNull
  @Column(name = "is_schooling")
  private boolean isSchooling;

  @NotNull
  @Column(name = "water_type")
  @Enumerated(EnumType.STRING)
  private WaterType waterType;

  @NotNull
  @Column(name = "owner_id")
  private Long ownerId;

  @Column(name = "name", nullable = true)
  @Size(max = 100)
  private String name;

  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "date_created", updatable = false)
  private java.time.LocalDateTime dateCreated;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aquarium_id")
  private Aquarium aquarium;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aquarium_manager_id")
  private AquariumManager aquariumManager;

  // Constructor for species subclasses - POJO pattern for JDBC
  protected Inhabitant(String species, String color, int count, boolean isSchooling,
                      WaterType waterType, Long ownerId, String name, String description) {
    this.species = Validator.notEmpty(species, "Species");
    this.color = color;
    this.count = Validator.positive(count, "Count");
    this.waterType = Validator.notNull(waterType, "Water type");
    this.ownerId = Validator.notNull(ownerId, "Owner ID");
    this.isSchooling = isSchooling;
    this.name = name;
    this.description = description;
    this.dateCreated = java.time.LocalDateTime.now();
  }

  protected void initializeInhabitant(String species, String color, int count, boolean isSchooling,
      WaterType waterType) {
    this.species = Validator.notEmpty(species, "Species");
    this.color = color;
    this.count = Validator.positive(count, "Count");
    this.waterType = Validator.notNull(waterType, "Water type");
    this.isSchooling = isSchooling;
  }

  public String getType() {
    return this.getClass().getSimpleName();
  }

  public void updateSpecies(String species) {
    this.species = Validator.notEmpty(species, "Species");
  }

  public void updateColor(String color) {
    this.color = color;
  }

  public void updateCount(int count) {
    this.count = Validator.positive(count, "Count");
  }

  public void updateSchooling(boolean isSchooling) {
    this.isSchooling = isSchooling;
  }

  public void updateWaterType(WaterType waterType) {
    this.waterType = Validator.notNull(waterType, "Water type");
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  public Inhabitant update(String species, String color, Integer count, Boolean isSchooling, WaterType waterType, String name, String description) {
    if (species != null) {
      updateSpecies(species);
    }

    if (color != null) {
      updateColor(color);
    }

    if (count != null) {
      updateCount(count);
    }

    if (isSchooling != null) {
      updateSchooling(isSchooling);
    }

    if (waterType != null) {
      updateWaterType(waterType);
    }

    if (name != null) {
      updateName(name);
    }

    if (description != null) {
      updateDescription(description);
    }

    return this;
  }

  // Package-private method for aquarium bidirectional relationship management
  void setAquarium(Aquarium aquarium) {
    if (this.aquarium == aquarium) {
      return;
    }

    if (this.aquarium != null && this.aquarium.getInhabitants().contains(this)) {
      this.aquarium.getInhabitants().remove(this);
    }

    this.aquarium = aquarium;

    if (aquarium != null && !aquarium.getInhabitants().contains(this)) {
      aquarium.getInhabitants().add(this);
    }
  }

  void setAquariumManager(AquariumManager aquariumManager) {
    this.aquariumManager = aquariumManager;
  }

  // Override getAquariumId() to use the aquarium relationship
  @Override
  public Long getAquariumId() {
    if (aquarium != null) {
      return aquarium.getId();
    }
    return super.getAquariumId(); // Fallback to parent implementation
  }

  // Secure aquarium assignment methods with domain validation
  public void assignToAquarium(Long aquariumId, Long requestingOwnerId) {
    // Domain security: Only owner can assign inhabitant to aquarium
    if (!this.ownerId.equals(requestingOwnerId)) {
      throw new IllegalArgumentException("Only the inhabitant owner can assign it to an aquarium");
    }
    super.assignToAquarium(aquariumId);
  }

  public void removeFromAquarium(Long requestingOwnerId) {
    // Domain security: Only owner can remove inhabitant from aquarium
    if (!this.ownerId.equals(requestingOwnerId)) {
      throw new IllegalArgumentException("Only the inhabitant owner can remove it from an aquarium");
    }
    super.removeFromAquarium();
  }

  public static Inhabitant createFromType(String type, String species, String color, int count,
      boolean isSchooling, WaterType waterType,
      boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater,
      Long ownerId, String name, String description) {

    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Inhabitant type is required");
    }
    
    Inhabitant inhabitant;

    switch (type.toLowerCase()) {
      case "fish":
        inhabitant = Fish.create(species, color, count, isSchooling, isAggressiveEater,
            requiresSpecialFood, waterType, isSnailEater, ownerId, name, description);
        break;
      case "snail":
        inhabitant = Snail.create(species, color, count, isSchooling, isSnailEater, waterType, ownerId, name, description);
        break;
      case "shrimp":
        inhabitant = Shrimp.create(species, color, count, isSchooling, waterType, ownerId, name, description);
        break;
      case "crayfish":
        inhabitant = Crayfish.create(species, color, count, isSchooling, waterType, ownerId, name, description);
        break;
      case "plant":
        inhabitant = Plant.create(species, color, count, isSchooling, waterType, ownerId, name, description);
        break;
      case "coral":
        inhabitant = Coral.create(species, color, count, isSchooling, waterType, ownerId, name, description);
        break;
      default:
        throw new IllegalArgumentException("Unsupported inhabitant type: " + type);
    }
    
    inhabitant.setOwnerIdInternal(ownerId);
    if (name != null) {
        inhabitant.setNameInternal(name);
    }
    inhabitant.color = color;
    inhabitant.description = description;
    inhabitant.dateCreated = java.time.LocalDateTime.now();
    return inhabitant;
  }
  
  protected void setOwnerIdInternal(Long ownerId) {
      this.ownerId = Validator.notNull(ownerId, "Owner ID");
  }
  
  protected void setNameInternal(String name) {
      updateName(name);
  }

  // Native domain ownership validation - DDD compliant
  public void validateOwnership(Long requestingOwnerId) {
    if (requestingOwnerId == null) {
      throw new IllegalArgumentException("Owner ID is required for validation");
    }
    
    if (!this.ownerId.equals(requestingOwnerId)) {
      throw new IllegalArgumentException("Access denied: You do not own this inhabitant");
    }
  }
}
