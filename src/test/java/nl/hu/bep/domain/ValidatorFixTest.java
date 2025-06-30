package nl.hu.bep.domain;

import nl.hu.bep.exception.ApplicationException.BusinessRuleException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nl.hu.bep.domain.accessories.Filter;
import nl.hu.bep.domain.accessories.Thermostat;
import nl.hu.bep.presentation.dto.request.AccessoryRequest;

class ValidatorFixTest {

    @Test
    @DisplayName("AccessoryRequest should provide default capacity when null")
    void testAccessoryRequestDefaultCapacity() {
        AccessoryRequest request = new AccessoryRequest(
            "SuperFilter 3000",
            "SF3000-12345", 
            "filter",
            null,
            true, null, // null capacityLiters should default to 100
            false, "blue", "High-quality filter", 
            null, null, 20.0, 30.0, 25.0
        );
        
        assertEquals(100, request.getCapacityLitersValue(), "Should default to 100L when capacityLiters is null");
    }

    @Test
    @DisplayName("Filter creation should work with valid capacity")
    void testFilterCreationWithValidCapacity() {
        assertDoesNotThrow(() -> {
            Accessory accessory = Accessory.createFromType(
                "filter", "Model", "Serial", 
                true, 100, // Valid capacity
                false, null, null,
                0.0, 0.0, 0.0, 1L, "blue", "description"
            );
            assertTrue(accessory instanceof Filter);
            assertEquals(100, ((Filter) accessory).getCapacityLiters());
        });
    }

    @Test
    @DisplayName("Filter creation should fail with zero capacity")
    void testFilterCreationWithZeroCapacity() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            Accessory.createFromType(
                "filter", "Model", "Serial", 
                true, 0, // Invalid capacity
                false, null, null,
                0.0, 0.0, 0.0, 1L, "blue", "description"
            );
        });
        assertTrue(exception.getMessage().contains("Filter capacity must be provided and positive"));
    }

    @Test
    @DisplayName("Thermostat creation should work with valid temperatures")
    void testThermostatCreationWithValidTemperatures() {
        assertDoesNotThrow(() -> {
            Accessory accessory = Accessory.createFromType(
                "thermostat", "TempControl", "TC123", 
                false, 0,
                false, null, null,
                20.0, 30.0, 25.0, // Valid temperatures
                1L, "white", "description"
            );
            assertTrue(accessory instanceof Thermostat);
            assertEquals(20.0, ((Thermostat) accessory).getMinTemperature());
            assertEquals(30.0, ((Thermostat) accessory).getMaxTemperature());
        });
    }

    @Test
    @DisplayName("Thermostat creation should fail with invalid temperatures")
    void testThermostatCreationWithInvalidTemperatures() {
        // Test zero minimum temperature
        BusinessRuleException exception1 = assertThrows(BusinessRuleException.class, () -> {
            Accessory.createFromType(
                "thermostat", "TempControl", "TC123", 
                false, 0,
                false, null, null,
                0.0, 30.0, 25.0, // Invalid min temp
                1L, "white", "description"
            );
        });
        assertTrue(exception1.getMessage().contains("Minimum temperature must be positive"));

        // Test min >= max temperature
        BusinessRuleException exception2 = assertThrows(BusinessRuleException.class, () -> {
            Accessory.createFromType(
                "thermostat", "TempControl", "TC123", 
                false, 0,
                false, null, null,
                30.0, 20.0, 25.0, // min >= max
                1L, "white", "description"
            );
        });
        assertTrue(exception2.getMessage().contains("Minimum temperature must be less than maximum temperature"));
    }

    @Test
    @DisplayName("AccessoryRequest defaults should work for realistic filter scenario")
    void testRealisticFilterScenario() {
        // Simulate a filter request where capacity is not provided (null)
        AccessoryRequest request = new AccessoryRequest(
            "Fluval C4 Power Filter",
            "FLV-C4-2024",
            "filter",
            1L, // aquarium ID
            true, null, // External filter, no capacity specified
            false, "black", "High performance canister filter",
            null, null, null, null, null
        );
        
        // This should now work without throwing an exception
        assertDoesNotThrow(() -> {
            Accessory accessory = Accessory.createFromType(
                request.type(),
                request.model(),
                request.serialNumber(),
                request.getIsExternalValue(),
                request.getCapacityLitersValue(), // Should be 100
                request.getIsLEDValue(),
                request.getTimeOnValue(),
                request.getTimeOffValue(),
                request.getMinTemperatureValue(),
                request.getMaxTemperatureValue(),
                request.getCurrentTemperatureValue(),
                1L,
                request.getColorValue(),
                request.getDescriptionValue()
            );
            
            assertTrue(accessory instanceof Filter);
            Filter filter = (Filter) accessory;
            assertEquals(100, filter.getCapacityLiters(), "Should use default capacity of 100L");
            assertTrue(filter.isExternal(), "Should be external filter");
        });
    }
}
