package nl.hu.bep.application.service;

import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.data.interfaces.OwnerRepository;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.dto.request.AquariumRequest;
import nl.hu.bep.presentation.dto.response.AquariumResponse;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service focused solely on aquarium-related operations.
 * Follows Single Responsibility Principle.
 */
@Slf4j
public class AquariumService {

    private final AquariumRepository aquariumRepository;
    private final OwnerRepository ownerRepository;
    private final EntityMapper entityMapper;

    @Inject
    public AquariumService(AquariumRepository aquariumRepository,
                          OwnerRepository ownerRepository,
                          EntityMapper entityMapper) {
        this.aquariumRepository = aquariumRepository;
        this.ownerRepository = ownerRepository;
        this.entityMapper = entityMapper;
    }

    public List<AquariumResponse> getAllAquariums(Long ownerId) {
        return aquariumRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToAquariumResponse)
                .collect(Collectors.toList());
    }

    public AquariumResponse getAquarium(Long aquariumId, Long requestingOwnerId) {
        Aquarium aquarium = findOwnedAquarium(aquariumId, requestingOwnerId);
        return entityMapper.mapToAquariumResponse(aquarium);
    }

    @Transactional
    public AquariumResponse createAquarium(AquariumRequest request, Long ownerId) {
        validateOwnerExists(ownerId);

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

        aquarium.assignToOwner(ownerId);
        Aquarium savedAquarium = aquariumRepository.insert(aquarium);
        return entityMapper.mapToAquariumResponse(savedAquarium);
    }

    @Transactional
    public AquariumResponse updateAquarium(Long aquariumId, AquariumRequest request, Long requestingOwnerId) {
        Aquarium aquarium = findOwnedAquarium(aquariumId, requestingOwnerId);

        aquarium.update(
                request.name(),
                request.length(),
                request.width(),
                request.height(),
                request.substrate(),
                request.waterType(),
                request.state(),
                null // temperature not supported in AquariumRequest, will be handled separately if needed
        );

        Aquarium updatedAquarium = aquariumRepository.update(aquarium);
        return entityMapper.mapToAquariumResponse(updatedAquarium);
    }

    @Transactional
    public void deleteAquarium(Long aquariumId, Long requestingOwnerId) {
        findOwnedAquarium(aquariumId, requestingOwnerId); // Validates ownership
        aquariumRepository.deleteById(aquariumId);
        log.info("Aquarium {} deleted by owner {}", aquariumId, requestingOwnerId);
    }

    private Aquarium findOwnedAquarium(Long aquariumId, Long requestingOwnerId) {
        Aquarium aquarium = aquariumRepository.findById(aquariumId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
        aquarium.validateOwnership(requestingOwnerId);
        return aquarium;
    }

    private void validateOwnerExists(Long ownerId) {
        if (!ownerRepository.findById(ownerId).isPresent()) {
            throw new ApplicationException.NotFoundException("Owner", ownerId);
        }
    }
}
