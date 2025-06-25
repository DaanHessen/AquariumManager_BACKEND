package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.domain.DomainException;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.domain.value.Dimensions;
import nl.hu.bep.domain.species.Fish;
import nl.hu.bep.config.AquariumConstants;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Pure JDBC Aquarium entity - ID-based relationships only.
 * Clean DDD implementation with domain security.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"ownerId", "aquariumManagerId"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
public class Aquarium {

    private Long id;
    private String name;
    private Dimensions dimensions;
    private SubstrateType substrate;
    private WaterType waterType;
    private Double temperature;
    private AquariumState state;
    private LocalDateTime currentStateStartTime;
    private String color;
    private String description;
    private LocalDateTime dateCreated;

    // ID-based relationships for pure JDBC
    private Long ownerId;
    private Long aquariumManagerId;

    private Set<Inhabitant> inhabitants = new HashSet<>();


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
        aquarium.temperature = AquariumConstants.DEFAULT_AQUARIUM_TEMPERATURE;
        aquarium.state = state != null ? state : AquariumState.SETUP;
        aquarium.currentStateStartTime = LocalDateTime.now();
        aquarium.color = color;
        aquarium.description = description;
        aquarium.dateCreated = LocalDateTime.now();

        return aquarium;
    }

    // Repository reconstruction method for JDBC mapping - NO REFLECTION
    public static Aquarium reconstruct(Long id, String name, Dimensions dimensions,
                                     SubstrateType substrate, WaterType waterType, Double temperature,
                                     AquariumState state, LocalDateTime currentStateStartTime,
                                     String color, String description, LocalDateTime dateCreated,
                                     Long aquariumManagerId, Long ownerId) {
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
        return aquarium;
    }

    public Set<Inhabitant> getInhabitants() {
        return Collections.unmodifiableSet(inhabitants);
    }

    public void addInhabitant(Inhabitant inhabitant, Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        if (inhabitant.getAquariumId() != null && !inhabitant.getAquariumId().equals(this.id)) {
            throw new DomainException("Inhabitant is already in another aquarium.");
        }

        for (Inhabitant existing : inhabitants) {
            if (!inhabitant.isCompatibleWith(existing)) {
                throw new DomainException("Inhabitant " + inhabitant.getName() + " is not compatible with " + existing.getName());
            }
        }

        inhabitant.assignToAquarium(this.id, requestingOwnerId);
        this.inhabitants.add(inhabitant);
    }

    public void removeInhabitant(Inhabitant inhabitant, Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        if (inhabitants.remove(inhabitant)) {
            inhabitant.removeFromAquarium(requestingOwnerId);
        }
    }

    // Domain assignment methods
    public void assignToOwner(Long ownerId) {
        Validator.notNull(ownerId, "Owner ID");
        this.ownerId = ownerId;
    }

    public void assignToManager(Long managerId) {
        this.aquariumManagerId = managerId;
    }

    public void unassignFromOwner() {
        this.ownerId = null;
    }

    public void unassignFromManager() {
        this.aquariumManagerId = null;
    }

    public boolean isOwnedBy(Long ownerId) {
        return this.ownerId != null && this.ownerId.equals(ownerId);
    }

    // Unified domain ownership validation - DDD compliant
    public void validateOwnership(Long requestingOwnerId) {
        if (requestingOwnerId == null) {
            throw new DomainException("Owner ID is required for ownership verification");
        }

        if (!isOwnedBy(requestingOwnerId)) {
            throw new DomainException("Access denied: You do not own this aquarium");
        }
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

        if (oldState != newState) {
            this.currentStateStartTime = LocalDateTime.now();
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

    public double getVolume() {
        return dimensions.getVolume();
    }

    // Rich domain business logic methods

    /**
     * Calculate recommended inhabitant capacity based on water type and volume
     */
    public int getRecommendedInhabitantCapacity() {
        double volume = getVolume();

        return switch (waterType) {
            case FRESHWATER -> (int) (volume / 5.0); // 5 liters per small fish
            case SALTWATER -> (int) (volume / 8.0);  // 8 liters per marine fish (more space needed)
            case BRACKISH -> (int) (volume / 6.0);   // 6 liters per brackish fish
        };
    }

    /**
     * Check if aquarium can accommodate additional inhabitants
     */
    public boolean canAccommodate(int additionalInhabitants) {
        if (state != AquariumState.RUNNING) {
            return false; // Can't add inhabitants to non-running aquarium
        }

        // This would typically check current inhabitant count + new ones against capacity
        // For now, just check against recommended capacity
        return additionalInhabitants <= getRecommendedInhabitantCapacity();
    }

    /**
     * Validate water compatibility
     */
    public boolean isWaterTypeCompatible(WaterType inhabitantWaterType) {
        return this.waterType == inhabitantWaterType;
    }

    /**
     * Temperature range validation
     */
    public boolean isTemperatureInRange(double minTemp, double maxTemp) {
        return temperature != null && temperature >= minTemp && temperature <= maxTemp;
    }

    /**
     * Check if aquarium is suitable for tropical fish
     */
    public boolean isSuitableForTropicalFish() {
        return isTemperatureInRange(24.0, 28.0) && waterType == WaterType.FRESHWATER;
    }

    /**
     * Check if aquarium is suitable for marine fish
     */
    public boolean isSuitableForMarineFish() {
        return isTemperatureInRange(22.0, 26.0) && waterType == WaterType.SALTWATER;
    }

    /**
     * Validate aquarium readiness for inhabitants
     */
    public void validateReadinessForInhabitants() {
        if (state != AquariumState.RUNNING) {
            throw new DomainException("Aquarium must be running before adding inhabitants");
        }

        if (temperature == null) {
            throw new DomainException("Temperature must be set before adding inhabitants");
        }

        if (temperature < 10.0 || temperature > 35.0) {
            throw new DomainException("Temperature must be between 10°C and 35°C for inhabitants");
        }

        if (getVolume() < 20.0) {
            throw new DomainException("Aquarium volume must be at least 20 liters for inhabitants");
        }
    }

    /**
     * Business rule for substrate compatibility
     */
    public boolean isSubstrateCompatibleWith(String fishSpecies) {
        // Different fish prefer different substrates
        return switch (substrate) {
            case SAND -> true; // Sand is generally safe for all fish
            case GRAVEL -> !fishSpecies.toLowerCase().contains("betta"); // Bettas prefer softer substrates
            case SOIL -> fishSpecies.toLowerCase().contains("tetra") ||
                    fishSpecies.toLowerCase().contains("guppy"); // Community fish love plants
        };
    }

    /**
     * Calculate maintenance frequency based on size and bio-load
     */
    public int getRecommendedMaintenanceIntervalDays() {
        double volume = getVolume();

        if (volume < 50) {
            return 3; // Small tanks need frequent maintenance
        } else if (volume < 200) {
            return 7; // Medium tanks weekly maintenance
        } else {
            return 14; // Large tanks bi-weekly maintenance
        }
    }

    /**
     * Check if maintenance is overdue
     */
    public boolean isMaintenanceOverdue() {
        if (state != AquariumState.RUNNING) {
            return false; // Not relevant if not running
        }

        long daysSinceLastMaintenance = getCurrentStateDurationMinutes() / (24 * 60);
        return daysSinceLastMaintenance > getRecommendedMaintenanceIntervalDays();
    }

    /**
     * Advanced state transition with business rules
     */
    public boolean canTransitionTo(AquariumState newState) {
        return switch (this.state) {
            case SETUP -> newState == AquariumState.RUNNING || newState == AquariumState.INACTIVE;
            case RUNNING -> newState == AquariumState.MAINTENANCE || newState == AquariumState.INACTIVE;
            case MAINTENANCE -> newState == AquariumState.RUNNING || newState == AquariumState.INACTIVE;
            case INACTIVE -> newState == AquariumState.SETUP;
        };
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

    public boolean canAccommodate(Inhabitant inhabitant) {
        if (this.waterType != inhabitant.getWaterType()) {
            return false; // Basic water type compatibility
        }

        // Additional checks can be added here, e.g., for space, temperature, etc.
        return true;
    }
}
