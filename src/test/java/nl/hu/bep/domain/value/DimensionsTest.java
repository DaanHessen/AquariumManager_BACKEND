package nl.hu.bep.domain.value;

import nl.hu.bep.exception.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dimensions Value Object Unit Tests")
class DimensionsTest {

    @Nested
    @DisplayName("Construction and Validation")
    class ConstructionAndValidation {

        @Test
        @DisplayName("Should create dimensions with valid positive values")
        void shouldCreateDimensionsWithValidPositiveValues() {
            // Given
            double length = 100.0;
            double width = 50.0;
            double height = 60.0;

            // When
            Dimensions dimensions = new Dimensions(length, width, height);

            // Then
            assertEquals(length, dimensions.getLength());
            assertEquals(width, dimensions.getWidth());
            assertEquals(height, dimensions.getHeight());
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, -1.0, -100.5})
        @DisplayName("Should throw exception for invalid length values")
        void shouldThrowExceptionForInvalidLengthValues(double invalidLength) {
            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> new Dimensions(invalidLength, 50.0, 60.0)
            );
            
            assertTrue(exception.getMessage().contains("Length"));
            assertTrue(exception.getMessage().contains("must be positive"));
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, -1.0, -50.25})
        @DisplayName("Should throw exception for invalid width values")
        void shouldThrowExceptionForInvalidWidthValues(double invalidWidth) {
            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> new Dimensions(100.0, invalidWidth, 60.0)
            );
            
            assertTrue(exception.getMessage().contains("Width"));
            assertTrue(exception.getMessage().contains("must be positive"));
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, -1.0, -75.8})
        @DisplayName("Should throw exception for invalid height values")
        void shouldThrowExceptionForInvalidHeightValues(double invalidHeight) {
            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> new Dimensions(100.0, 50.0, invalidHeight)
            );
            
            assertTrue(exception.getMessage().contains("Height"));
            assertTrue(exception.getMessage().contains("must be positive"));
        }

        @Test
        @DisplayName("Should handle very small positive values")
        void shouldHandleVerySmallPositiveValues() {
            // Given
            double smallValue = 0.1;

            // When
            Dimensions dimensions = new Dimensions(smallValue, smallValue, smallValue);

            // Then
            assertEquals(smallValue, dimensions.getLength());
            assertEquals(smallValue, dimensions.getWidth());
            assertEquals(smallValue, dimensions.getHeight());
        }

        @Test
        @DisplayName("Should handle very large positive values")
        void shouldHandleVeryLargePositiveValues() {
            // Given
            double largeValue = 10000.0;

            // When
            Dimensions dimensions = new Dimensions(largeValue, largeValue, largeValue);

            // Then
            assertEquals(largeValue, dimensions.getLength());
            assertEquals(largeValue, dimensions.getWidth());
            assertEquals(largeValue, dimensions.getHeight());
        }
    }

    @Nested
    @DisplayName("Volume Calculations")
    class VolumeCalculations {

        @Test
        @DisplayName("Should calculate volume in liters correctly")
        void shouldCalculateVolumeInLitersCorrectly() {
            // Given
            Dimensions dimensions = new Dimensions(100.0, 50.0, 40.0); // 200000 cubic cm

            // When
            double volumeInLiters = dimensions.getVolumeInLiters();

            // Then
            assertEquals(200.0, volumeInLiters, 0.001); // 200000 / 1000 = 200 liters
        }

        @Test
        @DisplayName("Should calculate volume correctly for getVolume method")
        void shouldCalculateVolumeCorrectlyForGetVolumeMethod() {
            // Given
            Dimensions dimensions = new Dimensions(50.0, 30.0, 20.0); // 30000 cubic cm

            // When
            double volume = dimensions.getVolume();

            // Then
            assertEquals(30.0, volume, 0.001); // 30000 / 1000 = 30 liters
        }

        @Test
        @DisplayName("Should return same value for getVolume and getVolumeInLiters")
        void shouldReturnSameValueForGetVolumeAndGetVolumeInLiters() {
            // Given
            Dimensions dimensions = new Dimensions(80.0, 40.0, 50.0);

            // When
            double volumeInLiters = dimensions.getVolumeInLiters();
            double volume = dimensions.getVolume();

            // Then
            assertEquals(volumeInLiters, volume);
        }

        @Test
        @DisplayName("Should handle small dimensions volume calculation")
        void shouldHandleSmallDimensionsVolumeCalculation() {
            // Given
            Dimensions dimensions = new Dimensions(1.0, 1.0, 1.0); // 1 cubic cm

            // When
            double volume = dimensions.getVolumeInLiters();

            // Then
            assertEquals(0.001, volume, 0.0001); // 1 / 1000 = 0.001 liters
        }

        @Test
        @DisplayName("Should handle large dimensions volume calculation")
        void shouldHandleLargeDimensionsVolumeCalculation() {
            // Given
            Dimensions dimensions = new Dimensions(200.0, 100.0, 80.0); // 1,600,000 cubic cm

            // When
            double volume = dimensions.getVolumeInLiters();

            // Then
            assertEquals(1600.0, volume, 0.001); // 1,600,000 / 1000 = 1600 liters
        }

        @Test
        @DisplayName("Should handle decimal dimensions for volume calculation")
        void shouldHandleDecimalDimensionsForVolumeCalculation() {
            // Given
            Dimensions dimensions = new Dimensions(50.5, 30.2, 25.8);

            // When
            double volume = dimensions.getVolumeInLiters();
            double expectedVolume = (50.5 * 30.2 * 25.8) / 1000;

            // Then
            assertEquals(expectedVolume, volume, 0.001);
        }
    }

    @Nested
    @DisplayName("Equality and Hash Code")
    class EqualityAndHashCode {

        @Test
        @DisplayName("Should be equal when all dimensions are the same")
        void shouldBeEqualWhenAllDimensionsAreTheSame() {
            // Given
            Dimensions dimensions1 = new Dimensions(100.0, 50.0, 60.0);
            Dimensions dimensions2 = new Dimensions(100.0, 50.0, 60.0);

            // When & Then
            assertEquals(dimensions1, dimensions2);
            assertEquals(dimensions1.hashCode(), dimensions2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when dimensions differ")
        void shouldNotBeEqualWhenDimensionsDiffer() {
            // Given
            Dimensions dimensions1 = new Dimensions(100.0, 50.0, 60.0);
            Dimensions dimensions2 = new Dimensions(100.0, 50.0, 61.0); // Different height

            // When & Then
            assertNotEquals(dimensions1, dimensions2);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            // Given
            Dimensions dimensions = new Dimensions(100.0, 50.0, 60.0);

            // When & Then
            assertEquals(dimensions, dimensions);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Dimensions dimensions = new Dimensions(100.0, 50.0, 60.0);

            // When & Then
            assertNotEquals(dimensions, null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            Dimensions dimensions = new Dimensions(100.0, 50.0, 60.0);
            String differentType = "Not a Dimensions object";

            // When & Then
            assertNotEquals(dimensions, differentType);
        }

        @Test
        @DisplayName("Should handle small floating point differences correctly")
        void shouldHandleSmallFloatingPointDifferencesCorrectly() {
            // Given
            Dimensions dimensions1 = new Dimensions(100.0, 50.0, 60.0);
            Dimensions dimensions2 = new Dimensions(100.0000001, 50.0, 60.0); // Very small difference

            // When & Then
            assertNotEquals(dimensions1, dimensions2);
        }

        @Test
        @DisplayName("Should have consistent hash codes for equal objects")
        void shouldHaveConsistentHashCodesForEqualObjects() {
            // Given
            Dimensions dimensions1 = new Dimensions(75.5, 45.2, 55.8);
            Dimensions dimensions2 = new Dimensions(75.5, 45.2, 55.8);

            // When & Then
            assertEquals(dimensions1, dimensions2);
            assertEquals(dimensions1.hashCode(), dimensions2.hashCode());
        }
    }
}
