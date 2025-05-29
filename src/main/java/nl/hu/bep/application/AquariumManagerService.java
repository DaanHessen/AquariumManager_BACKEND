package nl.hu.bep.application;

import nl.hu.bep.presentation.dto.*;
import nl.hu.bep.domain.*;
import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.data.*;
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
public class AquariumManagerService {
    
    private final AquariumRepository aquariumRepository;
    private final AccessoryRepository accessoryRepository;
    private final OrnamentRepository ornamentRepository;
    private final InhabitantRepository inhabitantRepository;
    private final AquariumStateHistoryRepository stateHistoryRepository;
    private final OwnerRepository ownerRepository;
    private final EntityMappingService mappingService;

    @Inject
    public AquariumManagerService(
            AquariumRepository aquariumRepository,
            AccessoryRepository accessoryRepository,
            OrnamentRepository ornamentRepository,
            InhabitantRepository inhabitantRepository,
            AquariumStateHistoryRepository stateHistoryRepository,
            OwnerRepository ownerRepository,
            EntityMappingService mappingService) {
        this.aquariumRepository = aquariumRepository;
        this.accessoryRepository = accessoryRepository;
        this.ornamentRepository = ornamentRepository;
        this.inhabitantRepository = inhabitantRepository;
        this.stateHistoryRepository = stateHistoryRepository;
        this.ownerRepository = ownerRepository;
        this.mappingService = mappingService;
    }

    // ========== AQUARIUM OPERATIONS ==========

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

    // ========== STATE HISTORY OPERATIONS ==========

    public List<AquariumStateHistoryResponse> getAquariumStateHistory(Long aquariumId) {
        if (!aquariumRepository.existsById(aquariumId)) {
            throw new ApplicationException.NotFoundException("Aquarium", aquariumId);
        }
        
        return stateHistoryRepository.findByAquariumIdOrderByStartTime(aquariumId).stream()
                .map(mappingService::mapStateHistory)
                .collect(Collectors.toList());
    }

    public Long getCurrentStateDuration(Long aquariumId) {
        Aquarium aquarium = findAquarium(aquariumId);
        return aquarium.getCurrentStateDurationMinutes();
    }

    // ========== ACCESSORY OPERATIONS ==========

    public List<AccessoryResponse> getAllAccessories(Long ownerId) {
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
        return accessoryRepository.findByAquariumId(aquariumId).stream()
                .map(mappingService::mapAccessory)
                .collect(Collectors.toList());
    }

    public AccessoryResponse createAccessory(AccessoryRequest request, Long ownerId) {
        Validator.notNull(request, "Request");
        
        // Create accessory logic here - simplified for now
        // This would need to be implemented based on the actual AccessoryRequest structure
        throw new UnsupportedOperationException("Accessory creation not yet implemented in consolidated service");
    }

    public AccessoryResponse updateAccessory(Long id, AccessoryRequest request) {
        // Update accessory logic here
        throw new UnsupportedOperationException("Accessory update not yet implemented in consolidated service");
    }

    public void deleteAccessory(Long id) {
        if (!accessoryRepository.existsById(id)) {
            throw new ApplicationException.NotFoundException("Accessory", id);
        }
        accessoryRepository.deleteById(id);
    }

    public AquariumResponse addAccessoryToAquarium(Long aquariumId, Long accessoryId, Map<String, Object> properties, Long ownerId) {
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

    public AquariumResponse removeAccessoryFromAquarium(Long aquariumId, Long accessoryId, Long ownerId) {
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

    // ========== ORNAMENT OPERATIONS ==========

    public List<OrnamentResponse> getAllOrnaments(Long ownerId) {
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
        return ornamentRepository.findByAquariumId(aquariumId).stream()
                .map(mappingService::mapOrnament)
                .collect(Collectors.toList());
    }

    public AquariumResponse addOrnamentToAquarium(Long aquariumId, Long ornamentId, Map<String, Object> properties, Long ownerId) {
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

    public AquariumResponse removeOrnamentFromAquarium(Long aquariumId, Long ornamentId, Long ownerId) {
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

    // ========== INHABITANT OPERATIONS ==========

    public List<InhabitantResponse> getAllInhabitants(Long ownerId) {
        return inhabitantRepository.findByOwnerId(ownerId).stream()
                .map(mappingService::mapInhabitant)
                .collect(Collectors.toList());
    }

    public InhabitantResponse getInhabitantById(Long id) {
        return inhabitantRepository.findById(id)
                .map(mappingService::mapInhabitant)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", id));
    }

    public List<InhabitantResponse> getInhabitantsByAquarium(Long aquariumId) {
        return inhabitantRepository.findByAquariumId(aquariumId).stream()
                .map(mappingService::mapInhabitant)
                .collect(Collectors.toList());
    }

    // ========== HELPER METHODS ==========

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

    private Inhabitant findInhabitant(Long id) {
        return inhabitantRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", id));
    }

    private Owner findOwner(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Owner", id));
    }

    public Optional<Owner> findOwnerByEmail(String email) {
        return ownerRepository.findByEmail(email);
    }
} 