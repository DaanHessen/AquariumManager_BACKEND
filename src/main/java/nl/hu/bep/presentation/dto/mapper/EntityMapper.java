package nl.hu.bep.presentation.dto.mapper;

import nl.hu.bep.domain.*;
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
                aquarium.getSubstrate(),
                aquarium.getWaterType(),
                aquarium.getTemperature(),
                aquarium.getState(),
                aquarium.getCurrentStateStartTime(),
                aquarium.getColor(),
                aquarium.getDescription(),
                aquarium.getDateCreated(),
                aquarium.getOwnerId(),
                aquarium.getAquariumManagerId()
        );
    }

    public AquariumResponse mapToDetailedAquariumResponse(Aquarium aquarium, 
                                                         List<InhabitantResponse> inhabitants,
                                                         List<AccessoryResponse> accessories,
                                                         List<OrnamentResponse> ornaments,
                                                         String ownerEmail) {
        return mapToAquariumResponse(aquarium);
    }

    public AccessoryResponse mapToAccessoryResponse(Accessory accessory) {
        if (accessory == null) {
            return null;
        }
        
        return new AccessoryResponse(
                accessory.getId(),
                accessory.getAccessoryType(),
                accessory.getModel(),
                accessory.getSerialNumber(),
                accessory.getColor(),
                accessory.getDescription(),
                accessory.getDateCreated(),
                accessory.getOwnerId(),
                accessory.getAquariumId(),
                accessory.isExternal(),
                (double) accessory.getCapacityLiters(),
                accessory.isLed(),
                accessory.getTurnOnTime(),
                accessory.getTurnOffTime(),
                accessory.getMinTemperature(),
                accessory.getMaxTemperature(),
                accessory.getCurrentTemperature()
        );
    }

    public OrnamentResponse mapToOrnamentResponse(Ornament ornament) {
        if (ornament == null) {
            return null;
        }
        
        return new OrnamentResponse(
                ornament.getId(),
                ornament.getName(),
                ornament.getMaterial(),
                ornament.getColor(),
                ornament.getDescription(),
                ornament.isAirPumpCompatible(),
                ornament.getDateCreated(),
                ornament.getOwnerId(),
                ornament.getAquariumId()
        );
    }

    public InhabitantResponse mapToInhabitantResponse(Inhabitant inhabitant) {
        if (inhabitant == null) {
            return null;
        }
        
        return new InhabitantResponse(
                inhabitant.getId(),
                inhabitant.getInhabitantType(),
                inhabitant.getSpecies(),
                inhabitant.getColor(),
                inhabitant.getCount(),
                inhabitant.isSchooling(),
                inhabitant.getWaterType(),
                inhabitant.getName(),
                inhabitant.getDescription(),
                inhabitant.getDateCreated(),
                inhabitant.getOwnerId(),
                inhabitant.getAquariumId(),
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