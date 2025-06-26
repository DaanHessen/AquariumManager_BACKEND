package nl.hu.bep.presentation.dto.request;

import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AquariumRequest(
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    String name,
    
    @NotNull(message = "Length is required")
    @Positive(message = "Length must be positive")
    Double length,
    
    @NotNull(message = "Width is required")
    @Positive(message = "Width must be positive")
    Double width,
    
    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    Double height,
    
    @NotNull(message = "Substrate type is required")
    SubstrateType substrate,
    
    @NotNull(message = "Water type is required")
    WaterType waterType,
    
    @Size(max = 50, message = "Color must be less than 50 characters")
    String color,
    
    @Size(max = 255, message = "Description must be less than 255 characters")
    String description,
    
    AquariumState state
) {} 