package nl.hu.bep.application.service;

import nl.hu.bep.data.interfaces.InhabitantRepository;
import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.dto.request.InhabitantRequest;
import nl.hu.bep.presentation.dto.response.InhabitantResponse;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service focused solely on inhabitant-related operations.
 * Follows Single Responsibility Principle.
 */
@Slf4j
public class InhabitantService {

    private final InhabitantRepository inhabitantRepository;
    private final AquariumRepository aquariumRepository;
    private final EntityMapper entityMapper;

    @Inject
    public InhabitantService(InhabitantRepository inhabitantRepository,
                            AquariumRepository aquariumRepository,
                            EntityMapper entityMapper) {
        this.inhabitantRepository = inhabitantRepository;
        this.aquariumRepository = aquariumRepository;
        this.entityMapper = entityMapper;
    }

    public List<InhabitantResponse> getAllInhabitants(Long ownerId) {
        return inhabitantRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToInhabitantResponse)
                .collect(Collectors.toList());
    }

    public InhabitantResponse getInhabitant(Long inhabitantId, Long requestingOwnerId) {
        Inhabitant inhabitant = findOwnedInhabitant(inhabitantId, requestingOwnerId);
        return entityMapper.mapToInhabitantResponse(inhabitant);
    }

    @Transactional
    public InhabitantResponse createInhabitant(InhabitantRequest request, Long ownerId) {
        validateAquariumAssignment(request.aquariumId(), ownerId);

        Inhabitant inhabitant = Inhabitant.create(
                request.type(),
                request.species(),
                request.name(),
                ownerId,
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.count()),
                Optional.ofNullable(request.isSchooling()),
                Optional.ofNullable(request.waterType()),
                Optional.ofNullable(request.description()),
                new Inhabitant.InhabitantProperties(
                        request.getAggressiveEaterValue(),
                        request.getRequiresSpecialFoodValue(),
                        request.getSnailEaterValue()
                )
        );

        if (request.aquariumId() != null) {
            inhabitant.assignToAquarium(request.aquariumId(), ownerId);
        }

        Inhabitant savedInhabitant = inhabitantRepository.insert(inhabitant);
        return entityMapper.mapToInhabitantResponse(savedInhabitant);
    }

    @Transactional
    public InhabitantResponse updateInhabitant(Long inhabitantId, InhabitantRequest request, Long requestingOwnerId) {
        Inhabitant inhabitant = findOwnedInhabitant(inhabitantId, requestingOwnerId);

        inhabitant.update(
                Optional.ofNullable(request.name()),
                Optional.ofNullable(request.species()),
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.count()),
                Optional.ofNullable(request.isSchooling()),
                Optional.ofNullable(request.waterType()),
                Optional.ofNullable(request.description())
        );

        if (request.aquariumId() != null) {
            validateAquariumAssignment(request.aquariumId(), requestingOwnerId);
            inhabitant.assignToAquarium(request.aquariumId(), requestingOwnerId);
        } else {
            inhabitant.removeFromAquarium(requestingOwnerId);
        }

        Inhabitant updatedInhabitant = inhabitantRepository.update(inhabitant);
        return entityMapper.mapToInhabitantResponse(updatedInhabitant);
    }

    @Transactional
    public void deleteInhabitant(Long inhabitantId, Long requestingOwnerId) {
        findOwnedInhabitant(inhabitantId, requestingOwnerId); // Validates ownership
        inhabitantRepository.deleteById(inhabitantId);
        log.info("Inhabitant {} deleted by owner {}", inhabitantId, requestingOwnerId);
    }

    private Inhabitant findOwnedInhabitant(Long inhabitantId, Long requestingOwnerId) {
        Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));
        inhabitant.validateOwnership(requestingOwnerId);
        return inhabitant;
    }

    public List<InhabitantResponse> getInhabitantsByAquarium(Long aquariumId) {
        return inhabitantRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToInhabitantResponse)
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
