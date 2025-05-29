package nl.hu.bep.application;

import nl.hu.bep.presentation.dto.AquariumRequest;
import nl.hu.bep.presentation.dto.AquariumResponse;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.Accessory;
import nl.hu.bep.domain.Ornament;
import nl.hu.bep.domain.Owner;
import nl.hu.bep.data.AccessoryRepository;
import nl.hu.bep.data.AquariumManagerRepository;
import nl.hu.bep.data.AquariumRepository;
import nl.hu.bep.data.OrnamentRepository;
import nl.hu.bep.data.OwnerRepository;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.application.exception.ApplicationException;
import nl.hu.bep.application.mapper.EntityMappingService;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class AquariumService {
  private final AquariumRepository aquariumRepository;
  private final AccessoryRepository accessoryRepository;
  private final OrnamentRepository ornamentRepository;
  private final OwnerRepository ownerRepository;
  // private final AquariumManagerRepository aquariumManagerRepository;
  private final EntityMappingService mappingService;

  @Inject
  public AquariumService(
      AquariumRepository aquariumRepository,
      AccessoryRepository accessoryRepository,
      OrnamentRepository ornamentRepository,
      OwnerRepository ownerRepository,
      AquariumManagerRepository aquariumManagerRepository,
      EntityMappingService mappingService) {
    this.aquariumRepository = aquariumRepository;
    this.accessoryRepository = accessoryRepository;
    this.ornamentRepository = ornamentRepository;
    this.ownerRepository = ownerRepository;
    // this.aquariumManagerRepository = aquariumManagerRepository;
    this.mappingService = mappingService;
  }

  public List<AquariumResponse> getAllAquariums(Long ownerId) {
    return aquariumRepository.findByOwnerIdWithCollections(ownerId).stream()
        .map(mappingService::mapAquarium)
        .collect(Collectors.toList());
  }

  public AquariumResponse getAquariumById(Long id) {
    return aquariumRepository.findByIdWithAllCollections(id)
        .map(mappingService::mapAquariumDetailed)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));
  }

  public AquariumResponse getAquariumDetailById(Long id) {
    return aquariumRepository.findByIdWithInhabitants(id)
        .map(mappingService::mapAquariumDetailed)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));
  }

  public AquariumResponse createAquarium(AquariumRequest request, Long ownerId) {
    Validator.notNull(request, "Request");

    Aquarium aquarium = Aquarium.create(
        request.name(),
        request.length(),
        request.width(),
        request.height(),
        request.substrate(),
        request.waterType(),
        request.color(),
        request.description(),
        request.state()
    );

    if (ownerId != null) {
      Owner owner = findOwner(ownerId);
      aquarium.assignToOwner(owner);
    }

    Aquarium savedAquarium = aquariumRepository.save(aquarium);
    
    // Fetch the aquarium with all required relationships to avoid LazyInitializationException
    Aquarium aquariumWithAllData = aquariumRepository.findByIdWithAllCollections(savedAquarium.getId())
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", savedAquarium.getId()));
    
    return mappingService.mapAquarium(aquariumWithAllData);
  }

  public AquariumResponse updateAquarium(Long id, AquariumRequest request) {
    Aquarium existingAquarium = findAquarium(id);

    existingAquarium.update(
        request.name(),
        request.length(),
        request.width(),
        request.height(),
        request.substrate(),
        request.waterType(),
        request.state(),
        null);

    aquariumRepository.save(existingAquarium);

    Aquarium updatedAquarium = aquariumRepository.findByIdWithAllCollections(id)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));

    return mappingService.mapAquariumDetailed(updatedAquarium);
  }

  public void deleteAquarium(Long id) {
    if (!aquariumRepository.existsById(id)) {
      throw new ApplicationException.NotFoundException("Aquarium", id);
    }
    aquariumRepository.deleteById(id);
  }

  public AquariumResponse addAccessory(Long aquariumId, Long accessoryId, Map<String, Object> properties,
      Long ownerId) {
    Aquarium aquarium = aquariumRepository.findByIdWithRelationships(aquariumId, "accessories")
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

    aquarium.verifyOwnership(ownerId);

    Accessory accessory = findAccessory(accessoryId);
    log.info("Adding accessory {} to aquarium: {}", accessory, aquarium);

    try {
      aquarium.addToAccessories(accessory);
      accessory = accessoryRepository.save(accessory);
      aquarium = aquariumRepository.save(aquarium);

      Aquarium updatedAquarium = aquariumRepository.findByIdWithAllCollections(aquariumId)
          .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

      return mappingService.mapAquariumDetailed(updatedAquarium);
    } catch (Exception e) {
      log.error("Error adding accessory to aquarium", e);
      throw new ApplicationException.BadRequestException(
          "Failed to add accessory to aquarium: " + e.getMessage(), e);
    }
  }

  public AquariumResponse removeAccessory(Long aquariumId, Long accessoryId, Long ownerId) {
    Aquarium aquarium = aquariumRepository.findByIdWithRelationships(aquariumId, "accessories")
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

    aquarium.verifyOwnership(ownerId);

    Accessory accessory = findAccessory(accessoryId);
    log.info("Removing accessory {} from aquarium: {}", accessory, aquarium);

    try {
      aquarium.removeFromAccessories(accessory);
      accessory = accessoryRepository.save(accessory);
      aquarium = aquariumRepository.save(aquarium);

      Aquarium updatedAquarium = aquariumRepository.findByIdWithAllCollections(aquariumId)
          .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

      return mappingService.mapAquariumDetailed(updatedAquarium);
    } catch (Exception e) {
      log.error("Error removing accessory from aquarium", e);
      throw new ApplicationException.BadRequestException(
          "Failed to remove accessory from aquarium: " + e.getMessage(), e);
    }
  }

  public AquariumResponse addOrnament(Long aquariumId, Long ornamentId, Map<String, Object> properties, Long ownerId) {
    Aquarium aquarium = aquariumRepository.findByIdWithRelationships(aquariumId, "ornaments")
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

    aquarium.verifyOwnership(ownerId);

    Ornament ornament = findOrnament(ornamentId);
    log.info("Adding ornament {} to aquarium: {}", ornament, aquarium);

    try {
      aquarium.addToOrnaments(ornament);
      ornament = ornamentRepository.save(ornament);
      aquarium = aquariumRepository.save(aquarium);

      Aquarium updatedAquarium = aquariumRepository.findByIdWithAllCollections(aquariumId)
          .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

      return mappingService.mapAquariumDetailed(updatedAquarium);
    } catch (Exception e) {
      log.error("Error adding ornament to aquarium", e);
      throw new ApplicationException.BadRequestException(
          "Failed to add ornament to aquarium: " + e.getMessage(), e);
    }
  }

  public AquariumResponse removeOrnament(Long aquariumId, Long ornamentId, Long ownerId) {
    Aquarium aquarium = aquariumRepository.findByIdWithRelationships(aquariumId, "ornaments")
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

    aquarium.verifyOwnership(ownerId);

    Ornament ornament = findOrnament(ornamentId);
    log.info("Removing ornament {} from aquarium: {}", ornament, aquarium);

    try {
      aquarium.removeFromOrnaments(ornament);
      ornament = ornamentRepository.save(ornament);
      aquarium = aquariumRepository.save(aquarium);

      Aquarium updatedAquarium = aquariumRepository.findByIdWithAllCollections(aquariumId)
          .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

      return mappingService.mapAquariumDetailed(updatedAquarium);
    } catch (Exception e) {
      log.error("Error removing ornament from aquarium", e);
      throw new ApplicationException.BadRequestException(
          "Failed to remove ornament from aquarium: " + e.getMessage(), e);
    }
  }

  public Optional<Owner> findOwnerByEmail(String email) {
    return ownerRepository.findByEmail(email);
  }

  private Aquarium findAquarium(Long id) {
    return aquariumRepository.findById(id)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));
  }

  private Accessory findAccessory(Long id) {
    return accessoryRepository.findById(id)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", id));
  }

  private Ornament findOrnament(Long id) {
    return ornamentRepository.findById(id)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", id));
  }

  private Owner findOwner(Long id) {
    return ownerRepository.findByIdWithAquariums(id)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Owner", id));
  }

  // private Aquarium findAquariumWithInhabitants(Long id) {
  // return aquariumRepository.findByIdWithInhabitants(id)
  // .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium",
  // id));
  // }

  // private Aquarium findAquariumWithAccessories(Long id) {
  //   return aquariumRepository.findByIdWithAccessories(id)
  //       .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));
  // }

  // private Aquarium findAquariumWithOrnaments(Long id) {
  //   return aquariumRepository.findByIdWithOrnaments(id)
  //       .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));
  // }
}
