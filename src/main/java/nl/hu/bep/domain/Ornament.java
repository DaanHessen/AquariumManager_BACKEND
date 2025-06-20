package nl.hu.bep.domain;

import lombok.*;
import nl.hu.bep.domain.base.AssignableEntity;
import nl.hu.bep.domain.utils.Validator;
import java.time.LocalDateTime;

/**
 * Represents an ornament in an aquarium.
 * Clean POJO implementation following DDD principles.
 */
@Getter
@Builder(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"aquariumId"})
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ornament extends AssignableEntity {
    private Long id;
    private String name;
    private String description;
    private String color;
    private String material;
    private boolean isAirPumpCompatible;
    private Long ownerId;
    private Long aquariumId; // ID-based relationship for JDBC
    private LocalDateTime dateCreated;

    // Factory method for creating new ornaments
    public static Ornament create(String name, String description, String color, boolean isAirPumpCompatible, Long ownerId) {
        Validator.notEmpty(name, "Ornament name");
        Validator.notNull(ownerId, "Owner ID");

        return Ornament.builder()
                .name(name)
                .description(description)
                .color(color)
                .isAirPumpCompatible(isAirPumpCompatible)
                .ownerId(ownerId)
                .dateCreated(LocalDateTime.now())
                .build();
    }

    // Overloaded factory method for additional material parameter
    public static Ornament create(String name, String description, String color, Boolean isAirPumpCompatible, Long ownerId, String material) {
        Validator.notEmpty(name, "Ornament name");
        Validator.notNull(ownerId, "Owner ID");

        return Ornament.builder()
                .name(name)
                .description(description)
                .color(color)
                .material(material)
                .isAirPumpCompatible(isAirPumpCompatible != null ? isAirPumpCompatible : false)
                .ownerId(ownerId)
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
        this.color = color;
    }

    public void updateMaterial(String material) {
        this.material = material;
    }

    public void updateAirPumpCompatibility(boolean isAirPumpCompatible) {
        this.isAirPumpCompatible = isAirPumpCompatible;
    }

    // Comprehensive update method
    public Ornament update(String name, String description, String color, String material, Boolean isAirPumpCompatible) {
        if (name != null) updateName(name);
        if (description != null) updateDescription(description);
        if (color != null) updateColor(color);
        if (material != null) updateMaterial(material);
        if (isAirPumpCompatible != null) updateAirPumpCompatibility(isAirPumpCompatible);
        return this;
    }

    // Secure aquarium assignment methods with domain validation
    public void assignToAquarium(Long aquariumId, Long requestingOwnerId) {
        // Domain security: Only owner can assign ornament to aquarium
        if (!this.ownerId.equals(requestingOwnerId)) {
            throw new IllegalArgumentException("Only the ornament owner can assign it to an aquarium");
        }
        this.aquariumId = aquariumId;
    }

    public void removeFromAquarium(Long requestingOwnerId) {
        // Domain security: Only owner can remove ornament from aquarium
        if (!this.ownerId.equals(requestingOwnerId)) {
            throw new IllegalArgumentException("Only the ornament owner can remove it from an aquarium");
        }
        this.aquariumId = null;
    }

    // For JPA compatibility (if needed)
    public void setAquarium(Aquarium aquarium) {
        this.aquariumId = aquarium != null ? aquarium.getId() : null;
    }

    // Repository reconstruction method for JDBC mapping
    public static Ornament reconstruct(Long id, String name, String description, String color, 
                                     String material, boolean isAirPumpCompatible, Long ownerId, 
                                     Long aquariumId, LocalDateTime dateCreated) {
        return Ornament.builder()
                .id(id)
                .name(name)
                .description(description)
                .color(color)
                .material(material)
                .isAirPumpCompatible(isAirPumpCompatible)
                .ownerId(ownerId)
                .aquariumId(aquariumId)
                .dateCreated(dateCreated)
                .build();
    }

    // Native domain ownership validation - DDD compliant
    public void validateOwnership(Long requestingOwnerId) {
        if (requestingOwnerId == null) {
            throw new IllegalArgumentException("Owner ID is required for validation");
        }
        
        if (!this.ownerId.equals(requestingOwnerId)) {
            throw new IllegalArgumentException("Access denied: You do not own this ornament");
        }
    }
}
