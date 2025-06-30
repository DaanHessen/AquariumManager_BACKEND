package nl.hu.bep.application.service;

import nl.hu.bep.data.interfaces.OrnamentRepository;
import nl.hu.bep.data.interfaces.OwnerRepository;  
import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.domain.Ornament;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.presentation.dto.request.OrnamentRequest;
import nl.hu.bep.presentation.dto.response.OrnamentResponse;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class OrnamentService {

    private final OrnamentRepository ornamentRepository;
    private final OwnerRepository ownerRepository;
    private final AquariumRepository aquariumRepository;
    private final EntityMapper entityMapper;

    @Inject
    public OrnamentService(OrnamentRepository ornamentRepository,
                          OwnerRepository ownerRepository,
                          AquariumRepository aquariumRepository,
                          EntityMapper entityMapper) {
        this.ornamentRepository = ornamentRepository;
        this.ownerRepository = ownerRepository;
        this.aquariumRepository = aquariumRepository;
        this.entityMapper = entityMapper;
    }

    public List<OrnamentResponse> getAllOrnaments(Long ownerId) {
        return ornamentRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::mapToOrnamentResponse)
                .collect(Collectors.toList());
    }

    public OrnamentResponse getOrnament(Long ornamentId, Long requestingOwnerId) {
        Ornament ornament = findOwnedOrnament(ornamentId, requestingOwnerId);
        return entityMapper.mapToOrnamentResponse(ornament);
    }

    public List<OrnamentResponse> getOrnamentsByAquarium(Long aquariumId) {
        return ornamentRepository.findByAquariumId(aquariumId).stream()
                .map(entityMapper::mapToOrnamentResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrnamentResponse createOrnament(OrnamentRequest request, Long ownerId) {
        validateOwnerExists(ownerId);
        validateAquariumAssignment(request.aquariumId(), ownerId);

        Ornament ornament = Ornament.create(
                request.name(),
                ownerId,
                Optional.ofNullable(request.description()),
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.material()),
                Optional.ofNullable(request.getIsAirPumpCompatibleValue())
        );

        if (request.aquariumId() != null) {
            ornament.assignToAquarium(request.aquariumId(), ownerId);
        }

        Ornament savedOrnament = ornamentRepository.insert(ornament);
        return entityMapper.mapToOrnamentResponse(savedOrnament);
    }

    @Transactional
    public OrnamentResponse updateOrnament(Long ornamentId, OrnamentRequest request, Long requestingOwnerId) {
        Ornament ornament = findOwnedOrnament(ornamentId, requestingOwnerId);
        validateAquariumAssignment(request.aquariumId(), requestingOwnerId);

        ornament.update(
                Optional.ofNullable(request.name()),
                Optional.ofNullable(request.description()),
                Optional.ofNullable(request.color()),
                Optional.ofNullable(request.material()),
                Optional.ofNullable(request.getIsAirPumpCompatibleValue())
        );

        if (request.aquariumId() != null) {
            ornament.assignToAquarium(request.aquariumId(), requestingOwnerId);
        } else {
            ornament.removeFromAquarium(requestingOwnerId);
        }

        Ornament updatedOrnament = ornamentRepository.update(ornament);
        return entityMapper.mapToOrnamentResponse(updatedOrnament);
    }

    @Transactional
    public void deleteOrnament(Long ornamentId, Long requestingOwnerId) {
        findOwnedOrnament(ornamentId, requestingOwnerId); // Validates ownership
        ornamentRepository.deleteById(ornamentId);
        log.info("Ornament {} deleted by owner {}", ornamentId, requestingOwnerId);
    }

    private Ornament findOwnedOrnament(Long ornamentId, Long requestingOwnerId) {
        Ornament ornament = ornamentRepository.findById(ornamentId)
                .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));
        ornament.validateOwnership(requestingOwnerId);
        return ornament;
    }

    private void validateOwnerExists(Long ownerId) {
        if (!ownerRepository.findById(ownerId).isPresent()) {
            throw new ApplicationException.NotFoundException("Owner", ownerId);
        }
    }

    private void validateAquariumAssignment(Long aquariumId, Long ownerId) {
        if (aquariumId != null) {
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));
            aquarium.validateOwnership(ownerId);
        }
    }
} 