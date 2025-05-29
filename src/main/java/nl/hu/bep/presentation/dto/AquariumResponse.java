package nl.hu.bep.presentation.dto;

import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;

import java.time.LocalDateTime;
import java.util.List;

public record AquariumResponse(
    Long id,
    String name,
    Double length,
    Double width,
    Double height,
    Double volumeInLiters,
    SubstrateType substrate,
    WaterType waterType,
    AquariumState state,
    LocalDateTime currentStateStartTime,
    Long currentStateDurationMinutes,
    Long ownerId,
    String ownerEmail,
    List<InhabitantResponse> inhabitants,
    List<AccessoryResponse> accessories,
    List<OrnamentResponse> ornaments,
    String color,
    String description,
    LocalDateTime dateCreated
) {} 