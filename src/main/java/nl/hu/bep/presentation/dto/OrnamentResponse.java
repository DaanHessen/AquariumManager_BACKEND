package nl.hu.bep.presentation.dto;

import java.time.LocalDateTime;

public record OrnamentResponse(
        Long id,
        String name,
        String color,
        String material,
        String description,
        LocalDateTime dateCreated,
        Boolean isAirPumpCompatible) {
}