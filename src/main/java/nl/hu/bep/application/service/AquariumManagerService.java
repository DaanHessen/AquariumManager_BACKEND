package nl.hu.bep.application.service;

import nl.hu.bep.domain.*; 
import nl.hu.bep.presentation.dto.request.*;
import nl.hu.bep.presentation.dto.response.*;
import nl.hu.bep.data.interfaces.*;
import nl.hu.bep.application.factory.InhabitantFactory;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;

import lombok.extern.slf4j.Slf4j;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
public class AquariumManagerService {
    
    private final AquariumRepository aquariumRepository;
    private final AccessoryRepository accessoryRepository;
    private final OrnamentRepository ornamentRepository;
    private final InhabitantRepository inhabitantRepository;
    private final OwnerRepository ownerRepository;
    private final EntityMapper entityMapper;
    private final InhabitantFactory inhabitantFactory;

    public AquariumManagerService(
            AquariumRepository aquariumRepository,
            AccessoryRepository accessoryRepository,
            OrnamentRepository ornamentRepository,
            InhabitantRepository inhabitantRepository,
            OwnerRepository ownerRepository,
            EntityMapper entityMapper,
            InhabitantFactory inhabitantFactory) {
        this.aquariumRepository = aquariumRepository;
        this.accessoryRepository = accessoryRepository;
        this.ornamentRepository = ornamentRepository;
        this.inhabitantRepository = inhabitantRepository;
        this.ownerRepository = ownerRepository;
        this.entityMapper = entityMapper;
        this.inhabitantFactory = inhabitantFactory;
    }

    public List<AquariumResponse> getAllAquariums(Long ownerId) {
        List<Aquarium> aquariums = aquariumRepository.findByOwnerId(ownerId);
        return entityMapper.mapToAquariumResponses(aquariums);
    }

    public AquariumResponse getAquarium(Long aquariumId, Long requestingOwnerId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        
        aquarium.validateOwnership(requestingOwnerId);
        
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    public AquariumResponse createAquarium(AquariumRequest request, Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Owner", ownerId));

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

        aquarium.validateOwnership(requestingOwnerId);

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
        
        aquarium.validateOwnership(requestingOwnerId);
        
        aquariumRepository.deleteById(aquariumId);
    }

    public List<AccessoryResponse> getAllAccessories(Long ownerId) {
        return accessoryRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToAccessoryResponse)
                .collect(Collectors.toList());
    }

    public AccessoryResponse getAccessory(Long accessoryId, Long requestingOwnerId) {
        Accessory accessory = accessoryRepository.findById(accessoryId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));
        
        accessory.validateOwnership(requestingOwnerId);
        
        return entityMapper.mapToAccessoryResponse(accessory);
    }

    public AccessoryResponse createAccessory(AccessoryRequest request, Long ownerId) {
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.validateOwnership(ownerId);
        }

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

    public AccessoryResponse updateAccessory(Long accessoryId, AccessoryRequest request, Long requestingOwnerId) {
        Accessory accessory = accessoryRepository.findById(accessoryId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));
        
        accessory.validateOwnership(requestingOwnerId);

        accessory.update(
                request.model(),
                request.serialNumber(),
                request.getColorValue(),
                request.getDescriptionValue()
        );

        if (request.aquariumId() != null && !request.aquariumId().equals(accessory.getAquariumId())) {
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
        
        accessory.validateOwnership(requestingOwnerId);
        
        accessoryRepository.deleteById(accessoryId);
    }

    public List<AccessoryResponse> getAccessoriesByAquarium(Long aquariumId) {
        return accessoryRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToAccessoryResponse)
                .collect(Collectors.toList());
    }

    public List<OrnamentResponse> getAllOrnaments(Long ownerId) {
        return ornamentRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToOrnamentResponse)
                .collect(Collectors.toList());
    }

    public OrnamentResponse getOrnament(Long ornamentId, Long requestingOwnerId) {
        Ornament ornament = ornamentRepository.findById(ornamentId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));
        
        ornament.validateOwnership(requestingOwnerId);
        
        return entityMapper.mapToOrnamentResponse(ornament);
    }

    public OrnamentResponse createOrnament(OrnamentRequest request, Long ownerId) {
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.validateOwnership(ownerId);
        }

        Ornament ornament = Ornament.create(
                request.name(),
                ownerId,
                Optional.ofNullable(request.description()),
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.material()),
                Optional.ofNullable(request.isAirPumpCompatible())
        );

        if (request.aquariumId() != null) {
            ornament.assignToAquarium(request.aquariumId(), ownerId);
        }

        Ornament savedOrnament = ornamentRepository.insert(ornament);
        return entityMapper.mapToOrnamentResponse(savedOrnament);
    }

    public OrnamentResponse updateOrnament(Long ornamentId, OrnamentRequest request, Long requestingOwnerId) {
        Ornament ornament = ornamentRepository.findById(ornamentId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));

        ornament.validateOwnership(requestingOwnerId);

        ornament.update(
                Optional.ofNullable(request.name()),
                Optional.ofNullable(request.description()),
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.material()),
                Optional.ofNullable(request.isAirPumpCompatible())
        );

        if (request.aquariumId() != null && !request.aquariumId().equals(ornament.getAquariumId())) {
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
        
        ornament.validateOwnership(requestingOwnerId);
        
        ornamentRepository.deleteById(ornamentId);
    }

    public List<OrnamentResponse> getOrnamentsByAquarium(Long aquariumId) {
        return ornamentRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToOrnamentResponse)
                .collect(Collectors.toList());
    }

    public List<InhabitantResponse> getAllInhabitants(Long ownerId) {
        return inhabitantRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToInhabitantResponse)
                .collect(Collectors.toList());
    }

    public InhabitantResponse getInhabitant(Long inhabitantId, Long requestingOwnerId) {
        Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));
        
        inhabitant.validateOwnership(requestingOwnerId);
        
        return entityMapper.mapToInhabitantResponse(inhabitant);
    }

    public InhabitantResponse createInhabitant(InhabitantRequest request, Long ownerId) {
        if (request.aquariumId() != null) {
            Aquarium aquarium = aquariumRepository.findById(request.aquariumId())
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", request.aquariumId()));
            aquarium.validateOwnership(ownerId);
        }

        Inhabitant.InhabitantProperties properties = new Inhabitant.InhabitantProperties(
                request.getAggressiveEaterValue(),
                request.getRequiresSpecialFoodValue(),
                request.getSnailEaterValue()
        );

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

        if (request.aquariumId() != null) {
            inhabitant.assignToAquarium(request.aquariumId(), ownerId);
        }

        Inhabitant savedInhabitant = inhabitantRepository.insert(inhabitant);
        return entityMapper.mapToInhabitantResponse(savedInhabitant);
    }

    public InhabitantResponse updateInhabitant(Long inhabitantId, InhabitantRequest request, Long requestingOwnerId) {
        Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));

        inhabitant.validateOwnership(requestingOwnerId);

        inhabitant.update(
                Optional.ofNullable(request.name()),
                Optional.ofNullable(request.species()),
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.count()),
                Optional.ofNullable(request.isSchooling()),
                Optional.ofNullable(request.waterType()),
                Optional.ofNullable(request.description())
        );

        if (request.aquariumId() != null && !request.aquariumId().equals(inhabitant.getAquariumId())) {
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
        
        inhabitant.validateOwnership(requestingOwnerId);
        
        inhabitantRepository.deleteById(inhabitantId);
    }

    public List<InhabitantResponse> getInhabitantsByAquarium(Long aquariumId) {
        return inhabitantRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToInhabitantResponse)
                .collect(Collectors.toList());
    }

    public Owner findOwnerByEmail(String email) {
        return ownerRepository.findByEmail(email).orElse(null);
    }
}