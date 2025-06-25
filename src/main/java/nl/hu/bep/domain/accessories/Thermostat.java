package nl.hu.bep.domain.accessories;

import nl.hu.bep.domain.Accessory;
import lombok.*;

/**
 * Represents a temperature control accessory for aquariums.

 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Thermostat extends Accessory {
    private double minTemperature;
    private double maxTemperature;
    private double currentTemperature;

    public Thermostat(String model, String serialNumber, double minTemperature, 
                     double maxTemperature, double currentTemperature, Long ownerId) {
        super(model, serialNumber, ownerId);
        this.minTemperature = minTemperature; // Remove duplicate validation - factory already validates
        this.maxTemperature = maxTemperature; // Remove duplicate validation - factory already validates  
        this.currentTemperature = currentTemperature;
    }

    @Override
    public String getAccessoryType() {
        return "Thermostat";
    }

    // Business logic methods
    public void updateProperties(double minTemperature, double maxTemperature, double currentTemperature) {
        if (minTemperature <= 0) {
            throw new IllegalArgumentException("Minimum temperature must be positive");
        }
        if (maxTemperature <= 0) {
            throw new IllegalArgumentException("Maximum temperature must be positive");
        }
        if (minTemperature >= maxTemperature) {
            throw new IllegalArgumentException("Minimum temperature must be less than maximum temperature");
        }
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.currentTemperature = currentTemperature;
    }

    public void adjustMinTemperature(double minTemperature) {
        if (minTemperature <= 0) {
            throw new IllegalArgumentException("Minimum temperature must be positive");
        }
        if (minTemperature >= this.maxTemperature) {
            throw new IllegalArgumentException("Minimum temperature must be less than maximum temperature");
        }
        this.minTemperature = minTemperature;
    }

    public void adjustMaxTemperature(double maxTemperature) {
        if (maxTemperature <= 0) {
            throw new IllegalArgumentException("Maximum temperature must be positive");
        }
        if (this.minTemperature >= maxTemperature) {
            throw new IllegalArgumentException("Minimum temperature must be less than maximum temperature");
        }
        this.maxTemperature = maxTemperature;
    }

    public void updateCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    // Business calculations
    public boolean isWithinRange() {
        return currentTemperature >= minTemperature && currentTemperature <= maxTemperature;
    }

    public boolean requiresHeating() {
        return currentTemperature < minTemperature;
    }

    public boolean requiresCooling() {
        return currentTemperature > maxTemperature;
    }

    public double getTemperatureVariance() {
        return maxTemperature - minTemperature;
    }

    public String getTemperatureStatus() {
        if (requiresHeating()) return "TOO_COLD";
        if (requiresCooling()) return "TOO_HOT";
        return "OPTIMAL";
    }

    // Repository access methods
    @Override
    public boolean isExternal() { return false; } // Thermostats don't have external
    
    @Override
    public int getCapacityLiters() { return 0; } // Thermostats don't have capacity
    
    @Override
    public boolean isLed() { return false; } // Thermostats don't have LED
    
    @Override
    public java.time.LocalTime getTurnOnTime() { return null; } // Thermostats don't have time settings
    
    @Override
    public java.time.LocalTime getTurnOffTime() { return null; } // Thermostats don't have time settings
    
    @Override
    public double getMinTemperature() { return minTemperature; }
    
    @Override
    public double getMaxTemperature() { return maxTemperature; }
    
    @Override
    public double getCurrentTemperature() { return currentTemperature; }
}
