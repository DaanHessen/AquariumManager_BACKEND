package nl.hu.bep.presentation.dto;

import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;

public record AquariumRequest(
    String name,
    Double length,
    Double width,
    Double height,
    SubstrateType substrate,
    WaterType waterType,
    String color,
    String description,
    AquariumState state
) {} 