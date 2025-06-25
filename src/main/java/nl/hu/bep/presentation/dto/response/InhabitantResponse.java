package nl.hu.bep.presentation.dto.response;

import nl.hu.bep.domain.enums.WaterType;

public record InhabitantResponse(
    Long id,
    String inhabitantType,
    String species,
    String color,
    Integer count,
    Boolean isSchooling,
    WaterType waterType,
    String name,
    String description,
    java.time.LocalDateTime dateCreated,
    Long ownerId,
    Long aquariumId,
    Boolean isAggressiveEater,
    Boolean requiresSpecialFood,
    Boolean isSnailEater
) {} 