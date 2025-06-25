package nl.hu.bep.domain;

import nl.hu.bep.domain.accessories.Filter;
import nl.hu.bep.domain.accessories.Lighting;
import nl.hu.bep.domain.accessories.Thermostat;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class AccessoryTest {

    @Test
    void createFilter() {
        Accessory filter = Accessory.createFromType("filter", "Eheim Classic", "12345", true, 250, false, null, null, 0, 0, 0, 1L, "grey", "A classic filter.");
        assertTrue(filter instanceof Filter);
        assertEquals("Eheim Classic", filter.getModel());
        assertEquals(1L, filter.getOwnerId());
        assertEquals(250, filter.getCapacityLiters());
    }

    @Test
    void createLighting() {
        Accessory lighting = Accessory.createFromType("lighting", "LEDdy Slim", "67890", false, 0, true, LocalTime.of(8, 0), LocalTime.of(22, 0), 0, 0, 0, 1L, "black", "A slim LED light.");
        assertTrue(lighting instanceof Lighting);
        assertEquals("LEDdy Slim", lighting.getModel());
        assertEquals(LocalTime.of(8, 0), lighting.getTurnOnTime());
        assertEquals(LocalTime.of(22, 0), lighting.getTurnOffTime());
    }

    @Test
    void createThermostat() {
        Accessory thermostat = Accessory.createFromType("thermostat", "Jager", "54321", false, 0, false, null, null, 22, 28, 25, 1L, "blue", "A reliable heater.");
        assertTrue(thermostat instanceof Thermostat);
        assertEquals("Jager", thermostat.getModel());
        assertEquals(22, thermostat.getMinTemperature());
        assertEquals(28, thermostat.getMaxTemperature());
        assertEquals(25, thermostat.getCurrentTemperature());
    }

    @Test
    void createUnsupportedAccessory() {
        assertThrows(IllegalArgumentException.class, () -> {
            Accessory.createFromType("toaster", "Philips", "999", false, 0, false, null, null, 0, 0, 0, 1L, "white", "Makes toast.");
        });
    }

    @Test
    void assignToAquarium() {
        Accessory accessory = Accessory.createFromType("filter", "Eheim Classic", "12345", true, 250, false, null, null, 0, 0, 0, 1L, "grey", "A classic filter.");
        accessory.assignToAquarium(10L, 1L);
        assertEquals(10L, accessory.getAquariumId());
    }

    @Test
    void assignToAquariumNotOwner() {
        Accessory accessory = Accessory.createFromType("filter", "Eheim Classic", "12345", true, 250, false, null, null, 0, 0, 0, 1L, "grey", "A classic filter.");
        assertThrows(IllegalArgumentException.class, () -> {
            accessory.assignToAquarium(10L, 2L);
        });
    }
}
