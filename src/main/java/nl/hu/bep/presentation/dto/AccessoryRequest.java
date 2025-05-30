package nl.hu.bep.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccessoryRequest(
        @NotBlank(message = "Model cannot be empty")
        @Size(min = 1, max = 50, message = "Model must be between 1 and 50 characters")
        String model,
        
        @NotBlank(message = "Serial number cannot be empty")
        @Size(min = 1, max = 50, message = "Serial number must be between 1 and 50 characters")
        String serialNumber,
        
        @NotBlank(message = "Type cannot be empty")
        String type,
        
        Long aquariumId,

        Boolean isExternal,
        Integer capacityLiters,

        Boolean isLED,
        
        @Size(max = 50, message = "Color must be less than 50 characters")
        String color,
        
        @Size(max = 255, message = "Description must be less than 255 characters")
        String description,
        
        String timeOn,
        String timeOff,

        Double minTemperature,
        Double maxTemperature,
        Double currentTemperature) {
    public boolean getIsExternalValue() {
        return isExternal != null ? isExternal : false;
    }

    public int getCapacityLitersValue() {
        return capacityLiters != null ? capacityLiters : 0;
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