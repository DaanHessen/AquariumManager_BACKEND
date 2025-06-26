package nl.hu.bep.domain.accessories;

import nl.hu.bep.domain.Accessory;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class AccessorySubclassesTest {

    @Test
    void createFilter_ValidParameters_CreatesFilter() {
        Accessory filter = Accessory.createFromType(
            "filter", "Eheim Classic", "12345", true, 250, 
            false, null, null, 0, 0, 0, 1L, "black", "High-quality filter"
        );
        
        assertTrue(filter instanceof Filter);
        assertEquals("Filter", filter.getAccessoryType());
        assertEquals(250, filter.getCapacityLiters());
        assertTrue(filter.isExternal());
    }

    @Test
    void createLighting_ValidParameters_CreatesLighting() {
        LocalTime timeOn = LocalTime.of(8, 0);
        LocalTime timeOff = LocalTime.of(22, 0);
        
        Accessory lighting = Accessory.createFromType(
            "lighting", "LED Strip", "67890", false, 0,
            true, timeOn, timeOff, 0, 0, 0, 1L, "white", "LED aquarium lighting"
        );
        
        assertTrue(lighting instanceof Lighting);
        assertEquals("Lighting", lighting.getAccessoryType());
        assertTrue(lighting.isLed());
        assertEquals(timeOn, lighting.getTurnOnTime());
        assertEquals(timeOff, lighting.getTurnOffTime());
    }

    @Test
    void createThermostat_ValidParameters_CreatesThermostat() {
        Accessory thermostat = Accessory.createFromType(
            "thermostat", "Aqueon Heater", "54321", false, 0,
            false, null, null, 22.0, 28.0, 25.0, 1L, "silver", "Reliable heater"
        );
        
        assertTrue(thermostat instanceof Thermostat);
        assertEquals("Thermostat", thermostat.getAccessoryType());
        assertEquals(22.0, thermostat.getMinTemperature());
        assertEquals(28.0, thermostat.getMaxTemperature());
        assertEquals(25.0, thermostat.getCurrentTemperature());
    }

    @Test
    void createFilter_InvalidCapacity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Accessory.createFromType(
                "filter", "Bad Filter", "00000", true, 0, // Invalid capacity
                false, null, null, 0, 0, 0, 1L, "red", "Bad filter"
            );
        });
    }

    @Test
    void createThermostat_InvalidTemperatureRange_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Accessory.createFromType(
                "thermostat", "Bad Heater", "00000", false, 0,
                false, null, null, 30.0, 20.0, 25.0, 1L, "red", "Invalid temp range" // min > max
            );
        });
    }
}
