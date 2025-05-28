package nl.hu.bep.domain;

import jakarta.persistence.*;
import lombok.*;
import nl.hu.bep.domain.utils.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "aquarium_manager")
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = { "owners", "aquariums", "inhabitants" })
@ToString(exclude = { "owners", "aquariums", "inhabitants" })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AquariumManager {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @PastOrPresent
  @Column(name = "installation_date")
  private LocalDate installationDate;

  @OneToMany(mappedBy = "aquariumManager", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Owner> owners = new HashSet<>();

  @OneToMany(mappedBy = "aquariumManager", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Aquarium> aquariums = new HashSet<>();

  @OneToMany(mappedBy = "aquariumManager", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Inhabitant> inhabitants = new HashSet<>();

  public static AquariumManager create(LocalDate installationDate) {
    AquariumManager manager = new AquariumManager();
    manager.installationDate = Validator.notNull(installationDate, "Installation date");
    manager.owners = new HashSet<>();
    manager.aquariums = new HashSet<>();
    manager.inhabitants = new HashSet<>();
    return manager;
  }

  public void addToOwners(Owner owner) {
    Validator.notNull(owner, "Owner");

    if (owners.contains(owner)) {
      return;
    }

    this.owners.add(owner);
    if (owner.getAquariumManager() != this) {
      owner.setAquariumManager(this);
    }
  }

  public void removeFromOwners(Owner owner) {
    Validator.notNull(owner, "Owner");

    if (this.owners.contains(owner)) {
      this.owners.remove(owner);
      if (owner.getAquariumManager() == this) {
        owner.unassignFromManager();
      }
    }
  }

  public void addToAquariums(Aquarium aquarium) {
    Validator.notNull(aquarium, "Aquarium");

    if (aquariums.contains(aquarium)) {
      return;
    }

    this.aquariums.add(aquarium);
    if (aquarium.getAquariumManager() != this) {
      aquarium.assignToManager(this);
    }
  }

  public void removeFromAquariums(Aquarium aquarium) {
    Validator.notNull(aquarium, "Aquarium");

    if (this.aquariums.contains(aquarium)) {
      this.aquariums.remove(aquarium);
      if (aquarium.getAquariumManager() == this) {
        aquarium.unassignFromManager();
      }
    }
  }

  public void addToInhabitants(Inhabitant inhabitant) {
    Validator.notNull(inhabitant, "Inhabitant");

    if (inhabitants.contains(inhabitant)) {
      return;
    }

    this.inhabitants.add(inhabitant);
    if (inhabitant.getAquariumManager() != this) {
      inhabitant.setAquariumManager(this);
    }
  }

  public void removeFromInhabitants(Inhabitant inhabitant) {
    Validator.notNull(inhabitant, "Inhabitant");

    if (this.inhabitants.contains(inhabitant)) {
      this.inhabitants.remove(inhabitant);
      if (inhabitant.getAquariumManager() == this) {
        inhabitant.setAquariumManager(null);
      }
    }
  }
}
