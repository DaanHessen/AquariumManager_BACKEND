package nl.hu.bep.presentation.dto.response;

import nl.hu.bep.domain.enums.WaterType;

public record InhabitantResponse(
    Long id,
    String species,
    String color,
    String description,
    java.time.LocalDateTime dateCreated,
    Integer count,
    Boolean isSchooling,
    WaterType waterType,
    Long aquariumId,
    String type,
    Boolean isAggressiveEater,
    Boolean requiresSpecialFood,
    Boolean isSnailEater
) { 
    public String getAquariumId() {
        return aquariumId != null ? aquariumId.toString() : "not set yet";
    }
} 