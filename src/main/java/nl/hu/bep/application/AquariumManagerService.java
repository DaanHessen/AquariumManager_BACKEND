package nl.hu.bep.application;

import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.data.*;
import nl.hu.bep.domain.*;
import nl.hu.bep.application.factory.InhabitantFactory;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.*;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Application service orchestrating business operations.
 * Coordinates between presentation layer, domain services, and data access.
 * Uses CDI for dependency injection following Jakarta EE best practices.
 * 
 * Responsibilities:
 * Business logic → Domain objects & Domain services
 * DTO mapping → EntityMapper
 * Security rules → Domain services
 */
@Slf4j
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
    @Inject
    private InhabitantFactory inhabitantFactory;

    // Default constructor for CDI
    public AquariumManagerService() {
        // CDI will inject dependencies
    }

    // ========== AQUARIUM OPERATIONS - PURE ORCHESTRATION ==========

    public List<AquariumResponse> getAllAquariums(Long ownerId) {
        return aquariumRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToAquariumResponse)
                .collect(Collectors.toList());
    }

    public AquariumResponse getAquarium(Long aquariumId, Long requestingOwnerId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        
        // Entity handles its own ownership validation
        aquarium.validateOwnership(requestingOwnerId);
        
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    public AquariumResponse createAquarium(AquariumRequest request, Long ownerId) {
        // Get owner and verify existence
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Owner", ownerId));

        // Domain factory handles validation
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

        // Domain method handles assignment - aquarium inherits manager from owner
        aquarium.assignToOwner(owner.getId());
        if (owner.getAquariumManagerId() != null) {
            aquarium.assignToManager(owner.getAquariumManagerId());
        }

        Aquarium savedAquarium = aquariumRepository.insert(aquarium);
        return entityMapper.mapToAquariumResponse(savedAquarium);
    }

    public AquariumResponse updateAquarium(Long aquariumId, AquariumRequest request, Long requestingOwnerId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

        // Domain service handles security
        aquarium.validateOwnership(requestingOwnerId);

        // Domain method handles validation and update
        aquarium.updateName(request.name());
        aquarium.updateDimensions(request.length(), request.width(), request.height());
        aquarium.updateSubstrate(request.substrate());
        aquarium.updateWaterType(request.waterType());
        aquarium.updateState(request.state());


        Aquarium updatedAquarium = aquariumRepository.update(aquarium);
        return entityMapper.mapToAquariumResponse(updatedAquarium);
    }

    public void deleteAquarium(Long aquariumId, Long requestingOwnerId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        
        // Domain service handles security
        aquarium.validateOwnership(requestingOwnerId);
        
        aquariumRepository.deleteById(aquariumId);
    }

    // ========== ACCESSORY OPERATIONS - PURE ORCHESTRATION ==========

    public List<AccessoryResponse> getAllAccessories(Long ownerId) {
        return accessoryRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToAccessoryResponse)
                .collect(Collectors.toList());
    }

    public AccessoryResponse getAccessory(Long accessoryId, Long requestingOwnerId) {
        Accessory accessory = accessoryRepository.findById(accessoryId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));
        
        // Domain service handles security
        if (accessory == null) throw new ApplicationException.NotFoundException("Accessory", null); accessory.validateOwnership(requestingOwnerId);
        
        return entityMapper.mapToAccessoryResponse(accessory);
    }

    public AccessoryResponse createAccessory(AccessoryRequest request, Long ownerId) {
        // Verify aquarium ownership if assigned
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.validateOwnership(ownerId);
        }

        // Use the accessor method which provides sensible defaults
        Accessory accessory = Accessory.createFromType(
                request.type(),
                request.model(),
                request.serialNumber(),
                request.getIsExternalValue(),
                request.getCapacityLitersValue(), // Use accessor method with defaults
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

        // Domain method handles aquarium assignment with security validation
        if (request.aquariumId() != null) {
            accessory.assignToAquarium(request.aquariumId(), ownerId);
        }

        Accessory savedAccessory = accessoryRepository.insert(accessory);
        return entityMapper.mapToAccessoryResponse(savedAccessory);
    }

    public AccessoryResponse updateAccessory(Long accessoryId, AccessoryRequest request, Long requestingOwnerId) {
        Accessory accessory = accessoryRepository.findById(accessoryId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));
        
        // Domain service handles security
        if (accessory == null) throw new ApplicationException.NotFoundException("Accessory", null); accessory.validateOwnership(requestingOwnerId);

        // Domain method handles validation and update
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
            aquarium.validateOwnership(requestingOwnerId);
            
            accessory.assignToAquarium(request.aquariumId(), requestingOwnerId);
        } else if (request.aquariumId() == null && accessory.getAquariumId() != null) {
            accessory.removeFromAquarium(requestingOwnerId);
        }

        Accessory updatedAccessory = accessoryRepository.update(accessory);
        return entityMapper.mapToAccessoryResponse(updatedAccessory);
    }

    public void deleteAccessory(Long accessoryId, Long requestingOwnerId) {
        Accessory accessory = accessoryRepository.findById(accessoryId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));
        
        // Domain service handles security
        if (accessory == null) throw new ApplicationException.NotFoundException("Accessory", null); accessory.validateOwnership(requestingOwnerId);
        
        accessoryRepository.deleteById(accessoryId);
    }

    public List<AccessoryResponse> getAccessoriesByAquarium(Long aquariumId) {
        return accessoryRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToAccessoryResponse)
                .collect(Collectors.toList());
    }

    // ========== ORNAMENT OPERATIONS - PURE ORCHESTRATION ==========

    public List<OrnamentResponse> getAllOrnaments(Long ownerId) {
        return ornamentRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToOrnamentResponse)
                .collect(Collectors.toList());
    }

    public OrnamentResponse getOrnament(Long ornamentId, Long requestingOwnerId) {
        Ornament ornament = ornamentRepository.findById(ornamentId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));
        
        // Domain entity handles security
        ornament.validateOwnership(requestingOwnerId);
        
        return entityMapper.mapToOrnamentResponse(ornament);
    }

    public OrnamentResponse createOrnament(OrnamentRequest request, Long ownerId) {
        // Verify aquarium ownership if assigned
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.validateOwnership(ownerId);
        }

        // Domain factory handles creation
        Ornament ornament = Ornament.create(
                request.name(),
                ownerId,
                Optional.ofNullable(request.description()),
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.material()),
                Optional.ofNullable(request.isAirPumpCompatible())
        );

        // Domain method handles aquarium assignment with security validation
        if (request.aquariumId() != null) {
            ornament.assignToAquarium(request.aquariumId(), ownerId);
        }

        Ornament savedOrnament = ornamentRepository.insert(ornament);
        return entityMapper.mapToOrnamentResponse(savedOrnament);
    }

    public OrnamentResponse updateOrnament(Long ornamentId, OrnamentRequest request, Long requestingOwnerId) {
        Ornament ornament = ornamentRepository.findById(ornamentId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));

        // Domain entity handles security
        ornament.validateOwnership(requestingOwnerId);

        // Domain method handles validation and update
        ornament.update(
                Optional.ofNullable(request.name()),
                Optional.ofNullable(request.description()),
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.material()),
                Optional.ofNullable(request.isAirPumpCompatible())
        );

        // Handle aquarium assignment change
        if (request.aquariumId() != null && !request.aquariumId().equals(ornament.getAquariumId())) {
            // Verify new aquarium ownership
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.validateOwnership(requestingOwnerId);
            
            ornament.assignToAquarium(request.aquariumId(), requestingOwnerId);
        } else if (request.aquariumId() == null && ornament.getAquariumId() != null) {
            ornament.removeFromAquarium(requestingOwnerId);
        }

        Ornament updatedOrnament = ornamentRepository.update(ornament);
        return entityMapper.mapToOrnamentResponse(updatedOrnament);
    }

    public void deleteOrnament(Long ornamentId, Long requestingOwnerId) {
        Ornament ornament = ornamentRepository.findById(ornamentId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));
        
        // Domain service handles security
        ornament.validateOwnership(requestingOwnerId);
        
        ornamentRepository.deleteById(ornamentId);
    }

    public List<OrnamentResponse> getOrnamentsByAquarium(Long aquariumId) {
        return ornamentRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToOrnamentResponse)
                .collect(Collectors.toList());
    }

    // ========== INHABITANT OPERATIONS - PURE ORCHESTRATION ==========

    public List<InhabitantResponse> getAllInhabitants(Long ownerId) {
        return inhabitantRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToInhabitantResponse)
                .collect(Collectors.toList());
    }

    public InhabitantResponse getInhabitant(Long inhabitantId, Long requestingOwnerId) {
        Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));
        
        // Domain service handles security
        inhabitant.validateOwnership(requestingOwnerId);
        
        return entityMapper.mapToInhabitantResponse(inhabitant);
    }

    public InhabitantResponse createInhabitant(InhabitantRequest request, Long ownerId) {
        // Verify aquarium ownership if assigned
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.validateOwnership(ownerId);
        }

        // Create properties object for species-specific attributes
        Inhabitant.InhabitantProperties properties = new Inhabitant.InhabitantProperties(
                request.getAggressiveEaterValue(),
                request.getRequiresSpecialFoodValue(),
                request.getSnailEaterValue()
        );

        // Use factory to create the appropriate inhabitant type
        Inhabitant inhabitant = inhabitantFactory.createInhabitant(
                request.type(),
                request.species(),
                request.name(),
                ownerId,
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.count()),
                Optional.ofNullable(request.isSchooling()),
                Optional.ofNullable(request.waterType()),
                Optional.ofNullable(request.description()),
                properties
        );

        // Domain method handles aquarium assignment with security validation
        if (request.aquariumId() != null) {
            inhabitant.assignToAquarium(request.aquariumId(), ownerId);
        }

        Inhabitant savedInhabitant = inhabitantRepository.insert(inhabitant);
        return entityMapper.mapToInhabitantResponse(savedInhabitant);
    }

    public InhabitantResponse updateInhabitant(Long inhabitantId, InhabitantRequest request, Long requestingOwnerId) {
        Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));

        // Domain service handles security
        inhabitant.validateOwnership(requestingOwnerId);

        // Domain method handles validation and update
        inhabitant.update(
                Optional.ofNullable(request.name()),
                Optional.ofNullable(request.species()),
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.count()),
                Optional.ofNullable(request.isSchooling()),
                Optional.ofNullable(request.waterType()),
                Optional.ofNullable(request.description())
        );

        // Handle aquarium assignment change
        if (request.aquariumId() != null && !request.aquariumId().equals(inhabitant.getAquariumId())) {
            // Verify new aquarium ownership
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.validateOwnership(requestingOwnerId);
            
            inhabitant.assignToAquarium(request.aquariumId(), requestingOwnerId);
        } else if (request.aquariumId() == null && inhabitant.getAquariumId() != null) {
            inhabitant.removeFromAquarium(requestingOwnerId);
        }

        Inhabitant updatedInhabitant = inhabitantRepository.update(inhabitant);
        return entityMapper.mapToInhabitantResponse(updatedInhabitant);
    }

    public void deleteInhabitant(Long inhabitantId, Long requestingOwnerId) {
        Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));
        
        // Domain service handles security
        inhabitant.validateOwnership(requestingOwnerId);
        
        inhabitantRepository.deleteById(inhabitantId);
    }

    public List<InhabitantResponse> getInhabitantsByAquarium(Long aquariumId) {
        return inhabitantRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToInhabitantResponse)
                .collect(Collectors.toList());
    }

    // ========== OWNER OPERATIONS ==========

    public Owner findOwnerByEmail(String email) {
        return ownerRepository.findByEmail(email).orElse(null);
    }
}