package nl.hu.bep.application;

import nl.hu.bep.data.*;
import nl.hu.bep.domain.*;
import nl.hu.bep.domain.services.OwnershipDomainService;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.*;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CONSOLIDATED APPLICATION SERVICE - DDD compliant.
 * THIN ORCHESTRATION ONLY - no business logic, no DTO mapping.
 * 
 * Business logic → Domain objects & Domain services
 * DTO mapping → EntityMapper
 * Security rules → Domain services
 */
@ApplicationScoped
@Transactional
public class AquariumManagerService {
    
    @Inject
    private AquariumRepository aquariumRepository;
    
    @Inject
    private AccessoryRepository accessoryRepository;
    
    @Inject
    private OrnamentRepository ornamentRepository;
    
    @Inject
    private InhabitantRepository inhabitantRepository;
    
    @Inject
    private OwnerRepository ownerRepository;
    
    @Inject
    private EntityMapper entityMapper;

    // ========== AQUARIUM OPERATIONS - PURE ORCHESTRATION ==========

    public List<AquariumResponse> getAllAquariums(Long ownerId) {
        return aquariumRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToAquariumResponse)
                .collect(Collectors.toList());
    }

    public AquariumResponse getAquarium(Long aquariumId, Long requestingOwnerId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        
        // Domain service handles security - no business logic in service
        OwnershipDomainService.verifyOwnership(aquarium, requestingOwnerId);
        
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    public AquariumResponse getAquariumById(Long id) {
        Aquarium aquarium = aquariumRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    public AquariumResponse getAquariumDetailById(Long id) {
        Aquarium aquarium = aquariumRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));
        
        // Get all related entities for detailed view
        List<AccessoryResponse> accessories = getAccessoriesByAquarium(id);
        List<OrnamentResponse> ornaments = getOrnamentsByAquarium(id);
        List<InhabitantResponse> inhabitants = getInhabitantsByAquarium(id);
        
        // Use enhanced mapper for detailed response with all relationships
        return entityMapper.mapToDetailedAquariumResponse(aquarium, inhabitants, accessories, ornaments);
    }

    public AquariumResponse createAquarium(AquariumRequest request, Long ownerId) {
        // Domain factory handles validation - no business logic here!
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

        // Domain method handles assignment
        if (ownerId != null) {
            aquarium.assignToOwner(ownerId);
        }

        Aquarium savedAquarium = aquariumRepository.insert(aquarium);
        return entityMapper.mapToAquariumResponse(savedAquarium);
    }

    public AquariumResponse updateAquarium(Long id, AquariumRequest request) {
        Aquarium existingAquarium = aquariumRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));

        // Domain method handles validation and update
        existingAquarium.update(
                request.name(),
                request.length(),
                request.width(),
                request.height(),
                request.substrate(),
                request.waterType(),
                request.state(),
                null);

        Aquarium updatedAquarium = aquariumRepository.update(existingAquarium);
        return entityMapper.mapToAquariumResponse(updatedAquarium);
    }

    public void deleteAquarium(Long id) {
        aquariumRepository.deleteById(id);
    }

    // ========== ACCESSORY OPERATIONS - PURE ORCHESTRATION ==========

    public List<AccessoryResponse> getAllAccessories(Long ownerId) {
        return accessoryRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToAccessoryResponse)
                .collect(Collectors.toList());
    }

    public AccessoryResponse getAccessoryById(Long id) {
        return accessoryRepository.findById(id)
                .map(entityMapper::mapToAccessoryResponse)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", id));
    }

    public AccessoryResponse createAccessory(AccessoryRequest request, Long ownerId) {
        // Create accessory using domain factory
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

        // Assign to aquarium if specified
        if (request.aquariumId() != null) {
            // Verify aquarium ownership first
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            OwnershipDomainService.verifyOwnership(aquarium, ownerId);
            
            // Assign accessory to aquarium
            accessory.assignToAquarium(request.aquariumId(), ownerId);
        }

        Accessory savedAccessory = accessoryRepository.insert(accessory);
        return entityMapper.mapToAccessoryResponse(savedAccessory);
    }

    public AccessoryResponse updateAccessory(Long id, AccessoryRequest request, Long ownerId) {
        // Get existing accessory and verify ownership
        Accessory accessory = accessoryRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", id));
        
        // Verify accessory ownership
        if (!accessory.getOwnerId().equals(ownerId)) {
            throw new ApplicationException.UnauthorizedException("Only the accessory owner can update it");
        }

        // Update accessory properties
        accessory.update(
                request.model(),
                request.serialNumber(),
                request.getColorValue(),
                request.getDescriptionValue()
        );

        // Handle aquarium assignment change
        if (request.aquariumId() != null && !request.aquariumId().equals(accessory.getAquariumId())) {
            // Verify new aquarium ownership
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            OwnershipDomainService.verifyOwnership(aquarium, ownerId);
            
            // Assign to new aquarium
            accessory.assignToAquarium(request.aquariumId(), ownerId);
        } else if (request.aquariumId() == null && accessory.getAquariumId() != null) {
            // Remove from current aquarium
            accessory.removeFromAquarium(ownerId);
        }

        Accessory updatedAccessory = accessoryRepository.update(accessory);
        return entityMapper.mapToAccessoryResponse(updatedAccessory);
    }

    public void deleteAccessory(Long id) {
        accessoryRepository.deleteById(id);
    }

    public List<AccessoryResponse> getAccessoriesByAquarium(Long aquariumId) {
        return accessoryRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToAccessoryResponse)
                .collect(Collectors.toList());
    }

    public AquariumResponse addAccessoryToAquarium(Long aquariumId, Long accessoryId, Map<String, Object> properties, Long ownerId) {
        // Verify aquarium ownership
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        OwnershipDomainService.verifyOwnership(aquarium, ownerId);
        
        // Get and verify accessory
        Accessory accessory = accessoryRepository.findById(accessoryId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));
        
        // Use domain method to securely assign accessory to aquarium
        accessory.assignToAquarium(aquariumId, ownerId);
        
        // Persist the assignment
        accessoryRepository.update(accessory);
        
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    public AquariumResponse removeAccessoryFromAquarium(Long aquariumId, Long accessoryId, Long ownerId) {
        // Verify aquarium ownership
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        OwnershipDomainService.verifyOwnership(aquarium, ownerId);
        
        // Get and verify accessory
        Accessory accessory = accessoryRepository.findById(accessoryId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));
        
        // Use domain method to securely remove accessory from aquarium
        accessory.removeFromAquarium(ownerId);
        
        // Persist the removal
        accessoryRepository.update(accessory);
        
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    // ========== ORNAMENT OPERATIONS - PURE ORCHESTRATION ==========

    public List<OrnamentResponse> getAllOrnaments(Long ownerId) {
        return ornamentRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToOrnamentResponse)
                .collect(Collectors.toList());
    }

    public OrnamentResponse getOrnamentById(Long id) {
        return ornamentRepository.findById(id)
                .map(entityMapper::mapToOrnamentResponse)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", id));
    }

    public OrnamentResponse createOrnament(OrnamentRequest request, Long ownerId) {
        // Verify aquarium ownership if assigned
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            OwnershipDomainService.verifyOwnership(aquarium, ownerId);
        }

        // Create ornament using domain factory
        Ornament ornament = Ornament.create(
                request.name(),
                request.description(),
                request.color(),
                request.isAirPumpCompatible(),
                ownerId,
                request.material()
        );

        // Assign to aquarium if specified
        if (request.aquariumId() != null) {
            ornament.assignToAquarium(request.aquariumId(), ownerId);
        }

        Ornament savedOrnament = ornamentRepository.insert(ornament);
        return entityMapper.mapToOrnamentResponse(savedOrnament);
    }

    public OrnamentResponse updateOrnament(Long id, OrnamentRequest request, Long ownerId) {
        // Get existing ornament and verify ownership
        Ornament ornament = ornamentRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", id));
        
        // Verify ornament ownership
        if (!ornament.getOwnerId().equals(ownerId)) {
            throw new ApplicationException.UnauthorizedException("Only the ornament owner can update it");
        }

        // Update ornament properties
        ornament.update(
                request.name(),
                request.description(),
                request.color(),
                request.isAirPumpCompatible(),
                request.material()
        );

        // Handle aquarium assignment change
        if (request.aquariumId() != null && !request.aquariumId().equals(ornament.getAquariumId())) {
            // Verify new aquarium ownership
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            OwnershipDomainService.verifyOwnership(aquarium, ownerId);
            
            // Assign to new aquarium
            ornament.assignToAquarium(request.aquariumId(), ownerId);
        } else if (request.aquariumId() == null && ornament.getAquariumId() != null) {
            // Remove from current aquarium
            ornament.removeFromAquarium(ownerId);
        }

        Ornament updatedOrnament = ornamentRepository.update(ornament);
        return entityMapper.mapToOrnamentResponse(updatedOrnament);
    }

    public void deleteOrnament(Long id, Long ownerId) {
        // Get ornament and verify ownership
        Ornament ornament = ornamentRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", id));
        
        // Verify ornament ownership
        if (!ornament.getOwnerId().equals(ownerId)) {
            throw new ApplicationException.UnauthorizedException("Only the ornament owner can delete it");
        }
        
        ornamentRepository.deleteById(id);
    }

    public List<OrnamentResponse> getOrnamentsByAquarium(Long aquariumId) {
        return ornamentRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToOrnamentResponse)
                .collect(Collectors.toList());
    }

    public AquariumResponse addOrnamentToAquarium(Long aquariumId, Long ornamentId, Map<String, Object> properties, Long ownerId) {
        // Verify aquarium ownership
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        OwnershipDomainService.verifyOwnership(aquarium, ownerId);
        
        // Get and verify ornament
        Ornament ornament = ornamentRepository.findById(ornamentId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));
        
        // Use domain method to securely assign ornament to aquarium
        ornament.assignToAquarium(aquariumId, ownerId);
        
        // Persist the assignment
        ornamentRepository.update(ornament);
        
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    public AquariumResponse removeOrnamentFromAquarium(Long aquariumId, Long ornamentId, Long ownerId) {
        // Verify aquarium ownership
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        OwnershipDomainService.verifyOwnership(aquarium, ownerId);
        
        // Get and verify ornament
        Ornament ornament = ornamentRepository.findById(ornamentId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));
        
        // Use domain method to securely remove ornament from aquarium
        ornament.removeFromAquarium(ownerId);
        
        // Persist the removal
        ornamentRepository.update(ornament);
        
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    // ========== INHABITANT OPERATIONS - PURE ORCHESTRATION ==========

    public List<InhabitantResponse> getAllInhabitants(Long ownerId) {
        return inhabitantRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToInhabitantResponse)
                .collect(Collectors.toList());
    }

    public InhabitantResponse getInhabitantById(Long id) {
        return inhabitantRepository.findById(id)
                .map(entityMapper::mapToInhabitantResponse)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", id));
    }

    public InhabitantResponse createInhabitant(InhabitantRequest request, Long ownerId) {
        // Verify aquarium ownership if assigned
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            OwnershipDomainService.verifyOwnership(aquarium, ownerId);
        }

        // Create inhabitant using domain factory
        Inhabitant inhabitant = Inhabitant.createFromType(
                request.type(),
                request.species(),
                request.color(),
                request.count(),
                request.isSchooling(),
                request.waterType(),
                false, // isAggressiveEater - default
                false, // requiresSpecialFood - default
                false, // isSnailEater - default
                ownerId,
                request.name(),
                request.description()
        );

        // Assign to aquarium if specified
        if (request.aquariumId() != null) {
            inhabitant.assignToAquarium(request.aquariumId(), ownerId);
        }

        Inhabitant savedInhabitant = inhabitantRepository.insert(inhabitant);
        return entityMapper.mapToInhabitantResponse(savedInhabitant);
    }

    public List<InhabitantResponse> getInhabitantsByAquarium(Long aquariumId) {
        return inhabitantRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToInhabitantResponse)
                .collect(Collectors.toList());
    }

    public InhabitantResponse updateInhabitant(Long id, InhabitantRequest request, Long ownerId) {
        // Get existing inhabitant and verify ownership
        Inhabitant inhabitant = inhabitantRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", id));
        
        // Verify inhabitant ownership
        if (!inhabitant.getOwnerId().equals(ownerId)) {
            throw new ApplicationException.UnauthorizedException("Only the inhabitant owner can update it");
        }

        // Update inhabitant properties
        inhabitant.update(
                request.species(),
                request.color(),
                request.count(),
                request.isSchooling(),
                request.waterType(),
                request.name(),
                request.description()
        );

        // Handle aquarium assignment change
        if (request.aquariumId() != null && !request.aquariumId().equals(inhabitant.getAquariumId())) {
            // Verify new aquarium ownership
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            OwnershipDomainService.verifyOwnership(aquarium, ownerId);
            
            // Assign to new aquarium
            inhabitant.assignToAquarium(request.aquariumId(), ownerId);
        } else if (request.aquariumId() == null && inhabitant.getAquariumId() != null) {
            // Remove from current aquarium
            inhabitant.removeFromAquarium(ownerId);
        }

        Inhabitant updatedInhabitant = inhabitantRepository.update(inhabitant);
        return entityMapper.mapToInhabitantResponse(updatedInhabitant);
    }

    public void deleteInhabitant(Long id, Long ownerId) {
        // Get inhabitant and verify ownership
        Inhabitant inhabitant = inhabitantRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", id));
        
        // Verify inhabitant ownership
        if (!inhabitant.getOwnerId().equals(ownerId)) {
            throw new ApplicationException.UnauthorizedException("Only the inhabitant owner can delete it");
        }
        
        inhabitantRepository.deleteById(id);
    }

    // ========== OWNER OPERATIONS - PURE ORCHESTRATION ==========

    public Owner findOwnerByEmail(String email) {
        return ownerRepository.findByEmail(email).orElse(null);
    }

    public AquariumResponse addInhabitantToAquarium(Long aquariumId, Long inhabitantId, Map<String, Object> properties, Long ownerId) {
        // Verify aquarium ownership
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        OwnershipDomainService.verifyOwnership(aquarium, ownerId);
        
        // Get and verify inhabitant
        Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));
        
        // Use domain method to securely assign inhabitant to aquarium
        inhabitant.assignToAquarium(aquariumId, ownerId);
        
        // Persist the assignment
        inhabitantRepository.update(inhabitant);
        
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    public AquariumResponse removeInhabitantFromAquarium(Long aquariumId, Long inhabitantId, Long ownerId) {
        // Verify aquarium ownership
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        OwnershipDomainService.verifyOwnership(aquarium, ownerId);
        
        // Get and verify inhabitant
        Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));
        
        // Use domain method to securely remove inhabitant from aquarium
        inhabitant.removeFromAquarium(ownerId);
        
        // Persist the removal
        inhabitantRepository.update(inhabitant);
        
        return entityMapper.mapToAquariumResponse(aquarium);
    }
}