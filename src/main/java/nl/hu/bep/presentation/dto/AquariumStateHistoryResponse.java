package nl.hu.bep.presentation.dto;

import nl.hu.bep.domain.enums.AquariumState;

import java.time.LocalDateTime;

public record AquariumStateHistoryResponse(
    Long id,
    AquariumState state,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Long durationMinutes,
    boolean isActive,
    LocalDateTime createdAt
) {} 