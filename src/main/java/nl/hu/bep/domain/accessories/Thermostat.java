package nl.hu.bep.domain.accessories;

import nl.hu.bep.domain.Accessory;
import nl.hu.bep.exception.ApplicationException.BusinessRuleException;

import java.time.LocalTime;
import lombok.*;

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
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.currentTemperature = currentTemperature;
    }

    @Override
    public String getAccessoryType() {
        return "Thermostat";
    }

    public void updateProperties(double minTemperature, double maxTemperature, double currentTemperature) {
        if (minTemperature <= 0) {
            throw new BusinessRuleException("Minimum temperature must be positive");
        }
        if (maxTemperature <= 0) {
            throw new BusinessRuleException("Maximum temperature must be positive");
        }
        if (minTemperature >= maxTemperature) {
            throw new BusinessRuleException("Minimum temperature must be less than maximum temperature");
        }
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.currentTemperature = currentTemperature;
    }

    public void adjustMinTemperature(double minTemperature) {
        if (minTemperature <= 0) {
            throw new BusinessRuleException("Minimum temperature must be positive");
        }
        if (minTemperature >= this.maxTemperature) {
            throw new BusinessRuleException("Minimum temperature must be less than maximum temperature");
        }
        this.minTemperature = minTemperature;
    }

    public void adjustMaxTemperature(double maxTemperature) {
        if (maxTemperature <= 0) {
            throw new BusinessRuleException("Maximum temperature must be positive");
        }
        if (this.minTemperature >= maxTemperature) {
            throw new BusinessRuleException("Minimum temperature must be less than maximum temperature");
        }
        this.maxTemperature = maxTemperature;
    }

    public void updateCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

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

    @Override
    public boolean isExternal() { return false; }
    
    @Override
    public int getCapacityLiters() { return 0; }
    
    @Override
    public boolean isLed() { return false; }
    
    @Override
    public LocalTime getTurnOnTime() { return null; }
    
    @Override
    public LocalTime getTurnOffTime() { return null; }
    
    @Override
    public double getMinTemperature() { return minTemperature; }
    
    @Override
    public double getMaxTemperature() { return maxTemperature; }
    
    @Override
    public double getCurrentTemperature() { return currentTemperature; }
}
