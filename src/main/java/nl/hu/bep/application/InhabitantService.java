package nl.hu.bep.application;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.application.exception.ApplicationException;
import nl.hu.bep.application.mapper.EntityMappingService;
import nl.hu.bep.data.AquariumRepository;
import nl.hu.bep.data.InhabitantRepository;
import nl.hu.bep.data.OwnerRepository;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.species.Fish;
import nl.hu.bep.presentation.dto.InhabitantRequest;
import nl.hu.bep.presentation.dto.InhabitantResponse;
import nl.hu.bep.presentation.dto.AquariumResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class InhabitantService {
    private final InhabitantRepository inhabitantRepository;
    private final AquariumRepository aquariumRepository;
    // private final OwnerRepository ownerRepository;
    private final EntityMappingService mappingService;

    @Inject
    public InhabitantService(
            InhabitantRepository inhabitantRepository,
            AquariumRepository aquariumRepository,
            OwnerRepository ownerRepository,
            EntityMappingService mappingService) {
        this.inhabitantRepository = inhabitantRepository;
        this.aquariumRepository = aquariumRepository;
        // this.ownerRepository = ownerRepository;
        this.mappingService = mappingService;
    }

    public List<InhabitantResponse> getAllInhabitants(Long ownerId) {
        log.info("Service: Fetching inhabitants for ownerId: {}", ownerId);
        return inhabitantRepository.findByOwnerId(ownerId).stream()
                .map(mappingService::mapInhabitant)
                .collect(Collectors.toList());
    }

    public InhabitantResponse getInhabitantById(Long id) {
        return inhabitantRepository.findById(id)
                .map(mappingService::mapInhabitant)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", id));
    }

    public Inhabitant getInhabitantEntityById(Long id) {
        return inhabitantRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", id));
    }

    public List<InhabitantResponse> getInhabitantsByAquarium(Long aquariumId) {
        Aquarium aquarium = findAquariumWithInhabitants(aquariumId);

        return aquarium.getInhabitants().stream()
                .map(mappingService::mapInhabitant)
                .collect(Collectors.toList());
    }

    public InhabitantResponse createInhabitant(InhabitantRequest request, Long ownerId) {
        Aquarium aquarium = null;
        if (request.aquariumId() != null) {
            aquarium = findAquariumWithInhabitants(request.aquariumId());
            aquarium.verifyOwnership(ownerId);
        }

        Inhabitant inhabitant = Inhabitant.createFromType(
                request.type(),
                request.species(),
                request.color(),
                request.count(),
                request.getSchoolingValue(),
                request.waterType(),
                request.getAggressiveEaterValue(),
                request.getRequiresSpecialFoodValue(),
                request.getSnailEaterValue(),
                ownerId,
                request.name(),
                request.description());

        inhabitant = inhabitantRepository.save(inhabitant);
        log.info("Saved inhabitant with ownerId: {}", inhabitant.getOwnerId());

        if (aquarium != null) {
            aquarium.addToInhabitants(inhabitant);
            aquariumRepository.save(aquarium);
            inhabitant = findInhabitant(inhabitant.getId());
        }

        return mappingService.mapInhabitant(inhabitant);
    }

    public InhabitantResponse updateInhabitant(Long id, InhabitantRequest request) {
        Inhabitant existingInhabitant = findInhabitant(id);

        Aquarium oldAquarium = existingInhabitant.getAquarium() != null
                ? findAquariumWithInhabitants(existingInhabitant.getAquarium().getId())
                : null;
        Aquarium newAquarium = request.aquariumId() != null ? findAquariumWithInhabitants(request.aquariumId()) : null;

        if (oldAquarium != null && (newAquarium == null || !oldAquarium.getId().equals(newAquarium.getId()))) {
            log.info("Removing inhabitant {} from old aquarium: {}", id, oldAquarium.getId());
            oldAquarium.removeFromInhabitants(existingInhabitant);
            aquariumRepository.save(oldAquarium);
        }

        existingInhabitant.update(
                request.species(),
                request.color(),
                request.count(),
                request.isSchooling(),
                request.waterType(),
                request.name(),
                request.description());

        if (existingInhabitant instanceof Fish fish) {
            fish.updateProperties(
                    request.getAggressiveEaterValue(),
                    request.getRequiresSpecialFoodValue(),
                    request.getSnailEaterValue());
        }

        existingInhabitant = inhabitantRepository.save(existingInhabitant);

        if (newAquarium != null && (oldAquarium == null || !oldAquarium.getId().equals(newAquarium.getId()))) {
            log.info("Adding inhabitant {} to new aquarium: {}", id, newAquarium.getId());
            newAquarium.addToInhabitants(existingInhabitant);
            aquariumRepository.save(newAquarium);
            existingInhabitant = findInhabitant(existingInhabitant.getId());
        }

        return mappingService.mapInhabitant(existingInhabitant);
    }

    public void deleteInhabitant(Long id) {
        if (!inhabitantRepository.existsById(id)) {
            throw new ApplicationException.NotFoundException("Inhabitant", id);
        }

        Inhabitant inhabitant = findInhabitant(id);
        if (inhabitant.getAquarium() != null) {
            Aquarium aquarium = findAquariumWithInhabitants(inhabitant.getAquarium().getId());
            aquarium.removeFromInhabitants(inhabitant);
            aquariumRepository.save(aquarium);
        }

        inhabitantRepository.deleteById(id);
    }

    public AquariumResponse addInhabitant(Long aquariumId, Long inhabitantId,
            Map<String, Object> properties, Long ownerId) {
        
        try {
            // Fetch aquarium with inhabitants collection eagerly loaded 
            Aquarium aquarium = aquariumRepository.findByIdWithInhabitants(aquariumId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

            aquarium.verifyOwnership(ownerId);

            Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));

            log.info("Adding inhabitant {} to aquarium: {}", inhabitant, aquarium);

            aquarium.addToInhabitants(inhabitant);

            inhabitant = inhabitantRepository.save(inhabitant);
            aquarium = aquariumRepository.save(aquarium);

            // Fetch the updated aquarium with all collections to ensure they're loaded
            Aquarium updatedAquarium = aquariumRepository.findByIdWithAllCollections(aquariumId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

            return mappingService.mapAquariumDetailed(updatedAquarium);
        } catch (Exception e) {
            log.error("Error adding inhabitant to aquarium", e);
            throw new ApplicationException.BadRequestException(
                    "Failed to add inhabitant to aquarium: " + e.getMessage(), e);
        }
    }

    public AquariumResponse removeInhabitant(Long aquariumId, Long inhabitantId, Long ownerId) {
        
        try {
            // Fetch aquarium with inhabitants collection eagerly loaded
            Aquarium aquarium = aquariumRepository.findByIdWithInhabitants(aquariumId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

            aquarium.verifyOwnership(ownerId);

            Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));

            log.info("Removing inhabitant {} from aquarium: {}", inhabitant, aquarium);

            aquarium.removeFromInhabitants(inhabitant);

            inhabitant = inhabitantRepository.save(inhabitant);
            aquarium = aquariumRepository.save(aquarium);

            // Fetch the updated aquarium with all collections to ensure they're loaded
            Aquarium updatedAquarium = aquariumRepository.findByIdWithAllCollections(aquariumId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

            return mappingService.mapAquariumDetailed(updatedAquarium);
        } catch (Exception e) {
            log.error("Error removing inhabitant from aquarium", e);
            throw new ApplicationException.BadRequestException(
                    "Failed to remove inhabitant from aquarium: " + e.getMessage(), e);
        }
    }

    private Inhabitant findInhabitant(Long id) {
        return inhabitantRepository.findById(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", id));
    }

    private Aquarium findAquariumWithInhabitants(Long id) {
        return aquariumRepository.findByIdWithInhabitants(id)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", id));
    }

    // private Owner findOwner(Long id) {
    // return ownerRepository.findById(id)
    // .orElseThrow(() -> new ApplicationException.NotFoundException("Owner", id));
    // }
}