package nl.hu.bep.presentation.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record AccessoryResponse(
        Long id,
        String accessoryType,
        String model,
        String serialNumber,
        String color,
        String description,
        LocalDateTime dateCreated,
        Long ownerId,
        Long aquariumId,
        Boolean isExternal,
        Double capacityLiters,
        Boolean isLed,
        LocalTime timeOn,
        LocalTime timeOff,
        Double minTemperature,
        Double maxTemperature,
        Double currentTemperature) {
}