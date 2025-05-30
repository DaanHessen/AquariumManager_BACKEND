package nl.hu.bep.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import nl.hu.bep.domain.enums.AquariumState;

public record StateChangeRequest(
    @NotNull(message = "New state is required")
    AquariumState newState,
    
    @NotNull(message = "Duration in previous state is required")
    @PositiveOrZero(message = "Duration must be zero or positive")
    Long durationInPreviousStateMinutes
) {} 