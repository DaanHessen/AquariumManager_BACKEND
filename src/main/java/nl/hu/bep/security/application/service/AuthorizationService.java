package nl.hu.bep.security.application.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.application.exception.ApplicationException;
import nl.hu.bep.data.AccessoryRepository;
import nl.hu.bep.data.AquariumRepository;
import nl.hu.bep.data.InhabitantRepository;
import nl.hu.bep.data.OrnamentRepository;
import nl.hu.bep.domain.Accessory;
import nl.hu.bep.domain.Aquarium;
import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.Ornament;
import nl.hu.bep.presentation.dto.AccessoryRequest;
import nl.hu.bep.presentation.dto.InhabitantRequest;
import nl.hu.bep.presentation.dto.OrnamentRequest;

@Singleton
@Slf4j
public class AuthorizationService {

    private final AquariumRepository aquariumRepository;
    private final InhabitantRepository inhabitantRepository;
    private final AccessoryRepository accessoryRepository;
    private final OrnamentRepository ornamentRepository;

    @Inject
    public AuthorizationService(
            AquariumRepository aquariumRepository,
            InhabitantRepository inhabitantRepository,
            AccessoryRepository accessoryRepository,
            OrnamentRepository ornamentRepository) {
        this.aquariumRepository = aquariumRepository;
        this.inhabitantRepository = inhabitantRepository;
        this.accessoryRepository = accessoryRepository;
        this.ornamentRepository = ornamentRepository;
    }

    public boolean verifyAquariumOwnership(Long aquariumId, Long ownerId) {
        log.debug("Verifying ownership for aquarium {} by owner {}", aquariumId, ownerId);

        try {
            Aquarium aquarium = aquariumRepository.findById(aquariumId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Aquarium", aquariumId));

            boolean isOwner = aquarium.isOwnedBy(ownerId);
            log.debug("Owner {} is {} the owner of aquarium {}",
                    ownerId, isOwner ? "" : "not", aquariumId);

            return isOwner;
        } catch (ApplicationException.NotFoundException e) {
            log.error("Aquarium not found during ownership verification: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error during aquarium ownership verification: {}", e.getMessage());
            return false;
        }
    }

    public boolean verifyInhabitantOwnership(Long inhabitantId, Long ownerId) {
        log.debug("Verifying ownership for inhabitant {} by owner {}", inhabitantId, ownerId);

        try {
            Inhabitant inhabitant = inhabitantRepository.findById(inhabitantId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));

            if (inhabitant.getAquarium() == null) {
                log.warn("Inhabitant {} is not assigned to any aquarium", inhabitantId);
                return false;
            }

            return verifyAquariumOwnership(inhabitant.getAquarium().getId(), ownerId);
        } catch (ApplicationException.NotFoundException e) {
            log.error("Inhabitant not found during ownership verification: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error during inhabitant ownership verification: {}", e.getMessage());
            return false;
        }
    }

    public boolean verifyAccessoryOwnership(Long accessoryId, Long ownerId) {
        log.debug("Verifying ownership for accessory {} by owner {}", accessoryId, ownerId);

        try {
            Accessory accessory = accessoryRepository.findById(accessoryId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Accessory", accessoryId));

            if (accessory.getAquarium() == null) {
                log.warn("Accessory {} is not assigned to any aquarium", accessoryId);
                return false;
            }

            return verifyAquariumOwnership(accessory.getAquarium().getId(), ownerId);
        } catch (ApplicationException.NotFoundException e) {
            log.error("Accessory not found during ownership verification: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error during accessory ownership verification: {}", e.getMessage());
            return false;
        }
    }

    public boolean verifyOrnamentOwnership(Long ornamentId, Long ownerId) {
        log.debug("Verifying ownership for ornament {} by owner {}", ornamentId, ownerId);

        try {
            Ornament ornament = ornamentRepository.findById(ornamentId)
                    .orElseThrow(() -> new ApplicationException.NotFoundException("Ornament", ornamentId));

            if (ornament.getAquarium() == null) {
                log.warn("Ornament {} is not assigned to any aquarium", ornamentId);
                return false;
            }

            return verifyAquariumOwnership(ornament.getAquarium().getId(), ownerId);
        } catch (ApplicationException.NotFoundException e) {
            log.error("Ornament not found during ownership verification: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error during ornament ownership verification: {}", e.getMessage());
            return false;
        }
    }

    public boolean verifyCanAddInhabitantToAquarium(Long inhabitantId, Long aquariumId, Long ownerId) {
        log.debug("Verifying if owner {} can add inhabitant {} to aquarium {}",
                ownerId, inhabitantId, aquariumId);

        return verifyAquariumOwnership(aquariumId, ownerId);
    }

    public boolean verifyCanUpdateInhabitant(Long inhabitantId, InhabitantRequest updatedRequest, Long ownerId) {
        log.debug("Verifying if owner {} can update inhabitant {}", ownerId, inhabitantId);

        boolean canUpdate = verifyInhabitantOwnership(inhabitantId, ownerId);

        if (canUpdate && updatedRequest.aquariumId() != null) {
            try {
                Inhabitant currentInhabitant = inhabitantRepository.findById(inhabitantId)
                        .orElseThrow(() -> new ApplicationException.NotFoundException("Inhabitant", inhabitantId));

                if (currentInhabitant.getAquarium() != null &&
                        !currentInhabitant.getAquarium().getId().equals(updatedRequest.aquariumId())) {
                    log.debug("Inhabitant update involves moving to a different aquarium");
                    canUpdate = verifyAquariumOwnership(updatedRequest.aquariumId(), ownerId);
                }
            } catch (Exception e) {
                log.error("Error checking aquarium change during inhabitant update: {}", e.getMessage());
                return false;
            }
        }

        return canUpdate;
    }

    public boolean verifyCanCreateInhabitant(InhabitantRequest request, Long ownerId) {
        log.debug("Verifying if owner {} can create inhabitant with data {}", ownerId, request);

        if (request.aquariumId() == null) {
            return true;
        }

        return verifyAquariumOwnership(request.aquariumId(), ownerId);
    }

    public boolean verifyCanCreateAccessory(AccessoryRequest request, Long ownerId) {
        log.debug("Verifying if owner {} can create accessory", ownerId);

        if (request.aquariumId() == null) {
            return true;
        }

        return verifyAquariumOwnership(request.aquariumId(), ownerId);
    }

    public boolean verifyCanCreateOrnament(OrnamentRequest request, Long ownerId) {
        log.debug("Verifying if owner {} can create ornament", ownerId);

        if (request.aquariumId() == null) {
            return true;
        }

        return verifyAquariumOwnership(request.aquariumId(), ownerId);
    }
}