package nl.hu.bep.presentation.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record AccessoryResponse(
        Long id,
        String model,
        String serialNumber,
        String type,
        Boolean isExternal,
        Integer capacityLiters,
        Boolean isLed,
        LocalTime turnOnTime,
        LocalTime turnOffTime,
        Double minTemperature,
        Double maxTemperature,
        Double currentTemperature,
        String color,
        String description,
        LocalDateTime dateCreated) {
}