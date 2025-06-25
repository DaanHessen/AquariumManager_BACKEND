package nl.hu.bep.domain;

import nl.hu.bep.domain.base.AssignableEntity;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.exception.domain.OwnershipException;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
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

    public Ornament update(Optional<String> name, Optional<String> description, Optional<String> color, Optional<String> material, Optional<Boolean> isAirPumpCompatible) {
        name.ifPresent(this::updateName);
        description.ifPresent(this::updateDescription);
        color.ifPresent(this::updateColor);
        material.ifPresent(this::updateMaterial);
        isAirPumpCompatible.ifPresent(this::updateAirPumpCompatibility);
        return this;
    }

    public void assignToAquarium(Long aquariumId, Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        super.assignToAquarium(aquariumId);
    }

    public void removeFromAquarium(Long requestingOwnerId) {
        validateOwnership(requestingOwnerId);
        super.removeFromAquarium();
    }

    public void setAquarium(Aquarium aquarium) {
        this.aquariumId = aquarium != null ? aquarium.getId() : null;
    }

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

    public void validateOwnership(Long requestingOwnerId) {
        Validator.notNull(requestingOwnerId, "Requesting Owner ID");
        if (this.ownerId == null || !this.ownerId.equals(requestingOwnerId)) {
            throw new OwnershipException("Ornament does not belong to the current user.");
        }
    }
}
