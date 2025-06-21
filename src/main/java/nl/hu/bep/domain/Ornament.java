package nl.hu.bep.domain;

import lombok.*;
import nl.hu.bep.domain.base.AssignableEntity;
import nl.hu.bep.domain.utils.Validator;
import java.time.LocalDateTime;
import java.util.Optional;

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
    private LocalDateTime dateCreated;

    /**
     * Single factory method to create a new Ornament.
     * Uses Optional for nullable fields to avoid multiple method overloads.
     */
    public static Ornament create(String name, Long ownerId, Optional<String> description, Optional<String> color, Optional<String> material, Optional<Boolean> isAirPumpCompatible) {
        Validator.notEmpty(name, "Ornament name");
        Validator.notNull(ownerId, "Owner ID");

        return Ornament.builder()
                .name(name)
                .ownerId(ownerId)
                .description(description.orElse(null))
                .color(color.orElse(null))
                .material(material.orElse(null))
                .isAirPumpCompatible(isAirPumpCompatible.orElse(false))
                .dateCreated(LocalDateTime.now())
                .build();
    }

    // Business logic methods for internal updates
    private void updateName(String name) {
        this.name = Validator.notEmpty(name, "Ornament name");
    }

    private void updateDescription(String description) {
        this.description = description;
    }

    private void updateColor(String color) {
        this.color = color;
    }

    private void updateMaterial(String material) {
        this.material = material;
    }

    private void updateAirPumpCompatibility(boolean isAirPumpCompatible) {
        this.isAirPumpCompatible = isAirPumpCompatible;
    }

    /**
     * Single method to update an Ornament's properties.
     * Uses Optional to allow partial updates.
     */
    public Ornament update(Optional<String> name, Optional<String> description, Optional<String> color, Optional<String> material, Optional<Boolean> isAirPumpCompatible) {
        name.ifPresent(this::updateName);
        description.ifPresent(this::updateDescription);
        color.ifPresent(this::updateColor);
        material.ifPresent(this::updateMaterial);
        isAirPumpCompatible.ifPresent(this::updateAirPumpCompatibility);
        return this;
    }

    // Secure aquarium assignment methods with domain validation
    public void assignToAquarium(Long aquariumId, Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        super.assignToAquarium(aquariumId);
    }

    public void removeFromAquarium(Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        super.removeFromAquarium();
    }

    // For JPA compatibility (if needed)
    public void setAquarium(Aquarium aquarium) {
        this.aquariumId = aquarium != null ? aquarium.getId() : null;
    }

    // Repository reconstruction method for JDBC mapping
    public static Ornament reconstruct(Long id, String name, String description, String color,
                                     String material, boolean isAirPumpCompatible, Long ownerId,
                                     Long aquariumId, LocalDateTime dateCreated) {
        Ornament ornament = Ornament.builder()
                .id(id)
                .name(name)
                .description(description)
                .color(color)
                .material(material)
                .ownerId(ownerId)
                .isAirPumpCompatible(isAirPumpCompatible)
                .dateCreated(dateCreated)
                .build();
        ornament.aquariumId = aquariumId;
        return ornament;
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
