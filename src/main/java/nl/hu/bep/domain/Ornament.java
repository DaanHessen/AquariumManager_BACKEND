package nl.hu.bep.domain;

import jakarta.persistence.*;
import lombok.*;
import nl.hu.bep.domain.utils.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ornaments")
@Getter
@EqualsAndHashCode(exclude = { "aquarium" })
@ToString(exclude = { "aquarium" })
@NoArgsConstructor
@AllArgsConstructor
public class Ornament {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "name")
  private String name;

  @Column(name = "description", length = 255)
  private String description;

  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "color")
  private String color;

  @NotNull
  @Column(name = "supports_air_pump")
  private boolean isAirPumpCompatible;

  @NotNull
  @Column(name = "owner_id")
  private Long ownerId;

  @Column(name = "date_created", updatable = false)
  private java.time.LocalDateTime dateCreated;

  @Column(name = "material")
  private String material;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aquarium_id")
  private Aquarium aquarium;

  public Ornament(String name, String description, String color, Boolean isAirPumpCompatible, Long ownerId, String material) {
    this.name = Validator.notEmpty(name, "Ornament name");
    this.description = description;
    this.color = Validator.notEmpty(color, "Ornament color");
    this.isAirPumpCompatible = isAirPumpCompatible != null && isAirPumpCompatible;
    this.ownerId = Validator.notNull(ownerId, "Owner ID");
    this.material = material;
    this.dateCreated = java.time.LocalDateTime.now();
  }

  void setAquarium(Aquarium aquarium) {
    this.aquarium = aquarium;
  }

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

  public Ornament update(String name, String description, String color, Boolean isAirPumpCompatible, String material) {
    if (name != null) {
      updateName(name);
    }

    if (description != null) {
      updateDescription(description);
    }

    if (color != null) {
      updateColor(color);
    }

    if (isAirPumpCompatible != null) {
      updateAirPumpCompatibility(isAirPumpCompatible);
    }

    if (material != null) {
      updateMaterial(material);
    }

    return this;
  }
}
