package nl.hu.bep.application.service;

import nl.hu.bep.data.interfaces.AccessoryRepository;
import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.domain.Accessory;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.dto.request.AccessoryRequest;
import nl.hu.bep.presentation.dto.response.AccessoryResponse;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service focused solely on accessory-related operations.
 * Follows Single Responsibility Principle.
 */
@Slf4j
public class AccessoryService {

    private final AccessoryRepository accessoryRepository;
    private final AquariumRepository aquariumRepository;
    private final EntityMapper entityMapper;

    @Inject
    public AccessoryService(AccessoryRepository accessoryRepository,
                           AquariumRepository aquariumRepository,
                           EntityMapper entityMapper) {
        this.accessoryRepository = accessoryRepository;
        this.aquariumRepository = aquariumRepository;
        this.entityMapper = entityMapper;
    }

    public List<AccessoryResponse> getAllAccessories(Long ownerId) {
        return accessoryRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToAccessoryResponse)
                .collect(Collectors.toList());
    }

    public AccessoryResponse getAccessory(Long accessoryId, Long requestingOwnerId) {
        Accessory accessory = findOwnedAccessory(accessoryId, requestingOwnerId);
        return entityMapper.mapToAccessoryResponse(accessory);
    }

    @Transactional
    public AccessoryResponse createAccessory(AccessoryRequest request, Long ownerId) {
        validateAquariumAssignment(request.aquariumId(), ownerId);

        Accessory accessory = Accessory.createFromType(
                request.type(),
                request.model(),
                request.serialNumber(),
                request.getIsExternalValue(),
                request.getCapacityLitersValue(),
                request.getIsLEDValue(),
                request.getTimeOnValue(),
                request.getTimeOffValue(),
                request.getMinTemperatureValue(),
                request.getMaxTemperatureValue(),
                request.getCurrentTemperatureValue(),
                ownerId,
                request.getColorValue(),
                request.getDescriptionValue()
        );

        if (request.aquariumId() != null) {
            accessory.assignToAquarium(request.aquariumId(), ownerId);
        }

        Accessory savedAccessory = accessoryRepository.insert(accessory);
        return entityMapper.mapToAccessoryResponse(savedAccessory);
    }

    @Transactional
    public AccessoryResponse updateAccessory(Long accessoryId, AccessoryRequest request, Long requestingOwnerId) {
        Accessory accessory = findOwnedAccessory(accessoryId, requestingOwnerId);

        accessory.update(
                request.model(),
                request.serialNumber(),
                request.getColorValue(),
                request.getDescriptionValue()
        );

        if (request.aquariumId() != null) {
            validateAquariumAssignment(request.aquariumId(), requestingOwnerId);
            accessory.assignToAquarium(request.aquariumId(), requestingOwnerId);
        } else {
            accessory.removeFromAquarium(requestingOwnerId);
        }

        Accessory updatedAccessory = accessoryRepository.update(accessory);
        return entityMapper.mapToAccessoryResponse(updatedAccessory);
    }

    @Transactional
    public void deleteAccessory(Long accessoryId, Long requestingOwnerId) {
        findOwnedAccessory(accessoryId, requestingOwnerId); // Validates ownership
        accessoryRepository.deleteById(accessoryId);
        log.info("Accessory {} deleted by owner {}", accessoryId, requestingOwnerId);
    }

    private Accessory findOwnedAccessory(Long accessoryId, Long requestingOwnerId) {
        Accessory accessory = accessoryRepository.findById(accessoryId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));
        accessory.validateOwnership(requestingOwnerId);
        return accessory;
    }

    public List<AccessoryResponse> getAccessoriesByAquarium(Long aquariumId) {
        return accessoryRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToAccessoryResponse)
                .collect(Collectors.toList());
    }

    private void validateAquariumAssignment(Long aquariumId, Long ownerId) {
        if (aquariumId != null) {
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
            aquarium.validateOwnership(ownerId);
        }
    }
}
