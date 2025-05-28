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
        String name) {

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
}