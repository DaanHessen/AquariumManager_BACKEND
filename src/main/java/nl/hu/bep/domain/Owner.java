package nl.hu.bep.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.*;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.domain.enums.Role;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "owners")
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = { "ownedAquariums", "aquariumManager" })
@ToString(exclude = { "ownedAquariums", "aquariumManager", "password" })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "first_name")
  private String firstName;

  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "last_name")
  private String lastName;

  @Email
  @NotNull
  @Column(name = "email", unique = true)
  private String email;

  @NotNull
  @Size(min = 6, max = 128)
  @Column(name = "password")
  private String password;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Role role;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Aquarium> ownedAquariums = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aquarium_manager_id")
  private AquariumManager aquariumManager;

  public static Owner create(String firstName, String lastName, String email) {
    Owner owner = new Owner();
    owner.firstName = Validator.notEmpty(firstName, "First name");
    owner.lastName = Validator.notEmpty(lastName, "Last name");
    owner.email = Validator.email(email);
    owner.ownedAquariums = new HashSet<>();
    owner.role = Role.OWNER;
    return owner;
  }

  public static Owner create(String firstName, String lastName, String email, String password) {
    Owner owner = create(firstName, lastName, email);
    owner.setHashedPassword(password);
    return owner;
  }

  void setAquariumManager(AquariumManager aquariumManager) {
    this.aquariumManager = aquariumManager;
  }

  private void setHashedPassword(String password) {
    Validator.notEmpty(password, "Password");
    this.password = BCrypt.hashpw(password, BCrypt.gensalt());
  }

  public void updatePassword(String password) {
    setHashedPassword(password);
  }

  public void updateEmail(String email) {
    this.email = Validator.email(email);
  }

  public void updateRole(String roleName) {
    if (roleName == null) {
      throw new IllegalArgumentException("Role name cannot be null");
    }
    this.role = Role.valueOf(roleName.toUpperCase());
  }

  public void updateLastLogin() {
    this.lastLogin = LocalDateTime.now();
  }

  public void addToAquariums(Aquarium aquarium) {
    Validator.notNull(aquarium, "Aquarium");

    if (ownedAquariums.contains(aquarium)) {
      return;
    }

    this.ownedAquariums.add(aquarium);
    if (aquarium.getOwner() != this) {
      aquarium.assignToOwner(this);
    }
  }

  public void removeFromAquariums(Aquarium aquarium) {
    Validator.notNull(aquarium, "Aquarium");

    if (this.ownedAquariums.contains(aquarium)) {
      this.ownedAquariums.remove(aquarium);

      if (aquarium.getOwner() == this) {
        aquarium.unassignFromOwner();
      }
    }
  }

  public void assignToManager(AquariumManager manager) {
    Validator.notNull(manager, "Aquarium Manager");

    unassignFromManager();

    this.aquariumManager = manager;
    if (!manager.getOwners().contains(this)) {
      manager.addToOwners(this);
    }
  }

  public void unassignFromManager() {
    if (this.aquariumManager != null) {
      AquariumManager currentManager = this.aquariumManager;
      this.aquariumManager = null;
      currentManager.removeFromOwners(this);
    }
  }
}
