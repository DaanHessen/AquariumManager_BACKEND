package nl.hu.bep.presentation.dto.request;

import nl.hu.bep.config.AquariumConstants;

import nl.hu.bep.domain.enums.WaterType;

public record InhabitantRequest(
        String species,
        String color,
        String description,
        Integer count,
        Boolean isSchooling,
        WaterType waterType,
        String type,
        Long aquariumId,
        Boolean isAggressiveEater,
        Boolean requiresSpecialFood,
        Boolean isSnailEater,
        String name,
        Integer age,
        String gender,
        Double phLevel,
        Double temperature,
        Double tankSize,
        Integer aggressionLevel,
        Double saltTolerance) {

    public boolean getSchoolingValue() {
        return isSchooling != null && isSchooling;
    }

    public boolean getAggressiveEaterValue() {
        return isAggressiveEater != null && isAggressiveEater;
    }

    public boolean getRequiresSpecialFoodValue() {
        return requiresSpecialFood != null && requiresSpecialFood;
    }

    public boolean getSnailEaterValue() {
        return isSnailEater != null && isSnailEater;
    }
    
    // Additional getter methods for service compatibility
    public Integer age() {
        return age != null ? age : AquariumConstants.DEFAULT_AGE;
    }
    
    public String gender() {
        return gender != null ? gender : AquariumConstants.DEFAULT_GENDER;
    }
    
    public double getPHLevelValue() {
        return phLevel != null ? phLevel : AquariumConstants.DEFAULT_PH_LEVEL;
    }
    
    public double getTemperatureValue() {
        return temperature != null ? temperature : AquariumConstants.DEFAULT_WATER_TEMPERATURE;
    }
    
    public double getTankSizeValue() {
        return tankSize != null ? tankSize : AquariumConstants.DEFAULT_TANK_SIZE;
    }
    
    public int getAggressionLevelValue() {
        return aggressionLevel != null ? aggressionLevel : AquariumConstants.DEFAULT_AGGRESSION_LEVEL;
    }
    
    public double getSaltToleranceValue() {
        return saltTolerance != null ? saltTolerance : AquariumConstants.DEFAULT_SALT_TOLERANCE;
    }
    
    public String getColorValue() {
        return color != null ? color : AquariumConstants.DEFAULT_COLOR;
    }
    
    public String getDescriptionValue() {
        return description != null ? description : "";
    }
}