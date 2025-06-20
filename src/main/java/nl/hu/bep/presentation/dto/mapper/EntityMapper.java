package nl.hu.bep.presentation.dto.mapper;

import nl.hu.bep.domain.*;
import nl.hu.bep.domain.accessories.*;
import nl.hu.bep.domain.species.*;
import nl.hu.bep.presentation.dto.*;

import java.util.List;

/**
 * Clean DTO mapper service - separates mapping concerns from business logic.
 * Service layer should NOT contain DTO mapping logic.
 */
public class EntityMapper {

    // ========== AQUARIUM MAPPING ==========
    
    public AquariumResponse mapToAquariumResponse(Aquarium aquarium) {
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
                null, // ownerEmail - not required in current implementation
                List.of(), // inhabitants - basic response without relationships
                List.of(), // accessories - basic response without relationships
                List.of(), // ornaments - basic response without relationships
                aquarium.getColor(),
                aquarium.getDescription(),
                aquarium.getDateCreated()
        );
    }

    /**
     * Enhanced mapper for detailed aquarium responses with full relationships.
     * Used for detailed views that include all associated entities.
     */
    public AquariumResponse mapToDetailedAquariumResponse(Aquarium aquarium, 
                                                         List<InhabitantResponse> inhabitants,
                                                         List<AccessoryResponse> accessories,
                                                         List<OrnamentResponse> ornaments) {
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
                null, // ownerEmail - not required in current implementation
                inhabitants != null ? inhabitants : List.of(),
                accessories != null ? accessories : List.of(),
                ornaments != null ? ornaments : List.of(),
                aquarium.getColor(),
                aquarium.getDescription(),
                aquarium.getDateCreated()
        );
    }

    /**
     * Enhanced mapper with owner email lookup.
     * Used when owner information is available and needed.
     */
    public AquariumResponse mapToAquariumResponseWithOwner(Aquarium aquarium, String ownerEmail) {
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
                List.of(), // inhabitants - basic response
                List.of(), // accessories - basic response  
                List.of(), // ornaments - basic response
                aquarium.getColor(),
                aquarium.getDescription(),
                aquarium.getDateCreated()
        );
    }

    // ========== ACCESSORY MAPPING ==========
    
    public AccessoryResponse mapToAccessoryResponse(Accessory accessory) {
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
                        null, // isLed
                        null, // turnOnTime
                        null, // turnOffTime
                        null, // minTemperature
                        null, // maxTemperature
                        null, // currentTemperature
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
                        null, // isExternal
                        null, // capacityLiters
                        lighting.isLed(),
                        lighting.getTurnOnTime(),
                        lighting.getTurnOffTime(),
                        null, // minTemperature
                        null, // maxTemperature
                        null, // currentTemperature
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
                        null, // isExternal
                        null, // capacityLiters
                        null, // isLed
                        null, // turnOnTime
                        null, // turnOffTime
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

    // ========== ORNAMENT MAPPING ==========
    
    public OrnamentResponse mapToOrnamentResponse(Ornament ornament) {
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

    // ========== INHABITANT MAPPING ==========
    
    public InhabitantResponse mapToInhabitantResponse(Inhabitant inhabitant) {
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
                inhabitant instanceof Fish ? ((Fish) inhabitant).isAggressiveEater() : null,
                inhabitant instanceof Fish ? ((Fish) inhabitant).isRequiresSpecialFood() : null,
                inhabitant instanceof Fish ? ((Fish) inhabitant).isSnailEater() : 
                        (inhabitant instanceof Snail ? ((Snail) inhabitant).isSnailEater() : null)
        );
    }
} 