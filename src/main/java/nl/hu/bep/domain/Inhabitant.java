package nl.hu.bep.domain;

import nl.hu.bep.domain.base.OwnedEntity;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.config.AquariumConstants;
import lombok.*;
import java.util.Optional;
import java.time.LocalDateTime;

/**
 * Pure JDBC inhabitant entity - ID-based relationships only.
 * Clean DDD implementation with domain security.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Inhabitant extends OwnedEntity {
    private Long id;
    private String species;
    private String color;
    private int count;
    private boolean isSchooling;
    private WaterType waterType;
    private String name;
    private String description;
    private LocalDateTime dateCreated;
    private Long aquariumId;

    // Single constructor for builders and reconstruction.
    // It handles validation and default values.
    protected Inhabitant(Long id, String name, String species, Long ownerId, String color, Integer count, Boolean isSchooling, WaterType waterType, String description, LocalDateTime dateCreated, Long aquariumId) {
        this.id = id;
        this.name = Validator.notEmpty(name, "Inhabitant name");
        this.species = Validator.notEmpty(species, "Species");
        this.ownerId = Validator.notNull(ownerId, "Owner ID");
        this.color = color;
        this.count = Optional.ofNullable(count).orElse(AquariumConstants.DEFAULT_INHABITANT_COUNT);
        this.isSchooling = Optional.ofNullable(isSchooling).orElse(AquariumConstants.DEFAULT_SCHOOLING);
        this.waterType = Optional.ofNullable(waterType).orElse(WaterType.FRESHWATER);
        this.description = description;
        this.dateCreated = Optional.ofNullable(dateCreated).orElse(LocalDateTime.now());
        this.aquariumId = aquariumId;
    }

    public abstract String getType();

    // Business logic methods
    private void updateSpecies(String species) {
        this.species = Validator.notEmpty(species, "Species");
    }

    private void updateColor(String color) {
        this.color = color;
    }

    private void updateCount(int count) {
        this.count = Validator.positive(count, "Count");
    }

    private void updateSchooling(boolean isSchooling) {
        this.isSchooling = isSchooling;
    }

    private void updateWaterType(WaterType waterType) {
        this.waterType = Validator.notNull(waterType, "Water type");
    }

    private void updateName(String name) {
        this.name = name;
    }

    private void updateDescription(String description) {
        this.description = description;
    }

    /**
     * Single method to update an Inhabitant's properties.
     * Uses Optional to allow partial updates.
     */
    public Inhabitant update(Optional<String> name, Optional<String> species, Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling, Optional<WaterType> waterType, Optional<String> description) {
        name.ifPresent(this::updateName);
        species.ifPresent(this::updateSpecies);
        color.ifPresent(this::updateColor);
        count.ifPresent(this::updateCount);
        isSchooling.ifPresent(this::updateSchooling);
        waterType.ifPresent(this::updateWaterType);
        description.ifPresent(this::updateDescription);
        return this;
    }

    // Secure aquarium assignment - consistent with other entities
    public void assignToAquarium(Long aquariumId, Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        this.aquariumId = aquariumId;
    }

    public void removeFromAquarium(Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        this.aquariumId = null;
    }

    // Helper methods from AssignableEntity
    public boolean isAssignedToAquarium() {
        return aquariumId != null;
    }

    // Domain ownership validation - delegates to parent class
    // Note: This method is inherited from OwnedEntity and doesn't need to be overridden

    // Factory method for creation - to be implemented by concrete subclasses
    // This is a template method that subclasses can override
    public static Inhabitant create(String species, String name, String ownerId, Optional<String> color, 
                                  Optional<Integer> count, Optional<Boolean> isSchooling, 
                                  Optional<WaterType> waterType, Optional<String> description, 
                                  InhabitantProperties properties) {
        // This method should be overridden by concrete subclasses
        // For now, we'll determine the type based on species name (simplified approach)
        throw new UnsupportedOperationException("Use specific subclass create method instead");
    }

    // Reconstruction method for repository use
    public static Inhabitant reconstruct(String type, long id, String name, String species, int count, 
                                       boolean isSchooling, WaterType waterType, Long ownerId, String color, 
                                       String description, LocalDateTime dateCreated, Long aquariumId, 
                                       boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        throw new UnsupportedOperationException("Subclasses must implement reconstruct method");
    }

    // Abstract methods for polymorphic behavior  
    public abstract InhabitantProperties getTypeSpecificProperties();
    public abstract boolean isCompatibleWith(Inhabitant other);
    
    // Polymorphic methods to eliminate instanceof checks in EntityMapper
    public abstract String getInhabitantType();
    public abstract Boolean getAggressiveEater();
    public abstract Boolean getRequiresSpecialFood();
    public abstract Boolean getSnailEater();

    // Value object for type-specific properties
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class InhabitantProperties {
        public final boolean isAggressiveEater;
        public final boolean requiresSpecialFood;
        public final boolean isSnailEater;
        
        // Default properties for types that don't have all properties
        public static InhabitantProperties defaults() {
            return new InhabitantProperties(false, false, false);
        }
    }
}
