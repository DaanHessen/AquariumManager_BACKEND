package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.domain.DomainException;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.domain.value.Dimensions;
import nl.hu.bep.domain.species.Fish;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.function.BiConsumer;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "aquariums")
@Getter
@EqualsAndHashCode(exclude = { "accessories", "inhabitants", "ornaments", "aquariumManager", "owner", "stateHistory" })
@ToString(exclude = { "accessories", "inhabitants", "ornaments", "aquariumManager", "owner", "stateHistory" })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
public class Aquarium {
  private static final Logger log = LoggerFactory.getLogger(Aquarium.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 1, max = 50)
  private String name;

  @Embedded
  private Dimensions dimensions;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SubstrateType substrate;

  @NotNull
  @Enumerated(EnumType.STRING)
  private WaterType waterType;

  @PositiveOrZero
  @Column(name = "temperature")
  private Double temperature;

  @NotNull
  @Enumerated(EnumType.STRING)
  private AquariumState state;

  @Column(name = "current_state_start_time")
  private LocalDateTime currentStateStartTime;

  @Column(name = "color")
  private String color;

  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "date_created", updatable = false)
  private LocalDateTime dateCreated;

  @OneToMany(mappedBy = "aquarium", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Accessory> accessories = new HashSet<>();

  @OneToMany(mappedBy = "aquarium", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Inhabitant> inhabitants = new HashSet<>();

  @OneToMany(mappedBy = "aquarium", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Ornament> ornaments = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aquarium_manager_id")
  private AquariumManager aquariumManager;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private Owner owner;

  private Long ownerId;
  private Long aquariumManagerId;

  public static Aquarium create(String name, double length, double width, double height,
      SubstrateType substrate, WaterType waterType, String color, String description, AquariumState state) {
    Validator.notEmpty(name, "Aquarium name");
    Validator.notNull(substrate, "Substrate type");
    Validator.notNull(waterType, "Water type");

    Aquarium aquarium = new Aquarium();
    aquarium.name = name;
    aquarium.dimensions = new Dimensions(length, width, height);
    aquarium.substrate = substrate;
    aquarium.waterType = waterType;
    aquarium.temperature = 24.0;
    aquarium.accessories = new HashSet<>();
    aquarium.inhabitants = new HashSet<>();
    aquarium.ornaments = new HashSet<>();
    aquarium.state = state != null ? state : AquariumState.SETUP;
    aquarium.currentStateStartTime = LocalDateTime.now();
    aquarium.color = color;
    aquarium.description = description;
    aquarium.dateCreated = LocalDateTime.now();

    return aquarium;
  }

  // Repository reconstruction method for JDBC mapping
  public static Aquarium reconstruct(Long id, String name, Dimensions dimensions, 
                                   SubstrateType substrate, WaterType waterType, Double temperature,
                                   AquariumState state, LocalDateTime currentStateStartTime,
                                   String color, String description, LocalDateTime dateCreated,
                                   Long aquariumManagerId, Long ownerId,
                                   Set<Accessory> accessories, Set<Inhabitant> inhabitants, Set<Ornament> ornaments) {
    Aquarium aquarium = new Aquarium();
    aquarium.id = id;
    aquarium.name = name;
    aquarium.dimensions = dimensions;
    aquarium.substrate = substrate;
    aquarium.waterType = waterType;
    aquarium.temperature = temperature;
    aquarium.state = state;
    aquarium.currentStateStartTime = currentStateStartTime;
    aquarium.color = color;
    aquarium.description = description;
    aquarium.dateCreated = dateCreated;
    aquarium.aquariumManagerId = aquariumManagerId;
    aquarium.ownerId = ownerId;
    aquarium.accessories = accessories != null ? accessories : new HashSet<>();
    aquarium.inhabitants = inhabitants != null ? inhabitants : new HashSet<>();
    aquarium.ornaments = ornaments != null ? ornaments : new HashSet<>();
    return aquarium;
  }

  public void updateName(String name) {
    this.name = Validator.notEmpty(name, "Aquarium name");
  }

  public void updateDimensions(Double length, Double width, Double height) {
    Validator.notNull(length, "Length");
    Validator.notNull(width, "Width");
    Validator.notNull(height, "Height");
    this.dimensions = new Dimensions(length, width, height);
  }

  public void updateSubstrate(SubstrateType substrate) {
    this.substrate = Validator.notNull(substrate, "Substrate type");
  }

  public void updateWaterType(WaterType waterType) {
    this.waterType = Validator.notNull(waterType, "Water type");
  }

  public void updateTemperature(Double temperature) {
    if (temperature != null && temperature < 0) {
      throw new IllegalArgumentException("Temperature cannot be negative");
    }
    this.temperature = temperature;
  }

  public void updateState(AquariumState newState) {
    AquariumState oldState = this.state;
    this.state = Validator.notNull(newState, "Aquarium state");
    
    // Only create history if state actually changed
    if (oldState != newState) {
      transitionToState(newState);
    }
  }

  public void activateAquarium() {
    if (this.state != AquariumState.SETUP && this.state != AquariumState.MAINTENANCE) {
      throw new DomainException("Cannot activate aquarium from " + this.state + " state");
    }
    transitionToState(AquariumState.RUNNING);
  }

  public void startMaintenance() {
    if (this.state != AquariumState.RUNNING) {
      throw new DomainException("Cannot start maintenance when aquarium is not running");
    }
    transitionToState(AquariumState.MAINTENANCE);
  }

  public void deactivateAquarium() {
    transitionToState(AquariumState.INACTIVE);
  }

  /**
   * Handles state transition with proper history tracking
   */
  private void transitionToState(AquariumState newState) {
    if (this.state == newState) {
      return; // No change needed
    } 

    // Update current state and timestamp
    this.state = newState;
    this.currentStateStartTime = LocalDateTime.now();
  }
  
  /**
   * Handles state transition with frontend-provided duration for the previous state
   */
  public void transitionToStateWithDuration(AquariumState newState, Long previousStateDurationMinutes) {
    if (this.state == newState) {
      return; // No change needed
    }

    // Update current state and timestamp
    this.state = newState;
    this.currentStateStartTime = LocalDateTime.now();
  }

  /**
   * Gets the current state duration in minutes
   */
  public long getCurrentStateDurationMinutes() {
    if (currentStateStartTime == null) {
      return 0;
    }
    return java.time.Duration.between(currentStateStartTime, LocalDateTime.now()).toMinutes();
  }

  public void addToInhabitants(Inhabitant inhabitant) {
    if (inhabitant == null) {
      throw new IllegalArgumentException("Inhabitant cannot be null");
    }

    // Validate compatibility before adding
    validateInhabitantCompatibility(inhabitant);

    // Add to collection with proper bidirectional relationship
    addToCollection(inhabitants, inhabitant, this::setInhabitantAquarium);
  }

  private void validateInhabitantCompatibility(Inhabitant inhabitant) {
    // Water type compatibility check
    if (inhabitant.getWaterType() != this.waterType) {
      throw new DomainException("Inhabitant water type (" + inhabitant.getWaterType() + 
          ") is not compatible with aquarium water type (" + this.waterType + ")");
    }
  }

  private void setInhabitantAquarium(Inhabitant inhabitant, Aquarium aquarium) {
    inhabitant.setAquarium(aquarium);
  }

  private void setOrnamentAquarium(Ornament ornament, Aquarium aquarium) {
    ornament.setAquarium(aquarium);
  }

  private void setAccessoryAquarium(Accessory accessory, Aquarium aquarium) {
    // For JDBC-based entities, we set the aquarium ID
    // This method will be implemented in the Accessory class
  }

  private <T> void addToCollection(Set<T> collection, T item, BiConsumer<T, Aquarium> setter) {
    if (!collection.contains(item)) {
      collection.add(item);
      setter.accept(item, this);
    }
  }

  private <T> void removeFromCollection(Set<T> collection, T item, BiConsumer<T, Aquarium> setter) {
    if (collection.remove(item)) {
      setter.accept(item, null);
    }
  }

  public void addToAccessories(Accessory accessory) {
    if (accessory == null) {
      throw new IllegalArgumentException("Accessory cannot be null");
    }
    
    // For JDBC-based entities, we handle the relationship differently
    accessories.add(accessory);
  }

  public void removeFromAccessories(Accessory accessory) {
    accessories.remove(accessory);
  }

  public void removeFromInhabitants(Inhabitant inhabitant) {
    removeFromCollection(inhabitants, inhabitant, this::setInhabitantAquarium);
  }

  public void addToOrnaments(Ornament ornament) {
    if (ornament == null) {
      throw new IllegalArgumentException("Ornament cannot be null");
    }
    
    addToCollection(ornaments, ornament, this::setOrnamentAquarium);
  }

  public void removeFromOrnaments(Ornament ornament) {
    removeFromCollection(ornaments, ornament, this::setOrnamentAquarium);
  }

  public void assignToOwner(Owner owner) {
    if (owner == null) {
      throw new IllegalArgumentException("Owner cannot be null");
    }

    // Remove from current owner if exists
    if (this.owner != null) {
      this.owner.unregisterAquarium(this.id);
    }

    // Assign to new owner
    this.owner = owner;
    this.ownerId = owner.getId();
    owner.addToAquariums(this);
  }

  /**
   * Safe assignment that checks if owner exists and handles the relationship properly
   */
  public void assignToOwnerSafely(Owner owner) {
    if (owner != null) {
      assignToOwner(owner);
    }
  }

  public void unassignFromOwner() {
    if (this.owner != null) {
      this.owner.unregisterAquarium(this.id);
      this.owner = null;
      this.ownerId = null;
    }
  }

  public void assignToManager(AquariumManager manager) {
    if (manager == null) {
      throw new IllegalArgumentException("Manager cannot be null");
    }

    // Remove from current manager if exists
    if (this.aquariumManager != null) {
      // This would need to be implemented in AquariumManager
    }

    // Assign to new manager
    this.aquariumManager = manager;
    this.aquariumManagerId = manager.getId();
  }

  public void unassignFromManager() {
    if (this.aquariumManager != null) {
      // Remove from current manager's collection
      this.aquariumManager = null;
      this.aquariumManagerId = null;
    }
  }

  public boolean isOwnedBy(Long ownerId) {
    return this.ownerId != null && this.ownerId.equals(ownerId);
  }

  public Long getOwnerId() {
    return this.ownerId != null ? this.ownerId : (this.owner != null ? this.owner.getId() : null);
  }

  public Long getAquariumManagerId() {
    return this.aquariumManagerId;
  }

  public double getVolume() {
    return dimensions.getVolume();
  }

  public Set<Inhabitant> getInhabitantsByWaterType(WaterType waterType) {
    return inhabitants.stream()
        .filter(i -> i.getWaterType() == waterType)
        .collect(Collectors.toSet());
  }

  public Set<Inhabitant> getSchoolingInhabitants() {
    return inhabitants.stream()
        .filter(Inhabitant::isSchooling)
        .collect(Collectors.toSet());
  }

  public void transferOrnament(Ornament ornament, Aquarium targetAquarium) {
    if (ornament == null || targetAquarium == null) {
      throw new IllegalArgumentException("Ornament and target aquarium cannot be null");
    }

    if (!this.ornaments.contains(ornament)) {
      throw new DomainException("Ornament does not belong to this aquarium");
    }

    this.removeFromOrnaments(ornament);
    targetAquarium.addToOrnaments(ornament);
  }

  public Ornament createAndAddOrnament(String name, String description, String color, boolean isAirPumpCompatible) {
    Ornament ornament = Ornament.create(name, description, color, isAirPumpCompatible, this.getOwnerId());
    this.addToOrnaments(ornament);
    return ornament;
  }

  public void transferAccessory(Accessory accessory, Aquarium targetAquarium) {
    if (accessory == null || targetAquarium == null) {
      throw new IllegalArgumentException("Accessory and target aquarium cannot be null");
    }

    if (!this.accessories.contains(accessory)) {
      throw new DomainException("Accessory does not belong to this aquarium");
    }

    this.removeFromAccessories(accessory);
    targetAquarium.addToAccessories(accessory);
  }

  public void transferInhabitant(Inhabitant inhabitant, Aquarium targetAquarium) {
    if (inhabitant == null || targetAquarium == null) {
      throw new IllegalArgumentException("Inhabitant and target aquarium cannot be null");
    }

    if (!this.inhabitants.contains(inhabitant)) {
      throw new DomainException("Inhabitant does not belong to this aquarium");
    }

    this.removeFromInhabitants(inhabitant);
    targetAquarium.addToInhabitants(inhabitant);
  }

  public Fish createAndAddFish(String species, String color, int count, boolean isSchooling,
      boolean isAggressiveEater, boolean requiresSpecialFood, WaterType waterType, boolean isSnailEater, String description) {
    
    Fish fish = Fish.create(species, color, count, isSchooling, isAggressiveEater, 
        requiresSpecialFood, waterType, isSnailEater, this.getOwnerId(), null, description);
    this.addToInhabitants(fish);
    return fish;
  }

  // Native domain ownership validation - DDD compliant
  public void verifyOwnership(Long requestingOwnerId) {
    if (requestingOwnerId == null) {
      throw new DomainException("Owner ID is required for ownership verification");
    }
    
    if (!isOwnedBy(requestingOwnerId)) {
      throw new DomainException("Access denied: You do not own this aquarium");
    }
  }

  public void addOrnamentWithOwnershipCheck(Ornament ornament, Long ownerId) {
    verifyOwnership(ownerId);
    addToOrnaments(ornament);
  }

  public void addAccessoryWithOwnershipCheck(Accessory accessory, Long ownerId) {
    verifyOwnership(ownerId);
    addToAccessories(accessory);
  }

  public void addInhabitantWithOwnershipCheck(Inhabitant inhabitant, Long ownerId) {
    verifyOwnership(ownerId);
    addToInhabitants(inhabitant);
  }

  public Aquarium update(String name, Double length, Double width, Double height,
      SubstrateType substrate, WaterType waterType, AquariumState state,
      Double temperature) {
    
    if (name != null) updateName(name);
    if (length != null && width != null && height != null) updateDimensions(length, width, height);
    if (substrate != null) updateSubstrate(substrate);
    if (waterType != null) updateWaterType(waterType);
    if (state != null) updateState(state);
    if (temperature != null) updateTemperature(temperature);
    
    return this;
  }
}
