package nl.hu.bep.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nl.hu.bep.domain.accessories.Filter;
import nl.hu.bep.domain.accessories.Lighting;
import nl.hu.bep.domain.accessories.Thermostat;
import nl.hu.bep.domain.enums.SubstrateType;

class AccessoryTest {

  private Accessory accessory;

  @Test
  @DisplayName("createFromType should create correct Filter instance")
  void testCreateFilter() {
    String type = "filter";
    String model = "SuperFilter 3000";
    String serialNumber = "SF3000-12345";
    boolean isExternal = true;
    int capacityLiters = 200;

    Accessory accessory = Accessory.createFromType(
        type, model, serialNumber,
        isExternal, capacityLiters,
        false, null, null,
        0.0, 0.0, 0.0,
        1L);

    assertNotNull(accessory);
    assertTrue(accessory instanceof Filter);
    assertEquals(model, accessory.getModel());
    assertEquals(serialNumber, accessory.getSerialNumber());
    assertEquals(1L, accessory.getOwnerId());

    Filter filter = (Filter) accessory;
    assertEquals(isExternal, filter.isExternal());
    assertEquals(capacityLiters, filter.getCapacityLiters());
  }

  @Test
  @DisplayName("createFromType should create correct Lighting instance")
  void testCreateLighting() {
    String type = "lighting";
    String model = "LuxLight 500";
    String serialNumber = "LL500-67890";
    boolean isLED = true;
    LocalTime timeOn = LocalTime.of(8, 0);
    LocalTime timeOff = LocalTime.of(20, 0);

    Accessory accessory = Accessory.createFromType(
        type, model, serialNumber,
        false, 0,
        isLED, timeOn, timeOff,
        0.0, 0.0, 0.0,
        1L);

    assertNotNull(accessory);
    assertTrue(accessory instanceof Lighting);
    assertEquals(model, accessory.getModel());
    assertEquals(serialNumber, accessory.getSerialNumber());
    assertEquals(1L, accessory.getOwnerId());

    Lighting lighting = (Lighting) accessory;
    assertEquals(isLED, lighting.isLed());
    assertEquals(timeOn, lighting.getTurnOnTime());
    assertEquals(timeOff, lighting.getTurnOffTime());
  }

  @Test
  @DisplayName("createFromType should create correct Thermostat instance")
  void testCreateThermostat() {
    String type = "thermostat";
    String model = "TempControl Pro";
    String serialNumber = "TCP-24680";
    double minTemperature = 22.5;
    double maxTemperature = 28.0;
    double currentTemperature = 25.0;

    Accessory accessory = Accessory.createFromType(
        type, model, serialNumber,
        false, 0,
        false, null, null,
        minTemperature, maxTemperature, currentTemperature,
        1L);

    assertNotNull(accessory);
    assertTrue(accessory instanceof Thermostat);
    assertEquals(model, accessory.getModel());
    assertEquals(serialNumber, accessory.getSerialNumber());
    assertEquals(1L, accessory.getOwnerId());

    Thermostat thermostat = (Thermostat) accessory;
    assertEquals(minTemperature, thermostat.getMinTemperature());
    assertEquals(maxTemperature, thermostat.getMaxTemperature());
    assertEquals(currentTemperature, thermostat.getCurrentTemperature());
  }

  @Test
  @DisplayName("Accessory update method should update common properties")
  void testAccessoryUpdate() {
    String initialModel = "InitialModel";
    String initialSerialNumber = "Initial-123";
    String updatedModel = "UpdatedModel";
    String updatedSerialNumber = "Updated-456";

    Accessory accessory = Accessory.createFromType(
        "filter", initialModel, initialSerialNumber,
        false, 100,
        false, null, null,
        0.0, 0.0, 0.0,
        1L);

    accessory.update(updatedModel, updatedSerialNumber);

    assertEquals(updatedModel, accessory.getModel());
    assertEquals(updatedSerialNumber, accessory.getSerialNumber());
    assertEquals(1L, accessory.getOwnerId());
  }

  @Test
  @DisplayName("Filter updateProperties should update specific properties")
  void testFilterUpdateProperties() {
    Filter filter = (Filter) Accessory.createFromType(
        "filter", "model", "serial",
        false, 100,
        false, null, null,
        0.0, 0.0, 0.0,
        1L);

    boolean newIsExternal = true;
    int newCapacity = 200;

    filter.updateProperties(newIsExternal, newCapacity);

    assertEquals(newIsExternal, filter.isExternal());
    assertEquals(newCapacity, filter.getCapacityLiters());
    assertEquals(1L, filter.getOwnerId());
  }

  @Test
  @DisplayName("Aquarium assignment should work correctly")
  void testAquariumAssignment() {
    Accessory accessory = Accessory.createFromType(
        "filter", "FilterModel", "F-12345",
        true, 150,
        false, null, null,
        0.0, 0.0, 0.0,
        1L);

    Aquarium aquarium = Aquarium.create(
        "Test Aquarium", 100.0, 40.0, 50.0,
        nl.hu.bep.domain.enums.SubstrateType.GRAVEL,
        nl.hu.bep.domain.enums.WaterType.FRESH);

    aquarium.addToAccessories(accessory);

    assertEquals(aquarium, accessory.getAquarium());
    assertTrue(aquarium.getAccessories().contains(accessory));

    aquarium.removeFromAccessories(accessory);

    assertNull(accessory.getAquarium());
    assertFalse(aquarium.getAccessories().contains(accessory));
  }
}