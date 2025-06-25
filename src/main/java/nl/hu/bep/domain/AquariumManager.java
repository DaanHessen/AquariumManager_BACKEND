package nl.hu.bep.domain;

import lombok.*;
import nl.hu.bep.domain.utils.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Getter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"ownerIds", "aquariumIds", "inhabitantIds"})
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AquariumManager {
    private Long id;
    private LocalDate installationDate;
    private String description;
    private LocalDateTime dateCreated;
    
    @Builder.Default
    private Set<Long> ownerIds = new HashSet<>();
    @Builder.Default
    private Set<Long> aquariumIds = new HashSet<>();
    @Builder.Default
    private Set<Long> inhabitantIds = new HashSet<>();

    public static AquariumManager create(LocalDate installationDate, String description) {
        return AquariumManager.builder()
                .installationDate(Validator.notNull(installationDate, "Installation date"))
                .description(description)
                .dateCreated(LocalDateTime.now())
                .build();
    }

    public static AquariumManager reconstruct(Long id, LocalDate installationDate, String description,
                                             LocalDateTime dateCreated, Set<Long> ownerIds,
                                             Set<Long> aquariumIds, Set<Long> inhabitantIds) {
        return AquariumManager.builder()
                .id(id)
                .installationDate(installationDate)
                .description(description)
                .dateCreated(dateCreated)
                .ownerIds(ownerIds != null ? ownerIds : new HashSet<>())
                .aquariumIds(aquariumIds != null ? aquariumIds : new HashSet<>())
                .inhabitantIds(inhabitantIds != null ? inhabitantIds : new HashSet<>())
                .build();
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void addOwner(Long ownerId) {
        if (ownerId != null) {
            this.ownerIds.add(ownerId);
        }
    }

    public void removeOwner(Long ownerId) {
        this.ownerIds.remove(ownerId);
    }

    public void addAquarium(Long aquariumId) {
        if (aquariumId != null) {
            this.aquariumIds.add(aquariumId);
        }
    }

    public void removeAquarium(Long aquariumId) {
        this.aquariumIds.remove(aquariumId);
    }

    public void addInhabitant(Long inhabitantId) {
        if (inhabitantId != null) {
            this.inhabitantIds.add(inhabitantId);
        }
    }

    public void removeInhabitant(Long inhabitantId) {
        this.inhabitantIds.remove(inhabitantId);
    }

    public boolean hasAquariums() {
        return !aquariumIds.isEmpty();
    }

    public boolean hasOwners() {
        return !ownerIds.isEmpty();
    }

    public int getTotalAquariumCount() {
        return aquariumIds.size();
    }
}
