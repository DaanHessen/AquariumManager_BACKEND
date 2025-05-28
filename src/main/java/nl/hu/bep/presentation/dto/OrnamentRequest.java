package nl.hu.bep.presentation.dto;

public record OrnamentRequest(
    String name,
    String color,
    String material,
    String description,
    Boolean supportsAirPump,
    Long aquariumId) {
  public boolean getSupportsAirPumpValue() {
    return supportsAirPump != null ? supportsAirPump : false;
  }
}