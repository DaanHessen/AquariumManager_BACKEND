package nl.hu.bep.domain.accessories;

import nl.hu.bep.domain.Accessory;
import nl.hu.bep.domain.utils.Validator;
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
        this.minTemperature = Validator.positive(minTemperature, "Minimum temperature");
        this.maxTemperature = Validator.positive(maxTemperature, "Maximum temperature");
        this.currentTemperature = currentTemperature;
    }

    @Override
    public String getAccessoryType() {
        return "Thermostat";
    }

    // Business logic methods
    public void updateProperties(double minTemperature, double maxTemperature, double currentTemperature) {
        this.minTemperature = Validator.positive(minTemperature, "Minimum temperature");
        this.maxTemperature = Validator.positive(maxTemperature, "Maximum temperature");
        this.currentTemperature = currentTemperature;
    }

    public void adjustMinTemperature(double minTemperature) {
        this.minTemperature = Validator.positive(minTemperature, "Minimum temperature");
    }

    public void adjustMaxTemperature(double maxTemperature) {
        this.maxTemperature = Validator.positive(maxTemperature, "Maximum temperature");
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
}
