package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.exception.DomainException;
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

  @OneToMany(mappedBy = "aquarium", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<AquariumStateHistory> stateHistory = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aquarium_manager_id")
  private AquariumManager aquariumManager;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private Owner owner;

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
    aquarium.stateHistory = new HashSet<>();
    aquarium.state = state != null ? state : AquariumState.SETUP;
    aquarium.currentStateStartTime = LocalDateTime.now();
    aquarium.color = color;
    aquarium.description = description;
    aquarium.dateCreated = LocalDateTime.now();

    // Create initial state history record
    AquariumStateHistory initialHistory = AquariumStateHistory.createActive(aquarium, aquarium.state);
    aquarium.stateHistory.add(initialHistory);

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

    // End current state history record
    AquariumStateHistory currentHistory = getCurrentActiveStateHistory();
    if (currentHistory != null) {
      currentHistory.endState();
    }

    // Update current state and timestamp
    this.state = newState;
    this.currentStateStartTime = LocalDateTime.now();

    // Create new state history record
    AquariumStateHistory newHistory = AquariumStateHistory.create(this, newState);
    this.stateHistory.add(newHistory);
  }
  
  /**
   * Handles state transition with frontend-provided duration for the previous state
   */
  public void transitionToStateWithDuration(AquariumState newState, Long previousStateDurationMinutes) {
    if (this.state == newState) {
      return; // No change needed
    }

    // End current active state history record with frontend-provided duration
    AquariumStateHistory currentHistory = getCurrentActiveStateHistory();
    if (currentHistory != null) {
      // Instead of removing and creating new, update the existing record with frontend duration
      currentHistory.setDurationMinutes(previousStateDurationMinutes);
      currentHistory.setEndTime(LocalDateTime.now());
    }

    // Update current state and timestamp
    this.state = newState;
    this.currentStateStartTime = LocalDateTime.now();

    // Create new active state history record for the new state
    AquariumStateHistory newHistory = AquariumStateHistory.createActive(this, newState);
    this.stateHistory.add(newHistory);
  }

  /**
   * Gets the currently active state history record
   */
  private AquariumStateHistory getCurrentActiveStateHistory() {
    return stateHistory.stream()
        .filter(AquariumStateHistory::isActive)
        .findFirst()
        .orElse(null);
  }

  /**
   * Gets the current state duration in minutes
   */
  public long getCurrentStateDurationMinutes() {
    if (currentStateStartTime == null) {
      return 0;
    }
    return java.time.temporal.ChronoUnit.MINUTES.between(currentStateStartTime, LocalDateTime.now());
  }

  /**
   * Gets all state history for this aquarium
   */
  public Set<AquariumStateHistory> getStateHistory() {
    return new HashSet<>(stateHistory);
  }

  public void addToInhabitants(Inhabitant inhabitant) {
    Validator.notNull(inhabitant, "Inhabitant");

    if (this.state == AquariumState.INACTIVE) {
      throw new DomainException("Cannot add inhabitants to an inactive aquarium");
    }

    validateInhabitantCompatibility(inhabitant);

    if (inhabitant.getAquarium() != null && inhabitant.getAquarium() != this) {
      inhabitant.getAquarium().getInhabitants().remove(inhabitant);
    }

    addToCollection(inhabitants, inhabitant, this::setInhabitantAquarium);
  }

  private void validateInhabitantCompatibility(Inhabitant inhabitant) {
    if (inhabitant.getWaterType() != this.waterType) {
      throw new DomainException.IncompatibleWaterTypeException(
          "Incompatible water types: Aquarium has " + this.waterType +
              " but inhabitant requires " + inhabitant.getWaterType());
    }
  }

  private void setInhabitantAquarium(Inhabitant inhabitant, Aquarium aquarium) {
    inhabitant.setAquarium(aquarium);
  }

  private void setOrnamentAquarium(Ornament ornament, Aquarium aquarium) {
    ornament.setAquarium(aquarium);
  }

  private void setAccessoryAquarium(Accessory accessory, Aquarium aquarium) {
    accessory.setAquarium(aquarium);
  }

  private <T> void addToCollection(Set<T> collection, T item, BiConsumer<T, Aquarium> setter) {
    if (item == null || collection.contains(item)) {
      return;
    }

    collection.add(item);
    setter.accept(item, this);
  }

  private <T> void removeFromCollection(Set<T> collection, T item, BiConsumer<T, Aquarium> setter) {
    if (item == null || !collection.contains(item)) {
      return;
    }

    collection.remove(item);
    setter.accept(item, null);
  }

  public void addToAccessories(Accessory accessory) {
    Validator.notNull(accessory, "Accessory");

    if (accessories.contains(accessory)) {
      throw new DomainException("Accessory is already in this aquarium");
    }

    addToCollection(accessories, accessory, this::setAccessoryAquarium);
  }

  public void removeFromAccessories(Accessory accessory) {
    Validator.notNull(accessory, "Accessory");
    removeFromCollection(accessories, accessory, this::setAccessoryAquarium);
  }

  public void removeFromInhabitants(Inhabitant inhabitant) {
    Validator.notNull(inhabitant, "Inhabitant");
    removeFromCollection(inhabitants, inhabitant, this::setInhabitantAquarium);
  }

  public void addToOrnaments(Ornament ornament) {
    Validator.notNull(ornament, "Ornament");

    if (ornaments.contains(ornament)) {
      throw new DomainException("Ornament is already in this aquarium");
    }

    addToCollection(ornaments, ornament, this::setOrnamentAquarium);
  }

  public void removeFromOrnaments(Ornament ornament) {
    Validator.notNull(ornament, "Ornament");
    removeFromCollection(ornaments, ornament, this::setOrnamentAquarium);
  }

  public void assignToOwner(Owner owner) {
    Validator.notNull(owner, "Owner");

    unassignFromOwner();

    this.owner = owner;
    // Only try to update owner's collection if it's already initialized (not a proxy)
    // This prevents lazy loading issues during entity creation
    try {
      if (!owner.getOwnedAquariums().contains(this)) {
        owner.addToAquariums(this);
      }
    } catch (Exception e) {
      // If accessing the collection fails (e.g., lazy loading issue),
      // the bidirectional relationship will be handled at the persistence layer
      // or when the collection is properly initialized
      log.debug("Could not update owner's aquarium collection during assignment: {}", e.getMessage());
    }
  }

  /**
   * Assigns this aquarium to an owner without trying to update the owner's collection.
   * This is safer to use during entity creation when lazy collections may not be initialized.
   * The bidirectional relationship should be handled at the persistence/service layer.
   */
  public void assignToOwnerSafely(Owner owner) {
    Validator.notNull(owner, "Owner");
    
    unassignFromOwner();
    this.owner = owner;
  }

  public void unassignFromOwner() {
    if (this.owner != null) {
      Owner currentOwner = this.owner;
      this.owner = null;
      currentOwner.getOwnedAquariums().remove(this);
    }
  }

  public void assignToManager(AquariumManager manager) {
    Validator.notNull(manager, "Aquarium Manager");

    unassignFromManager();

    this.aquariumManager = manager;
    if (!manager.getAquariums().contains(this)) {
      manager.addToAquariums(this);
    }
  }

  public void unassignFromManager() {
    if (this.aquariumManager != null) {
      AquariumManager currentManager = this.aquariumManager;
      this.aquariumManager = null;
      currentManager.getAquariums().remove(this);
    }
  }

  public boolean isOwnedBy(Long ownerId) {
    return this.owner != null && this.owner.getId() != null && this.owner.getId().equals(ownerId);
  }

  public double getVolume() {
    return dimensions != null ? dimensions.getVolumeInLiters() : 0;
  }

  public Set<Inhabitant> getInhabitantsByWaterType(WaterType waterType) {
    return this.inhabitants.stream()
        .filter(i -> i.getWaterType() == waterType)
        .collect(Collectors.toSet());
  }

  public Set<Inhabitant> getSchoolingInhabitants() {
    return this.inhabitants.stream()
        .filter(Inhabitant::isSchooling)
        .collect(Collectors.toSet());
  }

  public void transferOrnament(Ornament ornament, Aquarium targetAquarium) {
    if (ornament == null) {
      return;
    }

    if (this.ornaments.contains(ornament)) {
      this.removeFromOrnaments(ornament);
    }

    if (targetAquarium != null && targetAquarium != this) {
      targetAquarium.addToOrnaments(ornament);
    }
  }

  public Ornament createAndAddOrnament(String name, String description, String color, boolean supportsAirPump) {
    if (this.owner == null) {
        throw new DomainException("Cannot create ornament in an aquarium without an owner.");
    }
    Ornament ornament = new Ornament(name, description, color, supportsAirPump, this.owner.getId(), null);
    this.addToOrnaments(ornament);
    return ornament;
  }

  public void transferAccessory(Accessory accessory, Aquarium targetAquarium) {
    if (accessory == null) {
      return;
    }

    if (this.accessories.contains(accessory)) {
      this.removeFromAccessories(accessory);
    }

    if (targetAquarium != null && targetAquarium != this) {
      targetAquarium.addToAccessories(accessory);
    }
  }

  public void transferInhabitant(Inhabitant inhabitant, Aquarium targetAquarium) {
    if (inhabitant == null) {
      return;
    }

    if (this.inhabitants.contains(inhabitant)) {
      this.removeFromInhabitants(inhabitant);
    }

    if (targetAquarium != null && targetAquarium != this) {
      targetAquarium.addToInhabitants(inhabitant);
    }
  }

  public Fish createAndAddFish(String species, String color, int count, boolean isSchooling,
      boolean isAggressiveEater, boolean requiresSpecialFood, WaterType waterType, boolean isSnailEater, String description) {
    
    if (this.owner == null) {
        throw new DomainException("Cannot create fish in an aquarium without an owner.");
    }
    Fish fish = Fish.create(species, color, count, isSchooling, isAggressiveEater,
        requiresSpecialFood, waterType, isSnailEater, this.owner.getId(), null, description);

    this.addToInhabitants(fish);
    return fish;
  }

  public void verifyOwnership(Long ownerId) {
    if (!isOwnedBy(ownerId)) {
      throw new DomainException("This aquarium is not owned by the user with ID: " + ownerId);
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
    if (name != null) {
      updateName(name);
    }

    if (length != null && width != null && height != null) {
      updateDimensions(length, width, height);
    }

    if (substrate != null) {
      updateSubstrate(substrate);
    }

    if (waterType != null) {
      updateWaterType(waterType);
    }

    if (state != null) {
      updateState(state);
    }

    if (temperature != null) {
      updateTemperature(temperature);
    }

    return this;
  }
}
