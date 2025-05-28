package nl.hu.bep.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccessoryRequest(
        String model,
        String serialNumber,
        String type,
        Long aquariumId,

        Boolean isExternal,
        Integer capacityInLiters,

        Boolean isLED,
        String color,
        String description,
        String timeOn,
        String timeOff,

        Double minTemperature,
        Double maxTemperature,
        Double currentTemperature) {
    public boolean getIsExternalValue() {
        return isExternal != null ? isExternal : false;
    }

    public int getCapacityInLitersValue() {
        return capacityInLiters != null ? capacityInLiters : 0;
    }

    public boolean getIsLEDValue() {
        return isLED != null ? isLED : false;
    }

    public String getColorValue() {
        return color != null ? color : "white";
    }

    public String getDescriptionValue() {
        return description != null ? description : "";
    }

    public LocalTime getTimeOnValue() {
        return timeOn != null ? LocalTime.parse(timeOn) : LocalTime.of(8, 0);
    }

    public LocalTime getTimeOffValue() {
        return timeOff != null ? LocalTime.parse(timeOff) : LocalTime.of(20, 0);
    }

    public double getMinTemperatureValue() {
        return minTemperature != null ? minTemperature : 20.0;
    }

    public double getMaxTemperatureValue() {
        return maxTemperature != null ? maxTemperature : 30.0;
    }

    public double getCurrentTemperatureValue() {
        return currentTemperature != null ? currentTemperature : 25.0;
    }
}