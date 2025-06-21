package nl.hu.bep.domain;

import nl.hu.bep.domain.base.AssignableEntity;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.utils.Validator;
import lombok.*;
import nl.hu.bep.domain.species.Fish;
import nl.hu.bep.domain.species.Snail;
import nl.hu.bep.domain.species.Shrimp;
import nl.hu.bep.domain.species.Crayfish;
import nl.hu.bep.domain.species.Plant;
import nl.hu.bep.domain.species.Coral;
import java.util.Optional;

/**
 * Pure JDBC inhabitant entity - ID-based relationships only.
 * Clean DDD implementation with domain security.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Inhabitant extends AssignableEntity {
    private Long id;
    private String species;
    private String color;
    private int count;
    private boolean isSchooling;
    private WaterType waterType;
    private Long ownerId;
    private String name;
    private String description;
    private java.time.LocalDateTime dateCreated;

    // Base constructor for new entities
    protected Inhabitant(String name, String species, Long ownerId, Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling, Optional<WaterType> waterType, Optional<String> description) {
        this.name = Validator.notEmpty(name, "Inhabitant name");
        this.species = Validator.notEmpty(species, "Species");
        this.ownerId = Validator.notNull(ownerId, "Owner ID");
        this.color = color.orElse(null);
        this.count = count.orElse(1);
        this.isSchooling = isSchooling.orElse(false);
        this.waterType = waterType.orElse(WaterType.FRESHWATER);
        this.description = description.orElse(null);
        this.dateCreated = java.time.LocalDateTime.now();
    }
    
    // Protected constructor for repository reconstruction
    protected Inhabitant(Long id, String name, String species, Long ownerId, String color, int count, boolean isSchooling, WaterType waterType, String description, java.time.LocalDateTime dateCreated, Long aquariumId) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.ownerId = ownerId;
        this.color = color;
        this.count = count;
        this.isSchooling = isSchooling;
        this.waterType = waterType;
        this.description = description;
        this.dateCreated = dateCreated;
        super.aquariumId = aquariumId;
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

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
        super.assignToAquarium(aquariumId);
    }

    public void removeFromAquarium(Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        super.removeFromAquarium();
    }

    /**
     * Single factory method for creating inhabitants by type.
     * Consolidates logic and delegates specific property setting to subclasses.
     */
    public static Inhabitant createFromType(String type, String name, String species, Long ownerId,
                                          Optional<String> color, Optional<Integer> count, Optional<Boolean> isSchooling,
                                          Optional<WaterType> waterType, Optional<String> description,
                                          InhabitantProperties properties) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Inhabitant type is required");
        }
        
        return switch (type.toLowerCase()) {
            case "fish" -> Fish.create(name, species, ownerId, color, count, isSchooling, waterType, description, properties);
            case "snail" -> Snail.create(name, species, ownerId, color, count, isSchooling, waterType, description, properties);
            case "shrimp" -> Shrimp.create(name, species, ownerId, color, count, isSchooling, waterType, description);
            case "crayfish" -> Crayfish.create(name, species, ownerId, color, count, isSchooling, waterType, description);
            case "plant" -> Plant.create(name, species, ownerId, color, count, isSchooling, waterType, description);
            case "coral" -> Coral.create(name, species, ownerId, color, count, isSchooling, waterType, description);
            default -> throw new IllegalArgumentException("Unsupported inhabitant type: " + type);
        };
    }
    
    // Repository reconstruction method for JDBC mapping - NO REFLECTION
    public static Inhabitant reconstruct(String type, Long id, String species, String color, int count,
                                       boolean isSchooling, WaterType waterType, Long ownerId, String name,
                                       String description, java.time.LocalDateTime dateCreated, Long aquariumId,
                                       boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        return switch (type.toLowerCase()) {
            case "fish" -> Fish.reconstruct(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId, isAggressiveEater, requiresSpecialFood, isSnailEater);
            case "snail" -> Snail.reconstruct(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId, isSnailEater);
            case "shrimp" -> Shrimp.reconstruct(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
            case "crayfish" -> Crayfish.reconstruct(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
            case "plant" -> Plant.reconstruct(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
            case "coral" -> Coral.reconstruct(id, name, species, ownerId, color, count, isSchooling, waterType, description, dateCreated, aquariumId);
            default -> throw new IllegalArgumentException("Unknown inhabitant type: " + type);
        };
    }

    // Abstract method for type-specific property extraction - REPLACES REFLECTION
    public abstract InhabitantProperties getTypeSpecificProperties();

    // Value object for type-specific properties - REPLACES REFLECTION
    @Getter
    @AllArgsConstructor
    public static class InhabitantProperties {
        public final boolean isAggressiveEater;
        public final boolean requiresSpecialFood;
        public final boolean isSnailEater;
        
        // Default properties for types that don't have all properties
        public static InhabitantProperties defaults() {
            return new InhabitantProperties(false, false, false);
        }
    }

    // Unified domain ownership validation - consistent across all entities
    public void validateOwnership(Long requestingOwnerId) {
        if (requestingOwnerId == null) {
            throw new IllegalArgumentException("Owner ID is required for validation");
        }
        
        if (!this.ownerId.equals(requestingOwnerId)) {
            throw new IllegalArgumentException("Access denied: You do not own this inhabitant");
        }
    }
}
