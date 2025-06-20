package nl.hu.bep.domain;

import lombok.*;
import nl.hu.bep.domain.base.AssignableEntity;
import nl.hu.bep.domain.utils.Validator;
import java.time.LocalDateTime;

/**
 * Represents a decorative ornament in an aquarium.

 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"aquariumId"})
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ornament extends AssignableEntity {
    private Long id;
    private String name;
    private String description;
    private String color;
    private boolean isAirPumpCompatible;
    private Long ownerId;
    private String material;
    private LocalDateTime dateCreated;
    private Long aquariumId; // ID-based relationship

    // Factory method for business logic
    public static Ornament create(String name, String description, String color, 
                                 Boolean isAirPumpCompatible, Long ownerId, String material) {
        return Ornament.builder()
                .name(Validator.notEmpty(name, "Ornament name"))
                .description(description)
                .color(Validator.notEmpty(color, "Ornament color"))
                .isAirPumpCompatible(isAirPumpCompatible != null && isAirPumpCompatible)
                .ownerId(Validator.notNull(ownerId, "Owner ID"))
                .material(material)
                .dateCreated(LocalDateTime.now())
                .build();
    }

    // Business logic methods
    public void updateName(String name) {
        this.name = Validator.notEmpty(name, "Ornament name");
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateColor(String color) {
        this.color = Validator.notEmpty(color, "Ornament color");
    }

    public void updateAirPumpCompatibility(boolean isAirPumpCompatible) {
        this.isAirPumpCompatible = isAirPumpCompatible;
    }

    public void updateMaterial(String material) {
        this.material = material;
    }

    // Comprehensive update method
    public Ornament update(String name, String description, String color, 
                          Boolean isAirPumpCompatible, String material) {
        if (name != null) updateName(name);
        if (description != null) updateDescription(description);
        if (color != null) updateColor(color);
        if (isAirPumpCompatible != null) updateAirPumpCompatibility(isAirPumpCompatible);
        if (material != null) updateMaterial(material);
        return this;
    }

    // Secure aquarium assignment methods with domain validation
    public void assignToAquarium(Long aquariumId, Long requestingOwnerId) {
        // Domain security: Only owner can assign ornament to aquarium
        if (!this.ownerId.equals(requestingOwnerId)) {
            throw new IllegalArgumentException("Only the ornament owner can assign it to an aquarium");
        }
        // Direct assignment with validation passed
        this.aquariumId = aquariumId;
    }

    public void removeFromAquarium(Long requestingOwnerId) {
        // Domain security: Only owner can remove ornament from aquarium
        if (!this.ownerId.equals(requestingOwnerId)) {
            throw new IllegalArgumentException("Only the ornament owner can remove it from an aquarium");
        }
        // Direct removal with validation passed
        this.aquariumId = null;
    }

    // Public method for repository reconstruction only
    public static Ornament reconstruct(Long id, String name, String description, String color,
                                      boolean isAirPumpCompatible, Long ownerId, String material,
                                      LocalDateTime dateCreated, Long aquariumId) {
        return Ornament.builder()
                .id(id)
                .name(name)
                .description(description)
                .color(color)
                .isAirPumpCompatible(isAirPumpCompatible)
                .ownerId(ownerId)
                .material(material)
                .dateCreated(dateCreated)
                .aquariumId(aquariumId)
                .build();
    }
}
