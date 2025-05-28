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

@Entity
@Table(name = "aquariums")
@Getter
@EqualsAndHashCode(exclude = { "accessories", "inhabitants", "ornaments", "aquariumManager", "owner" })
@ToString(exclude = { "accessories", "inhabitants", "ornaments", "aquariumManager", "owner" })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
public class Aquarium {
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
    aquarium.color = color;
    aquarium.description = description;
    aquarium.dateCreated = LocalDateTime.now();

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

  public void updateState(AquariumState state) {
    this.state = Validator.notNull(state, "Aquarium state");
  }

  public void activateAquarium() {
    if (this.state != AquariumState.SETUP && this.state != AquariumState.MAINTENANCE) {
      throw new DomainException("Cannot activate aquarium from " + this.state + " state");
    }
    this.state = AquariumState.RUNNING;
  }

  public void startMaintenance() {
    if (this.state != AquariumState.RUNNING) {
      throw new DomainException("Cannot start maintenance when aquarium is not running");
    }
    this.state = AquariumState.MAINTENANCE;
  }

  public void deactivateAquarium() {
    this.state = AquariumState.INACTIVE;
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
    if (!owner.getOwnedAquariums().contains(this)) {
      owner.addToAquariums(this);
    }
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
