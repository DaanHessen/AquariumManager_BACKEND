package nl.hu.bep.presentation.dto.response;

import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;

import java.time.LocalDateTime;

public record AquariumResponse(
    Long id,
    String name,
    Double length,
    Double width,
    Double height,
    SubstrateType substrate,
    WaterType waterType,
    Double temperature,
    AquariumState state,
    LocalDateTime currentStateStartTime,
    String color,
    String description,
    LocalDateTime dateCreated,
    Long ownerId,
    Long aquariumManagerId
) {} 