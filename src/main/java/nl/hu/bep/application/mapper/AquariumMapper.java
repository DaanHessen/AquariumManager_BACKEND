package nl.hu.bep.application.mapper;

import jakarta.inject.Singleton;
import nl.hu.bep.domain.*;
import nl.hu.bep.domain.species.Fish;
import nl.hu.bep.presentation.dto.*;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class AquariumMapper {

    public AquariumResponse toAquariumResponse(Aquarium aquarium) {
        return toAquariumResponse(aquarium, false);
    }

    public AquariumResponse toAquariumResponse(Aquarium aquarium, Boolean includeCollections) {
        if (aquarium == null) {
            return null;
        }

        return new AquariumResponse(
                aquarium.getId(),
                aquarium.getName(),
                aquarium.getDimensions().getLength(),
                aquarium.getDimensions().getWidth(),
                aquarium.getDimensions().getHeight(),
                aquarium.getDimensions().getVolumeInLiters(),
                aquarium.getSubstrate(),
                aquarium.getWaterType(),
                aquarium.getState(),
                aquarium.getOwner() != null ? aquarium.getOwner().getId() : null,
                aquarium.getOwner() != null ? aquarium.getOwner().getEmail() : null,
                mapInhabitants(aquarium, includeCollections),
                mapAccessories(aquarium, includeCollections),
                mapOrnaments(aquarium, includeCollections),
                aquarium.getColor(),
                aquarium.getDescription(),
                aquarium.getDateCreated());
    }

    public List<AquariumResponse> toAquariumResponseList(List<Aquarium> aquariums) {
        return toAquariumResponseList(aquariums, false);
    }

    public List<AquariumResponse> toAquariumResponseList(List<Aquarium> aquariums, Boolean includeCollections) {
        if (aquariums == null) {
            return Collections.emptyList();
        }
        return aquariums.stream()
                .map(aquarium -> toAquariumResponse(aquarium, includeCollections))
                .toList();
    }

    private List<InhabitantResponse> mapInhabitants(Aquarium aquarium, Boolean includeCollections) {
        if (Boolean.FALSE.equals(includeCollections) || aquarium.getInhabitants() == null) {
            return Collections.emptyList();
        }
        return toInhabitantResponseList(aquarium.getInhabitants());
    }

    private List<AccessoryResponse> mapAccessories(Aquarium aquarium, Boolean includeCollections) {
        if (Boolean.FALSE.equals(includeCollections) || aquarium.getAccessories() == null) {
            return Collections.emptyList();
        }
        return toAccessoryResponseList(aquarium.getAccessories());
    }

    private List<OrnamentResponse> mapOrnaments(Aquarium aquarium, Boolean includeCollections) {
        if (Boolean.FALSE.equals(includeCollections) || aquarium.getOrnaments() == null) {
            return Collections.emptyList();
        }
        return toOrnamentResponseList(aquarium.getOrnaments());
    }

    public InhabitantResponse toInhabitantResponse(Inhabitant inhabitant) {
        if (inhabitant == null) {
            return null;
        }

        return new InhabitantResponse(
                inhabitant.getId(),
                inhabitant.getSpecies(),
                inhabitant.getColor(),
                inhabitant.getDescription(),
                inhabitant.getDateCreated(),
                inhabitant.getCount(),
                inhabitant.isSchooling(),
                inhabitant.getWaterType(),
                inhabitant.getAquarium() != null ? inhabitant.getAquarium().getId() : null,
                inhabitant.getClass().getSimpleName(),
                getIsAggressiveEater(inhabitant),
                getRequiresSpecialFood(inhabitant),
                getIsSnailEater(inhabitant));
    }

    public List<InhabitantResponse> toInhabitantResponseList(Set<Inhabitant> inhabitants) {
        if (inhabitants == null) {
            return Collections.emptyList();
        }
        return inhabitants.stream()
                .map(this::toInhabitantResponse)
                .collect(Collectors.toList());
    }

    private boolean getIsAggressiveEater(Inhabitant inhabitant) {
        if (inhabitant instanceof Fish) {
            return ((Fish) inhabitant).isAggressiveEater();
        }
        return false;
    }

    private boolean getRequiresSpecialFood(Inhabitant inhabitant) {
        if (inhabitant instanceof Fish) {
            return ((Fish) inhabitant).isRequiresSpecialFood();
        }
        return false;
    }

    private boolean getIsSnailEater(Inhabitant inhabitant) {
        if (inhabitant instanceof Fish) {
            return ((Fish) inhabitant).isSnailEater();
        }
        return false;
    }

    public AccessoryResponse toAccessoryResponse(Accessory accessory) {
        if (accessory == null) {
            return null;
        }

        String type = accessory.getClass().getSimpleName();

        Boolean isExternal = null;
        Integer capacityLiters = null;
        Boolean isLed = null;
        LocalTime turnOnTime = null;
        LocalTime turnOffTime = null;
        Double minTemperature = null;
        Double maxTemperature = null;
        Double currentTemperature = null;

        switch (type) {
            case "Filter" -> {
                nl.hu.bep.domain.accessories.Filter filter = (nl.hu.bep.domain.accessories.Filter) accessory;
                isExternal = filter.isExternal();
                capacityLiters = filter.getCapacityLiters();
            }
            case "Lighting" -> {
                nl.hu.bep.domain.accessories.Lighting lighting = (nl.hu.bep.domain.accessories.Lighting) accessory;
                isLed = lighting.isLed();
                turnOnTime = lighting.getTurnOnTime();
                turnOffTime = lighting.getTurnOffTime();
            }
            case "Thermostat" -> {
                nl.hu.bep.domain.accessories.Thermostat thermostat = (nl.hu.bep.domain.accessories.Thermostat) accessory;
                minTemperature = thermostat.getMinTemperature();
                maxTemperature = thermostat.getMaxTemperature();
                currentTemperature = thermostat.getCurrentTemperature();
            }
        }

        return new AccessoryResponse(
                accessory.getId(),
                accessory.getModel(),
                accessory.getSerialNumber(),
                accessory.getClass().getSimpleName(),
                isExternal,
                capacityLiters,
                isLed,
                turnOnTime,
                turnOffTime,
                minTemperature,
                maxTemperature,
                currentTemperature,
                accessory.getColor(),
                accessory.getDescription(),
                accessory.getDateCreated());
    }

    public List<AccessoryResponse> toAccessoryResponseList(Set<Accessory> accessories) {
        if (accessories == null) {
            return Collections.emptyList();
        }
        return accessories.stream()
                .map(this::toAccessoryResponse)
                .collect(Collectors.toList());
    }

    public OrnamentResponse toOrnamentResponse(Ornament ornament) {
        if (ornament == null) {
            return null;
        }

        return new OrnamentResponse(
                ornament.getId(),
                ornament.getName(),
                ornament.getColor(),
                ornament.getMaterial(),
                ornament.getDescription(),
                ornament.getDateCreated(),
                ornament.isAirPumpCompatible());
    }

    public List<OrnamentResponse> toOrnamentResponseList(Set<Ornament> ornaments) {
        if (ornaments == null) {
            return Collections.emptyList();
        }
        return ornaments.stream()
                .map(this::toOrnamentResponse)
                .collect(Collectors.toList());
    }

    public AquariumStateHistoryResponse toStateHistoryResponse(AquariumStateHistory stateHistory) {
        if (stateHistory == null) {
            return null;
        }

        return new AquariumStateHistoryResponse(
                stateHistory.getId(),
                stateHistory.getState(),
                stateHistory.getStartTime(),
                stateHistory.getEndTime(),
                stateHistory.getDurationMinutes(),
                stateHistory.isActive(),
                stateHistory.getCreatedAt()
        );
    }
}