package nl.hu.bep.presentation.dto;

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
        return age != null ? age : 0;
    }
    
    public String gender() {
        return gender != null ? gender : "Unknown";
    }
    
    public double getPHLevelValue() {
        return phLevel != null ? phLevel : 7.0;
    }
    
    public double getTemperatureValue() {
        return temperature != null ? temperature : 25.0;
    }
    
    public double getTankSizeValue() {
        return tankSize != null ? tankSize : 100.0;
    }
    
    public int getAggressionLevelValue() {
        return aggressionLevel != null ? aggressionLevel : 1;
    }
    
    public double getSaltToleranceValue() {
        return saltTolerance != null ? saltTolerance : 0.0;
    }
    
    public String getColorValue() {
        return color != null ? color : "Unknown";
    }
    
    public String getDescriptionValue() {
        return description != null ? description : "";
    }
}