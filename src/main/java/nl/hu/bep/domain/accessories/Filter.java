package nl.hu.bep.domain.accessories;

import nl.hu.bep.domain.Accessory;
import lombok.*;

/**
 * Represents a water filter accessory for aquariums.

 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Filter extends Accessory {
    private boolean isExternal;
    private int capacityLiters;

    public Filter(String model, String serialNumber, boolean isExternal, int capacityLiters, Long ownerId) {
        super(model, serialNumber, ownerId);
        this.isExternal = isExternal;
        this.capacityLiters = capacityLiters; // Remove duplicate validation - factory already validates
    }

    @Override
    public String getAccessoryType() {
        return "Filter";
    }

    public void updateProperties(boolean isExternal, int capacityLiters) {
        if (capacityLiters <= 0) {
            throw new IllegalArgumentException("Filter capacity must be positive");
        }
        this.isExternal = isExternal;
        this.capacityLiters = capacityLiters;
    }

    public boolean isSuitableForAquarium(double aquariumVolumeLiters) {
        // we're going to assume the filter is OK if it's capable of handling 2x the aquarium volume
        return capacityLiters >= (aquariumVolumeLiters * 2);
    }

    public void updateCapacity(int capacityLiters) {
        if (capacityLiters <= 0) {
            throw new IllegalArgumentException("Filter capacity must be positive");
        }
        this.capacityLiters = capacityLiters;
    }

    public void updateExternal(boolean isExternal) {
        this.isExternal = isExternal;
    }

    // Repository access methods
    @Override
    public boolean isExternal() { return isExternal; }
    
    @Override
    public int getCapacityLiters() { return capacityLiters; }
    
    @Override
    public boolean isLed() { return false; } // Filters don't have LED
    
    @Override
    public java.time.LocalTime getTurnOnTime() { return null; } // Filters don't have time settings
    
    @Override
    public java.time.LocalTime getTurnOffTime() { return null; } // Filters don't have time settings
    
    @Override
    public double getMinTemperature() { return 0.0; } // Filters don't have temperature
    
    @Override
    public double getMaxTemperature() { return 0.0; } // Filters don't have temperature
    
    @Override
    public double getCurrentTemperature() { return 0.0; } // Filters don't have temperature
}
