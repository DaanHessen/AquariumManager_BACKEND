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
public class Filter extends Accessory {
    private boolean isExternal;
    private int capacityLiters;

    public Filter(String model, String serialNumber, boolean isExternal, int capacityLiters, Long ownerId) {
        super(model, serialNumber, ownerId);
        this.isExternal = isExternal;
        this.capacityLiters = capacityLiters;
    }

    @Override
    public String getAccessoryType() {
        return "Filter";
    }

    public void updateProperties(boolean isExternal, int capacityLiters) {
        if (capacityLiters <= 0) {
            throw new BusinessRuleException("Filter capacity must be positive");
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
            throw new BusinessRuleException("Filter capacity must be positive");
        }
        this.capacityLiters = capacityLiters;
    }

    public void updateExternal(boolean isExternal) {
        this.isExternal = isExternal;
    }

    @Override
    public boolean isExternal() { return isExternal; }
    
    @Override
    public int getCapacityLiters() { return capacityLiters; }
    
    @Override
    public boolean isLed() { return false; }
    
    @Override
    public LocalTime getTurnOnTime() { return null; }
    
    @Override
    public LocalTime getTurnOffTime() { return null; }
    
    @Override
    public double getMinTemperature() { return 0.0; }
    
    @Override
    public double getMaxTemperature() { return 0.0; }
    
    @Override
    public double getCurrentTemperature() { return 0.0; }
}
