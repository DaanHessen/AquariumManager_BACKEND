package nl.hu.bep.presentation.dto.mapper;

import nl.hu.bep.domain.*;
import nl.hu.bep.domain.accessories.*;
import nl.hu.bep.presentation.dto.response.*;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Collections;

/**
 * Streamlined DTO mapper service for entity-to-response conversions.
 * Focused on clean, maintainable mapping with consistent null handling.
 * Service layer orchestrates business logic; this mapper handles pure data transformation.
 */
@ApplicationScoped
public class EntityMapper {

    // ========== AQUARIUM MAPPING ==========
    
    /**
     * Maps an Aquarium domain entity to a basic response DTO.
     * @param aquarium the domain entity to map
     * @return AquariumResponse with basic fields populated
     */
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
                null, // ownerEmail - populated separately when needed
                Collections.emptyList(), // inhabitants - populated separately for detailed views
                Collections.emptyList(), // accessories - populated separately for detailed views
                Collections.emptyList(), // ornaments - populated separately for detailed views
                aquarium.getColor(),
                aquarium.getDescription(),
                aquarium.getDateCreated()
        );
    }

    /**
     * Maps an Aquarium to response with complete relationship data.
     * Used for detailed aquarium views that include all associated entities.
     * @param aquarium the domain entity
     * @param inhabitants mapped inhabitant responses
     * @param accessories mapped accessory responses  
     * @param ornaments mapped ornament responses
     * @param ownerEmail optional owner email
     * @return complete AquariumResponse with all relationships
     */
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

    // ========== ACCESSORY MAPPING ==========
    
    /**
     * Maps an Accessory domain entity to response DTO.
     * Handles different accessory types (Filter, Lighting, Thermostat) with type-specific fields.
     * @param accessory the domain entity to map
     * @return AccessoryResponse with type-appropriate fields populated
     */
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
                        null, // isLed - not applicable
                        null, // turnOnTime - not applicable
                        null, // turnOffTime - not applicable
                        null, // minTemperature - not applicable
                        null, // maxTemperature - not applicable
                        null, // currentTemperature - not applicable
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
                        null, // isExternal - not applicable
                        null, // capacityLiters - not applicable
                        lighting.isLed(),
                        lighting.getTurnOnTime(),
                        lighting.getTurnOffTime(),
                        null, // minTemperature - not applicable
                        null, // maxTemperature - not applicable
                        null, // currentTemperature - not applicable
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
                        null, // isExternal - not applicable
                        null, // capacityLiters - not applicable
                        null, // isLed - not applicable
                        null, // turnOnTime - not applicable
                        null, // turnOffTime - not applicable
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
    
    /**
     * Maps an Ornament domain entity to response DTO.
     * @param ornament the domain entity to map
     * @return OrnamentResponse with all fields populated
     */
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

    // ========== INHABITANT MAPPING ==========
    
    /**
     * Maps an Inhabitant domain entity to response DTO.
     * @param inhabitant the domain entity to map
     * @return InhabitantResponse with all fields populated
     */
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

    // ========== BULK MAPPING UTILITIES ==========
    
    /**
     * Maps a list of entities to a list of responses using the appropriate mapper method.
     * @param aquariums list of aquarium entities
     * @return list of aquarium responses
     */
    public List<AquariumResponse> mapToAquariumResponses(List<Aquarium> aquariums) {
        if (aquariums == null) {
            return Collections.emptyList();
        }
        return aquariums.stream()
                .map(this::mapToAquariumResponse)
                .toList();
    }

    /**
     * Maps a list of accessory entities to responses.
     * @param accessories list of accessory entities
     * @return list of accessory responses
     */
    public List<AccessoryResponse> mapToAccessoryResponses(List<Accessory> accessories) {
        if (accessories == null) {
            return Collections.emptyList();
        }
        return accessories.stream()
                .map(this::mapToAccessoryResponse)
                .toList();
    }

    /**
     * Maps a list of ornament entities to responses.
     * @param ornaments list of ornament entities
     * @return list of ornament responses
     */
    public List<OrnamentResponse> mapToOrnamentResponses(List<Ornament> ornaments) {
        if (ornaments == null) {
            return Collections.emptyList();
        }
        return ornaments.stream()
                .map(this::mapToOrnamentResponse)
                .toList();
    }

    /**
     * Maps a list of inhabitant entities to responses.
     * @param inhabitants list of inhabitant entities
     * @return list of inhabitant responses
     */
    public List<InhabitantResponse> mapToInhabitantResponses(List<Inhabitant> inhabitants) {
        if (inhabitants == null) {
            return Collections.emptyList();
        }
        return inhabitants.stream()
                .map(this::mapToInhabitantResponse)
                .toList();
    }
}