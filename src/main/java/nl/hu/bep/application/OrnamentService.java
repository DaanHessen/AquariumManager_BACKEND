package nl.hu.bep.application;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.application.exception.ApplicationException;
import nl.hu.bep.application.mapper.EntityMappingService;
import nl.hu.bep.data.AquariumRepository;
import nl.hu.bep.data.OrnamentRepository;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.Ornament;
import nl.hu.bep.presentation.dto.OrnamentRequest;
import nl.hu.bep.presentation.dto.OrnamentResponse;
import nl.hu.bep.presentation.dto.AquariumResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class OrnamentService {
  private final OrnamentRepository ornamentRepository;
  private final AquariumRepository aquariumRepository;
  private final EntityMappingService mappingService;

  @Inject
  public OrnamentService(
      OrnamentRepository ornamentRepository,
      AquariumRepository aquariumRepository,
      EntityMappingService mappingService) {
    this.ornamentRepository = ornamentRepository;
    this.aquariumRepository = aquariumRepository;
    this.mappingService = mappingService;
  }

  public List<OrnamentResponse> getAllOrnaments(Long ownerId) {
    log.info("Service: Fetching ornaments for ownerId: {}", ownerId);
    return ornamentRepository.findByOwnerId(ownerId).stream()
        .map(mappingService::mapOrnament)
        .collect(Collectors.toList());
  }

  public OrnamentResponse getOrnamentById(Long id) {
    return ornamentRepository.findById(id)
        .map(mappingService::mapOrnament)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", id));
  }

  public List<OrnamentResponse> getOrnamentsByAquarium(Long aquariumId) {
    Aquarium aquarium = findAquariumWithOrnaments(aquariumId);

    return aquarium.getOrnaments().stream()
        .map(mappingService::mapOrnament)
        .collect(Collectors.toList());
  }

  public OrnamentResponse createOrnament(OrnamentRequest request, Long ownerId) {
    Aquarium aquarium = null;
    if (request.aquariumId() != null) {
      aquarium = findAquariumWithOrnaments(request.aquariumId());
      aquarium.verifyOwnership(ownerId);
    }

    Ornament ornament = new Ornament(
        request.name(),
        request.description(),
        request.color(),
        request.getSupportsAirPumpValue(),
        ownerId,
        request.material());

    ornament = ornamentRepository.save(ornament);
    log.info("Saved ornament with ownerId: {}", ornament.getOwnerId());

    if (aquarium != null) {
      aquarium.addToOrnaments(ornament);
      aquariumRepository.save(aquarium);
      ornament = findOrnament(ornament.getId());
    }

    return mappingService.mapOrnament(ornament);
  }

  public OrnamentResponse updateOrnament(Long id, OrnamentRequest request) {
    Ornament existingOrnament = findOrnament(id);

    Aquarium oldAquarium = existingOrnament.getAquarium() != null
        ? findAquariumWithOrnaments(existingOrnament.getAquarium().getId())
        : null;
    Aquarium newAquarium = request.aquariumId() != null ? findAquariumWithOrnaments(request.aquariumId()) : null;

    if (oldAquarium != null && (newAquarium == null || !oldAquarium.getId().equals(newAquarium.getId()))) {
      log.info("Removing ornament {} from old aquarium: {}", id, oldAquarium.getId());
      oldAquarium.removeFromOrnaments(existingOrnament);
      aquariumRepository.save(oldAquarium);
    }

    existingOrnament.update(
        request.name(),
        request.description(),
        request.color(),
        request.supportsAirPump(),
        request.material());

    existingOrnament = ornamentRepository.save(existingOrnament);

    if (newAquarium != null && (oldAquarium == null || !oldAquarium.getId().equals(newAquarium.getId()))) {
      log.info("Adding ornament {} to new aquarium: {}", id, newAquarium.getId());
      newAquarium.addToOrnaments(existingOrnament);
      aquariumRepository.save(newAquarium);
      existingOrnament = findOrnament(existingOrnament.getId());
    }

    return mappingService.mapOrnament(existingOrnament);
  }

  public void deleteOrnament(Long id) {
    if (!ornamentRepository.existsById(id)) {
      throw new ApplicationException.NotFoundException("Ornament", id);
    }

    Ornament ornament = findOrnament(id);
    if (ornament.getAquarium() != null) {
      Aquarium aquarium = findAquariumWithOrnaments(ornament.getAquarium().getId());
      aquarium.removeFromOrnaments(ornament);
      aquariumRepository.save(aquarium);
    }

    ornamentRepository.deleteById(id);
  }

  public AquariumResponse addOrnament(Long aquariumId, Long ornamentId, Long ownerId, Map<String, Object> properties) {
    Aquarium aquarium = aquariumRepository.findByIdWithOrnaments(aquariumId)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
    Ornament ornament = ornamentRepository.findById(ornamentId)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));

    aquarium.verifyOwnership(ownerId);
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
    Aquarium aquarium = aquariumRepository.findByIdWithOrnaments(aquariumId)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
    Ornament ornament = ornamentRepository.findById(ornamentId)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));

    aquarium.verifyOwnership(ownerId);
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

  private Ornament findOrnament(Long id) {
    return ornamentRepository.findById(id)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", id));
  }

  private Aquarium findAquariumWithOrnaments(Long id) {
    return aquariumRepository.findByIdWithOrnaments(id)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));
  }
}