package nl.hu.bep.domain;

import nl.hu.bep.domain.accessories.Filter;
import nl.hu.bep.domain.accessories.Lighting;
import nl.hu.bep.domain.accessories.Thermostat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Accessory Domain Tests")
class AccessoryTest {

    private static final Long OWNER_ID = 1L;
    private static final Long DIFFERENT_OWNER_ID = 2L;
    private static final Long AQUARIUM_ID = 10L;

    @Nested
    @DisplayName("Filter Tests")
    class FilterTests {

        @Test
        @DisplayName("Should create filter with valid parameters")
        void shouldCreateFilterWithValidParameters() {
            // Act
            Accessory filter = Accessory.createFromType("filter", "Eheim Classic", "12345", 
                    true, 250, false, null, null, 0, 0, 0, OWNER_ID, "grey", "A classic filter.");

            // Assert
            assertNotNull(filter);
            assertTrue(filter instanceof Filter);
            assertEquals("Eheim Classic", filter.getModel());
            assertEquals(OWNER_ID, filter.getOwnerId());
            assertEquals(250, filter.getCapacityLiters());
            assertEquals("grey", filter.getColor());
            assertEquals("A classic filter.", filter.getDescription());
        }

        @Test
        @DisplayName("Should assign filter to aquarium")
        void shouldAssignFilterToAquarium() {
            // Arrange
            Accessory filter = Accessory.createFromType("filter", "Eheim Classic", "12345", 
                    true, 250, false, null, null, 0, 0, 0, OWNER_ID, "grey", "A classic filter.");

            // Act
            filter.assignToAquarium(AQUARIUM_ID, OWNER_ID);

            // Assert
            assertEquals(AQUARIUM_ID, filter.getAquariumId());
        }

        @Test
        @DisplayName("Should throw exception when assigning filter with wrong owner")
        void shouldThrowExceptionWhenAssigningFilterWithWrongOwner() {
            // Arrange
            Accessory filter = Accessory.createFromType("filter", "Eheim Classic", "12345", 
                    true, 250, false, null, null, 0, 0, 0, OWNER_ID, "grey", "A classic filter.");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> 
                    filter.assignToAquarium(AQUARIUM_ID, DIFFERENT_OWNER_ID));
        }
    }

    @Nested
    @DisplayName("Lighting Tests")
    class LightingTests {

        @Test
        @DisplayName("Should create lighting with valid parameters")
        void shouldCreateLightingWithValidParameters() {
            // Act
            Accessory lighting = Accessory.createFromType("lighting", "LEDdy Slim", "67890", 
                    false, 0, true, LocalTime.of(8, 0), LocalTime.of(22, 0), 0, 0, 0, 
                    OWNER_ID, "black", "A slim LED light.");

            // Assert
            assertNotNull(lighting);
            assertTrue(lighting instanceof Lighting);
            assertEquals("LEDdy Slim", lighting.getModel());
            assertEquals(LocalTime.of(8, 0), lighting.getTurnOnTime());
            assertEquals(LocalTime.of(22, 0), lighting.getTurnOffTime());
            assertEquals("black", lighting.getColor());
            assertEquals("A slim LED light.", lighting.getDescription());
        }

        @Test
        @DisplayName("Should validate lighting schedule")
        void shouldValidateLightingSchedule() {
            // Arrange
            Accessory lighting = Accessory.createFromType("lighting", "LEDdy Slim", "67890", 
                    false, 0, true, LocalTime.of(8, 0), LocalTime.of(22, 0), 0, 0, 0, 
                    OWNER_ID, "black", "A slim LED light.");

            // Act & Assert
            assertTrue(lighting.getTurnOnTime().isBefore(lighting.getTurnOffTime()));
        }
    }

    @Nested
    @DisplayName("Thermostat Tests")
    class ThermostatTests {

        @Test
        @DisplayName("Should create thermostat with valid parameters")
        void shouldCreateThermostatWithValidParameters() {
            // Act
            Accessory thermostat = Accessory.createFromType("thermostat", "Jager", "54321", 
                    false, 0, false, null, null, 22, 28, 25, OWNER_ID, "blue", "A reliable heater.");

            // Assert
            assertNotNull(thermostat);
            assertTrue(thermostat instanceof Thermostat);
            assertEquals("Jager", thermostat.getModel());
            assertEquals(22, thermostat.getMinTemperature());
            assertEquals(28, thermostat.getMaxTemperature());
            assertEquals(25, thermostat.getCurrentTemperature());
            assertEquals("blue", thermostat.getColor());
            assertEquals("A reliable heater.", thermostat.getDescription());
        }

        @Test
        @DisplayName("Should validate thermostat temperature range")
        void shouldValidateThermostatTemperatureRange() {
            // Arrange
            Accessory thermostat = Accessory.createFromType("thermostat", "Jager", "54321", 
                    false, 0, false, null, null, 22, 28, 25, OWNER_ID, "blue", "A reliable heater.");

            // Act & Assert
            assertTrue(thermostat.getMinTemperature() <= thermostat.getCurrentTemperature());
            assertTrue(thermostat.getCurrentTemperature() <= thermostat.getMaxTemperature());
        }
    }

    @Nested
    @DisplayName("Factory and Validation Tests")
    class FactoryAndValidationTests {

        @Test
        @DisplayName("Should throw exception for unsupported accessory type")
        void shouldThrowExceptionForUnsupportedAccessoryType() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> 
                    Accessory.createFromType("toaster", "Philips", "999", false, 0, false, 
                            null, null, 0, 0, 0, OWNER_ID, "white", "Makes toast."));
        }

        @Test
        @DisplayName("Should validate accessory ownership")
        void shouldValidateAccessoryOwnership() {
            // Arrange
            Accessory accessory = Accessory.createFromType("filter", "Test Filter", "123", 
                    true, 200, false, null, null, 0, 0, 0, OWNER_ID, "black", "Test filter.");

            // Act & Assert
            assertDoesNotThrow(() -> accessory.validateOwnership(OWNER_ID));
            assertThrows(IllegalArgumentException.class, () -> accessory.validateOwnership(DIFFERENT_OWNER_ID));
        }

        @Test
        @DisplayName("Should unassign accessory from aquarium")
        void shouldUnassignAccessoryFromAquarium() {
            // Arrange
            Accessory accessory = Accessory.createFromType("filter", "Test Filter", "123", 
                    true, 200, false, null, null, 0, 0, 0, OWNER_ID, "black", "Test filter.");
            accessory.assignToAquarium(AQUARIUM_ID, OWNER_ID);

            // Act
            accessory.removeFromAquarium(OWNER_ID);

            // Assert
            assertNull(accessory.getAquariumId());
        }

        @Test
        @DisplayName("Should throw exception when unassigning with wrong owner")
        void shouldThrowExceptionWhenUnassigningWithWrongOwner() {
            // Arrange
            Accessory accessory = Accessory.createFromType("filter", "Test Filter", "123", 
                    true, 200, false, null, null, 0, 0, 0, OWNER_ID, "black", "Test filter.");
            accessory.assignToAquarium(AQUARIUM_ID, OWNER_ID);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> 
                    accessory.removeFromAquarium(DIFFERENT_OWNER_ID));
        }
    }

    @Nested
    @DisplayName("Accessory Equality and Hash Code")
    class AccessoryEqualityAndHashCode {

        @Test
        @DisplayName("Should test accessory equality")
        void shouldTestAccessoryEquality() {
            // Arrange
            Accessory accessory1 = Accessory.createFromType("filter", "Filter1", "123", 
                    true, 200, false, null, null, 0, 0, 0, OWNER_ID, "black", "Filter 1.");
            Accessory accessory2 = Accessory.createFromType("filter", "Filter2", "456", 
                    true, 300, false, null, null, 0, 0, 0, OWNER_ID, "blue", "Filter 2.");

            // Act & Assert
            assertNotEquals(accessory1, accessory2); // Different accessories should not be equal
            assertEquals(accessory1, accessory1); // Same instance should be equal
        }

        @Test
        @DisplayName("Should have consistent hash code")
        void shouldHaveConsistentHashCode() {
            // Arrange
            Accessory accessory = Accessory.createFromType("filter", "Test Filter", "123", 
                    true, 200, false, null, null, 0, 0, 0, OWNER_ID, "black", "Test filter.");

            // Act & Assert
            assertEquals(accessory.hashCode(), accessory.hashCode()); // Hash code should be consistent
        }
    }
}
