package nl.hu.bep.domain.accessories;

import nl.hu.bep.domain.Accessory;
import nl.hu.bep.domain.utils.Validator;
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
        this.capacityLiters = Validator.positive(capacityLiters, "Filter capacity");
    }

    @Override
    public String getAccessoryType() {
        return "Filter";
    }

    public void updateProperties(boolean isExternal, int capacityLiters) {
        this.isExternal = isExternal;
        this.capacityLiters = Validator.positive(capacityLiters, "Filter capacity");
    }

    public boolean isSuitableForAquarium(double aquariumVolumeLiters) {
        // we're going to assume the filter is OK if it's capable of handling 2x the aquarium volume
        return capacityLiters >= (aquariumVolumeLiters * 2);
    }

    public void updateCapacity(int capacityLiters) {
        this.capacityLiters = Validator.positive(capacityLiters, "Filter capacity");
    }

    public void updateExternal(boolean isExternal) {
        this.isExternal = isExternal;
    }
}
