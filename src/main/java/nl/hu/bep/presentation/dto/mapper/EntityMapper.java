package nl.hu.bep.presentation.dto.mapper;

import nl.hu.bep.domain.*;
import nl.hu.bep.domain.accessories.*;
import nl.hu.bep.presentation.dto.response.*;

import java.util.List;
import java.util.Collections;

public class EntityMapper {

    public AquariumResponse mapToAquariumResponse(Aquarium aquarium) {
        if (aquarium == null) {
            return null;
        }
        
        return new AquariumResponse(
                aquarium.getId(),
                aquarium.getName(),
                aquarium.getDimensions().getLength(),
                aquarium.getDimensions().getWidth(),
                aquarium.getDimensions().getHeight(),
                aquarium.getVolume(),
                aquarium.getSubstrate(),
                aquarium.getWaterType(),
                aquarium.getState(),
                aquarium.getCurrentStateStartTime(),
                aquarium.getCurrentStateDurationMinutes(),
                aquarium.getOwnerId(),
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                aquarium.getColor(),
                aquarium.getDescription(),
                aquarium.getDateCreated()
        );
    }

    public AquariumResponse mapToDetailedAquariumResponse(Aquarium aquarium, 
                                                         List<InhabitantResponse> inhabitants,
                                                         List<AccessoryResponse> accessories,
                                                         List<OrnamentResponse> ornaments,
                                                         String ownerEmail) {
        if (aquarium == null) {
            return null;
        }
        
        return new AquariumResponse(
                aquarium.getId(),
                aquarium.getName(),
                aquarium.getDimensions().getLength(),
                aquarium.getDimensions().getWidth(),
                aquarium.getDimensions().getHeight(),
                aquarium.getVolume(),
                aquarium.getSubstrate(),
                aquarium.getWaterType(),
                aquarium.getState(),
                aquarium.getCurrentStateStartTime(),
                aquarium.getCurrentStateDurationMinutes(),
                aquarium.getOwnerId(),
                ownerEmail,
                inhabitants != null ? inhabitants : Collections.emptyList(),
                accessories != null ? accessories : Collections.emptyList(),
                ornaments != null ? ornaments : Collections.emptyList(),
                aquarium.getColor(),
                aquarium.getDescription(),
                aquarium.getDateCreated()
        );
    }

    public AccessoryResponse mapToAccessoryResponse(Accessory accessory) {
        if (accessory == null) {
            return null;
        }

        return switch (accessory.getAccessoryType().toLowerCase()) {
            case "filter" -> {
                Filter filter = (Filter) accessory;
                yield new AccessoryResponse(
                        accessory.getId(),
                        accessory.getModel(),
                        accessory.getSerialNumber(),
                        "Filter",
                        filter.isExternal(),
                        filter.getCapacityLiters(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        accessory.getColor(),
                        accessory.getDescription(),
                        accessory.getDateCreated()
                );
            }
            case "lighting" -> {
                Lighting lighting = (Lighting) accessory;
                yield new AccessoryResponse(
                        accessory.getId(),
                        accessory.getModel(),
                        accessory.getSerialNumber(),
                        "Lighting",
                        null,
                        null,
                        lighting.isLed(),
                        lighting.getTurnOnTime(),
                        lighting.getTurnOffTime(),
                        null,
                        null,
                        null,
                        accessory.getColor(),
                        accessory.getDescription(),
                        accessory.getDateCreated()
                );
            }
            case "thermostat" -> {
                Thermostat thermostat = (Thermostat) accessory;
                yield new AccessoryResponse(
                        accessory.getId(),
                        accessory.getModel(),
                        accessory.getSerialNumber(),
                        "Thermostat",
                        null,
                        null,
                        null,
                        null,
                        null,
                        thermostat.getMinTemperature(),
                        thermostat.getMaxTemperature(),
                        thermostat.getCurrentTemperature(),
                        accessory.getColor(),
                        accessory.getDescription(),
                        accessory.getDateCreated()
                );
            }
            default -> throw new IllegalArgumentException("Unknown accessory type: " + accessory.getAccessoryType());
        };
    }


    public OrnamentResponse mapToOrnamentResponse(Ornament ornament) {
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
                ornament.isAirPumpCompatible()
        );
    }

    public InhabitantResponse mapToInhabitantResponse(Inhabitant inhabitant) {
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
                inhabitant.getAquariumId(),
                inhabitant.getType(),
                inhabitant.getAggressiveEater(),
                inhabitant.getRequiresSpecialFood(),
                inhabitant.getSnailEater()
        );
    }

    public List<AquariumResponse> mapToAquariumResponses(List<Aquarium> aquariums) {
        if (aquariums == null) {
            return Collections.emptyList();
        }
        return aquariums.stream()
                .map(this::mapToAquariumResponse)
                .toList();
    }

    public List<AccessoryResponse> mapToAccessoryResponses(List<Accessory> accessories) {
        if (accessories == null) {
            return Collections.emptyList();
        }
        return accessories.stream()
                .map(this::mapToAccessoryResponse)
                .toList();
    }

    public List<OrnamentResponse> mapToOrnamentResponses(List<Ornament> ornaments) {
        if (ornaments == null) {
            return Collections.emptyList();
        }
        return ornaments.stream()
                .map(this::mapToOrnamentResponse)
                .toList();
    }

    public List<InhabitantResponse> mapToInhabitantResponses(List<Inhabitant> inhabitants) {
        if (inhabitants == null) {
            return Collections.emptyList();
        }
        return inhabitants.stream()
                .map(this::mapToInhabitantResponse)
                .toList();
    }
}