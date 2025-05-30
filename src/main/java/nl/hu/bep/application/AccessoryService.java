package nl.hu.bep.application;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.application.exception.ApplicationException;
import nl.hu.bep.application.mapper.EntityMappingService;
import nl.hu.bep.data.AccessoryRepository;
import nl.hu.bep.data.AquariumRepository;
import nl.hu.bep.domain.Accessory;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.presentation.dto.AccessoryRequest;
import nl.hu.bep.presentation.dto.AccessoryResponse;
import nl.hu.bep.presentation.dto.AquariumResponse;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class AccessoryService {
  private final AccessoryRepository accessoryRepository;
  private final AquariumRepository aquariumRepository;
  private final EntityMappingService mappingService;

  @Inject
  public AccessoryService(
      AccessoryRepository accessoryRepository,
      AquariumRepository aquariumRepository,
      EntityMappingService mappingService) {
    this.accessoryRepository = accessoryRepository;
    this.aquariumRepository = aquariumRepository;
    this.mappingService = mappingService;
  }

  public List<AccessoryResponse> getAllAccessories(Long ownerId) {
    log.info("Service: Fetching accessories for ownerId: {}", ownerId);
    return accessoryRepository.findByOwnerId(ownerId).stream()
        .map(mappingService::mapAccessory)
        .collect(Collectors.toList());
  }

  public AccessoryResponse getAccessoryById(Long id) {
    return accessoryRepository.findById(id)
        .map(mappingService::mapAccessory)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", id));
  }

  public List<AccessoryResponse> getAccessoriesByAquarium(Long aquariumId) {
    Aquarium aquarium = findAquariumWithAccessories(aquariumId);

    return aquarium.getAccessories().stream()
        .map(mappingService::mapAccessory)
        .collect(Collectors.toList());
  }

  public AccessoryResponse createAccessory(AccessoryRequest request, Long ownerId) {
    // Validate basic requirements
    if (request.model() == null || request.model().trim().isEmpty()) {
      throw new ApplicationException.BadRequestException("Accessory model cannot be empty");
    }
    
    if (request.serialNumber() == null || request.serialNumber().trim().isEmpty()) {
      throw new ApplicationException.BadRequestException("Accessory serial number cannot be empty");
    }
    
    if (request.type() == null || request.type().trim().isEmpty()) {
      throw new ApplicationException.BadRequestException("Accessory type cannot be empty");
    }

    Aquarium aquarium = null;
    if (request.aquariumId() != null) {
      aquarium = findAquariumWithAccessories(request.aquariumId());
      aquarium.verifyOwnership(ownerId);
    }

    Accessory accessory;
    try {
      accessory = Accessory.createFromType(
          request.type(),
          request.model(),
          request.serialNumber(),
          request.getIsExternalValue(),
          request.getCapacityInLitersValue(),
          request.getIsLEDValue(),
          request.getTimeOnValue(),
          request.getTimeOffValue(),
          request.getMinTemperatureValue(),
          request.getMaxTemperatureValue(),
          request.getCurrentTemperatureValue(),
          ownerId,
          request.color(),
          request.description());
    } catch (IllegalArgumentException e) {
      throw new ApplicationException.BadRequestException("Invalid accessory data: " + e.getMessage(), e);
    } catch (Exception e) {
      log.error("Error creating accessory: {}", e.getMessage(), e);
      throw new ApplicationException.BadRequestException("Failed to create accessory: " + e.getMessage(), e);
    }

    try {
      accessory = accessoryRepository.save(accessory);
      log.info("Saved accessory with ownerId: {}", accessory.getOwnerId());
    } catch (Exception e) {
      log.error("Database error saving accessory: {}", e.getMessage(), e);
      throw new ApplicationException.BadRequestException("Failed to save accessory to database: " + e.getMessage(), e);
    }

    if (aquarium != null) {
      try {
        log.info("Adding accessory {} to aquarium on creation: {}", accessory, aquarium);
        aquarium.addToAccessories(accessory);
        aquariumRepository.save(aquarium);
        accessory = findAccessory(accessory.getId());
      } catch (Exception e) {
        log.error("Error adding accessory to aquarium during creation: {}", e.getMessage(), e);
        // Don't fail the entire operation, but log the issue
        log.warn("Accessory was created but could not be added to aquarium");
      }
    }

    // Handle potential lazy loading issues when mapping
    try {
      return mappingService.mapAccessory(accessory);
    } catch (Exception e) {
      log.error("Error mapping accessory, creating fallback response: {}", e.getMessage(), e);
      
      // Fallback: create response directly to avoid lazy loading issues
      return createAccessoryResponseFallback(accessory, request);
    }
  }

  private AccessoryResponse createAccessoryResponseFallback(Accessory accessory, AccessoryRequest request) {
    // Create response without accessing any potentially lazy relationships
    return new AccessoryResponse(
        accessory.getId(),
        accessory.getModel(),
        accessory.getSerialNumber(),
        accessory.getClass().getSimpleName(),
        request.getIsExternalValue(),
        request.getCapacityInLitersValue(),
        request.getIsLEDValue(),
        request.getTimeOnValue(),
        request.getTimeOffValue(),
        request.getMinTemperatureValue(),
        request.getMaxTemperatureValue(),
        request.getCurrentTemperatureValue(),
        accessory.getColor(),
        accessory.getDescription(),
        accessory.getDateCreated());
  }

  public AccessoryResponse updateAccessory(Long id, AccessoryRequest request) {
    Accessory existingAccessory = findAccessory(id);

    Aquarium oldAquarium = existingAccessory.getAquarium() != null
        ? findAquariumWithAccessories(existingAccessory.getAquarium().getId())
        : null;
    Aquarium newAquarium = request.aquariumId() != null ? findAquariumWithAccessories(request.aquariumId()) : null;

    if (oldAquarium != null && (newAquarium == null || !oldAquarium.getId().equals(newAquarium.getId()))) {
      log.info("Removing accessory {} from old aquarium: {}", id, oldAquarium.getId());
      oldAquarium.removeFromAccessories(existingAccessory);
      aquariumRepository.save(oldAquarium);
    }

    existingAccessory.update(request.model(), request.serialNumber(), request.color(), request.description());

    String type = existingAccessory.getClass().getSimpleName();
    switch (type) {
      case "Filter" -> {
        nl.hu.bep.domain.accessories.Filter filter = (nl.hu.bep.domain.accessories.Filter) existingAccessory;
        filter.updateProperties(request.getIsExternalValue(), request.getCapacityInLitersValue());
      }
      case "Lighting" -> {
        nl.hu.bep.domain.accessories.Lighting lighting = (nl.hu.bep.domain.accessories.Lighting) existingAccessory;
        lighting.updateProperties(request.getIsLEDValue(), request.getTimeOffValue(), request.getTimeOnValue());
      }
      case "Thermostat" -> {
        nl.hu.bep.domain.accessories.Thermostat thermostat = (nl.hu.bep.domain.accessories.Thermostat) existingAccessory;
        thermostat.updateProperties(request.getMinTemperatureValue(), request.getMaxTemperatureValue(),
            request.getCurrentTemperatureValue());
      }
    }

    existingAccessory = accessoryRepository.save(existingAccessory);

    if (newAquarium != null && (oldAquarium == null || !oldAquarium.getId().equals(newAquarium.getId()))) {
      log.info("Adding accessory {} to new aquarium: {}", id, newAquarium.getId());
      newAquarium.addToAccessories(existingAccessory);
      aquariumRepository.save(newAquarium);
      existingAccessory = findAccessory(existingAccessory.getId());
    }

    return mappingService.mapAccessory(existingAccessory);
  }

  public void deleteAccessory(Long id) {
    if (!accessoryRepository.existsById(id)) {
      throw new ApplicationException.NotFoundException("Accessory", id);
    }

    Accessory accessory = findAccessory(id);
    if (accessory.getAquarium() != null) {
      Aquarium aquarium = findAquariumWithAccessories(accessory.getAquarium().getId());
      aquarium.removeFromAccessories(accessory);
      aquariumRepository.save(aquarium);
    }

    accessoryRepository.deleteById(id);
  }

  public AquariumResponse addAccessory(Long aquariumId, Long accessoryId, Long ownerId) {
    Aquarium aquarium = aquariumRepository.findByIdWithRelationships(aquariumId, "accessories")
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
    Accessory accessory = accessoryRepository.findById(accessoryId)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));

    aquarium.verifyOwnership(ownerId);
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
    Accessory accessory = accessoryRepository.findById(accessoryId)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));

    aquarium.verifyOwnership(ownerId);
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

  public AquariumResponse addAccessoryToAquarium(Long aquariumId, Long accessoryId) {
    return addAccessory(aquariumId, accessoryId, null);
  }

  public AquariumResponse removeAccessoryFromAquarium(Long aquariumId, Long accessoryId) {
    return removeAccessory(aquariumId, accessoryId, null);
  }

  private Accessory findAccessory(Long id) {
    return accessoryRepository.findById(id)
        .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", id));
  }

  private Aquarium findAquariumWithAccessories(Long id) {
    return aquariumRepository.findByIdWithRelationships(id, "accessories")
        .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));
  }
}