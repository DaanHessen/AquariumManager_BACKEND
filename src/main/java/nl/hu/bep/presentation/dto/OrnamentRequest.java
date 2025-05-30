package nl.hu.bep.presentation.dto;

public record OrnamentRequest(
    String name,
    String color,
    String material,
    String description,
    Boolean isAirPumpCompatible,
    Long aquariumId) {
  public boolean getIsAirPumpCompatibleValue() {
    return isAirPumpCompatible != null ? isAirPumpCompatible : false;
  }
}