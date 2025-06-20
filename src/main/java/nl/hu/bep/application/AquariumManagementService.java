package nl.hu.bep.application;

import nl.hu.bep.data.*;
import nl.hu.bep.domain.*;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.*;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Single application service for aquarium management.
 * THIN ORCHESTRATION ONLY - no business logic, no DTO mapping.
 * 
 * Business logic → Domain objects
 * DTO mapping → EntityMapper
 * Security rules → Domain services
 */
@ApplicationScoped
@Transactional
public class AquariumManagementService {
    
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

    // ========== AQUARIUM OPERATIONS ==========

    public List<AquariumResponse> getOwnerAquariums(Long ownerId) {
        return aquariumRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToAquariumResponse)
                .collect(Collectors.toList());
    }

    public AquariumResponse getAquarium(Long aquariumId, Long requestingOwnerId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        
        // Domain service handles security
        aquarium.verifyOwnership(requestingOwnerId);
        
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    public AquariumResponse createAquarium(AquariumRequest request, Long ownerId) {
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

        // Domain method handles assignment
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Owner", ownerId));
        aquarium.assignToOwner(owner);

        Aquarium savedAquarium = aquariumRepository.insert(aquarium);
        return entityMapper.mapToAquariumResponse(savedAquarium);
    }

    public AquariumResponse updateAquarium(Long aquariumId, AquariumRequest request, Long requestingOwnerId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

        // Domain service handles security
        aquarium.verifyOwnership(requestingOwnerId);

        // Domain method handles validation and update
        aquarium.update(
                request.name(),
                request.length(),
                request.width(),
                request.height(),
                request.substrate(),
                request.waterType(),
                request.state(),
                null);

        Aquarium updatedAquarium = aquariumRepository.update(aquarium);
        return entityMapper.mapToAquariumResponse(updatedAquarium);
    }

    public void deleteAquarium(Long aquariumId, Long requestingOwnerId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        
        // Domain service handles security
        aquarium.verifyOwnership(requestingOwnerId);
        
        aquariumRepository.deleteById(aquariumId);
    }

    // ========== INHABITANT OPERATIONS ==========

    public List<InhabitantResponse> getOwnerInhabitants(Long ownerId) {
        return inhabitantRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToInhabitantResponse)
                .collect(Collectors.toList());
    }

    public InhabitantResponse createInhabitant(InhabitantRequest request, Long ownerId) {
        // Verify aquarium ownership if assigned
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.verifyOwnership(ownerId);
        }

        // Domain factory handles creation - using createFromType
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

        // Domain method handles aquarium assignment with security validation
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.addToInhabitants(inhabitant);
        }

        Inhabitant savedInhabitant = inhabitantRepository.insert(inhabitant);
        return entityMapper.mapToInhabitantResponse(savedInhabitant);
    }

    // ========== ACCESSORY OPERATIONS ==========

    public List<AccessoryResponse> getOwnerAccessories(Long ownerId) {
        return accessoryRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToAccessoryResponse)
                .collect(Collectors.toList());
    }

    // ========== ORNAMENT OPERATIONS ==========

    public List<OrnamentResponse> getOwnerOrnaments(Long ownerId) {
        return ornamentRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToOrnamentResponse)
                .collect(Collectors.toList());
    }

    public OrnamentResponse createOrnament(OrnamentRequest request, Long ownerId) {
        // Verify aquarium ownership if assigned
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.verifyOwnership(ownerId);
        }

        // Domain factory handles creation
        Ornament ornament = Ornament.create(
                request.name(),
                request.description(),
                request.color(),
                request.isAirPumpCompatible(),
                ownerId,
                request.material()
        );

        // Domain method handles aquarium assignment with security validation
        if (request.aquariumId() != null) {
            ornament.assignToAquarium(request.aquariumId(), ownerId);
        }

        Ornament savedOrnament = ornamentRepository.insert(ornament);
        return entityMapper.mapToOrnamentResponse(savedOrnament);
    }

    // ========== OWNER OPERATIONS ==========

    public Owner findOwnerByEmail(String email) {
        return ownerRepository.findByEmail(email).orElse(null);
    }
} 