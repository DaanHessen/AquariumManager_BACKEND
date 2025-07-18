package nl.hu.bep.domain;

import nl.hu.bep.domain.base.OwnedEntity;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.exception.ApplicationException.BusinessRuleException;
import nl.hu.bep.config.AquariumConstants;
import nl.hu.bep.domain.species.*;

import lombok.*;
import java.util.Optional;
import java.time.LocalDateTime;

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

    public void assignToAquarium(Long aquariumId, Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        this.aquariumId = aquariumId;
    }

    public void removeFromAquarium(Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        this.aquariumId = null;
    }

    public boolean isAssignedToAquarium() {
        return aquariumId != null;
    }

    public static Inhabitant create(String type, String species, String name, Long ownerId, 
                                  Optional<String> color, Optional<Integer> count, 
                                  Optional<Boolean> isSchooling, Optional<WaterType> waterType, 
                                  Optional<String> description, InhabitantProperties properties) {
        
        return switch (type.toLowerCase()) {
            case "fish" -> Fish.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .isAggressiveEater(properties != null ? properties.isAggressiveEater : false)
                    .requiresSpecialFood(properties != null ? properties.requiresSpecialFood : false)
                    .isSnailEater(properties != null ? properties.isSnailEater : false)
                    .build();
                    
            case "plant" -> Plant.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .build();
                    
            case "snail" -> Snail.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .isSnailEater(properties != null ? properties.isSnailEater : false)
                    .build();
                    
            case "shrimp" -> Shrimp.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .build();
                    
            case "crayfish" -> Crayfish.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .build();
                    
            case "coral" -> Coral.builder()
                    .name(name)
                    .species(species)
                    .ownerId(ownerId)
                    .color(color.orElse(null))
                    .count(count.orElse(null))
                    .isSchooling(isSchooling.orElse(null))
                    .waterType(waterType.orElse(null))
                    .description(description.orElse(null))
                    .build();
                    
            default -> throw new BusinessRuleException("Invalid inhabitant type: " + type);
        };
    }

    public static Inhabitant reconstruct(String type, long id, String species, String color, int count, 
                                       boolean isSchooling, WaterType waterType, Long ownerId, String name, 
                                       String description, LocalDateTime dateCreated, Long aquariumId, 
                                       boolean isAggressiveEater, boolean requiresSpecialFood, boolean isSnailEater) {
        
        if (type == null || type.isEmpty()) {
            throw new BusinessRuleException("Inhabitant type is required for reconstruction");
        }

        return switch (type.toLowerCase()) {
            case "fish" -> Fish.builder()
                    .id(id)
                    .species(species)
                    .color(color)
                    .count(count)
                    .isSchooling(isSchooling)
                    .waterType(waterType)
                    .ownerId(ownerId)
                    .name(name)
                    .description(description)
                    .dateCreated(dateCreated)
                    .aquariumId(aquariumId)
                    .isAggressiveEater(isAggressiveEater)
                    .requiresSpecialFood(requiresSpecialFood)
                    .isSnailEater(isSnailEater)
                    .build();
                    
            case "plant" -> Plant.builder()
                    .id(id)
                    .species(species)
                    .color(color)
                    .count(count)
                    .isSchooling(isSchooling)
                    .waterType(waterType)
                    .ownerId(ownerId)
                    .name(name)
                    .description(description)
                    .dateCreated(dateCreated)
                    .aquariumId(aquariumId)
                    .build();
                    
            case "snail" -> Snail.builder()
                    .id(id)
                    .species(species)
                    .color(color)
                    .count(count)
                    .isSchooling(isSchooling)
                    .waterType(waterType)
                    .ownerId(ownerId)
                    .name(name)
                    .description(description)
                    .dateCreated(dateCreated)
                    .aquariumId(aquariumId)
                    .isSnailEater(isSnailEater)
                    .build();
                    
            case "shrimp" -> Shrimp.builder()
                    .id(id)
                    .species(species)
                    .color(color)
                    .count(count)
                    .isSchooling(isSchooling)
                    .waterType(waterType)
                    .ownerId(ownerId)
                    .name(name)
                    .description(description)
                    .dateCreated(dateCreated)
                    .aquariumId(aquariumId)
                    .build();
                    
            case "crayfish" -> Crayfish.builder()
                    .id(id)
                    .species(species)
                    .color(color)
                    .count(count)
                    .isSchooling(isSchooling)
                    .waterType(waterType)
                    .ownerId(ownerId)
                    .name(name)
                    .description(description)
                    .dateCreated(dateCreated)
                    .aquariumId(aquariumId)
                    .build();
                    
            case "coral" -> Coral.builder()
                    .id(id)
                    .species(species)
                    .color(color)
                    .count(count)
                    .isSchooling(isSchooling)
                    .waterType(waterType)
                    .ownerId(ownerId)
                    .name(name)
                    .description(description)
                    .dateCreated(dateCreated)
                    .aquariumId(aquariumId)
                    .build();
                    
            default -> throw new BusinessRuleException("Unsupported inhabitant type: " + type);
        };
    }

    public abstract InhabitantProperties getTypeSpecificProperties();
    public abstract boolean isCompatibleWith(Inhabitant other);
    
    public abstract String getInhabitantType();
    public abstract Boolean getAggressiveEater();
    public abstract Boolean getRequiresSpecialFood();
    public abstract Boolean getSnailEater();

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class InhabitantProperties {
        public final boolean isAggressiveEater;
        public final boolean requiresSpecialFood;
        public final boolean isSnailEater;
        
        public static InhabitantProperties defaults() {
            return new InhabitantProperties(false, false, false);
        }
    }
}
