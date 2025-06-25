package nl.hu.bep.presentation.dto.response;

import java.time.LocalDateTime;

public record OrnamentResponse(
        Long id,
        String name,
        String material,
        // String size,
        String color,
        String description,
        Boolean isAirPumpCompatible,
        LocalDateTime dateCreated,
        Long ownerId,
        Long aquariumId) {
}