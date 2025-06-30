package nl.hu.bep.domain.species;

import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Fish Species Tests")
class FishTest {

    @Nested
    @DisplayName("Fish Creation")
    class FishCreation {

        @Test
        @DisplayName("Should create fish with valid data")
        void shouldCreateFishWithValidData() {
            // When
            Fish fish = Fish.builder()
                    .name("Clownfish")
                    .species("Amphiprioninae")
                    .waterType(WaterType.SALTWATER)
                    .color("Orange")
                    .ownerId(1L)
                    .count(2)
                    .build();

            // Then
            assertEquals("Clownfish", fish.getName());
            assertEquals(WaterType.SALTWATER, fish.getWaterType());
            assertEquals("Orange", fish.getColor());
            assertEquals("Fish", fish.getType());
        }

        @Test
        @DisplayName("Should create fish with freshwater type")
        void shouldCreateFishWithFreshwaterType() {
            // When
            Fish fish = Fish.builder()
                    .name("Goldfish")
                    .species("Goldfish Species")
                    .ownerId(1L)
                    .waterType(WaterType.FRESHWATER)
                    .color("Gold")
                    .build();

            // Then
            assertEquals("Goldfish", fish.getName());
            assertEquals(WaterType.FRESHWATER, fish.getWaterType());
            assertEquals("Gold", fish.getColor());
        }

        @Test
        @DisplayName("Should throw exception when species name is null")
        void shouldThrowExceptionWhenSpeciesNameIsNull() {
            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Fish.builder()
                    .name(null)
                    .species("Test Species")
                    .ownerId(1L)
                    .waterType(WaterType.SALTWATER)
                    .color("Orange")
                    .build()
            );
            
            assertTrue(exception.getMessage().contains("name"));
        }

        @Test
        @DisplayName("Should throw exception when species name is empty")
        void shouldThrowExceptionWhenSpeciesNameIsEmpty() {
            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Fish.builder()
                    .name("")
                    .species("Test Species")
                    .ownerId(1L)
                    .waterType(WaterType.SALTWATER)
                    .color("Orange")
                    .build()
            );
            
            assertTrue(exception.getMessage().contains("name"));
        }

        @Test
        @DisplayName("Should use default water type when null")
        void shouldUseDefaultWaterTypeWhenNull() {
            // When
            Fish fish = Fish.builder()
                    .name("Clownfish")
                    .species("Test Species")
                    .ownerId(1L)
                    .waterType(null)
                    .color("Orange")
                    .build();
            
            // Then
            assertEquals(WaterType.FRESHWATER, fish.getWaterType());
        }

        @Test
        @DisplayName("Should allow null color pattern")
        void shouldAllowNullColorPattern() {
            // When & Then
            assertDoesNotThrow(() -> Fish.builder()
                .name("Clownfish")
                .species("Test Species")
                .ownerId(1L)
                .waterType(WaterType.SALTWATER)
                .color(null)
                .build());
        }
    }

    @Nested
    @DisplayName("Fish Equality")
    class FishEquality {

        @Test
        @DisplayName("Should be equal when all properties match")
        void shouldBeEqualWhenAllPropertiesMatch() {
            // Given
            Fish fish1 = Fish.builder()
                    .name("Clownfish")
                    .species("Test Species")
                    .ownerId(1L)
                    .waterType(WaterType.SALTWATER)
                    .color("Orange")
                    .build();
            Fish fish2 = Fish.builder()
                    .name("Clownfish")
                    .species("Test Species")
                    .ownerId(1L)
                    .waterType(WaterType.SALTWATER)
                    .color("Orange")
                    .build();

            // When & Then
            assertEquals(fish1, fish2);
            assertEquals(fish1.hashCode(), fish2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when species names differ")
        void shouldNotBeEqualWhenSpeciesNamesDiffer() {
            // Given
            Fish fish1 = Fish.builder()
                    .id(1L)
                    .name("Clownfish")
                    .species("Species1")
                    .ownerId(1L)
                    .waterType(WaterType.SALTWATER)
                    .color("Orange")
                    .build();
            Fish fish2 = Fish.builder()
                    .id(2L)
                    .name("Angelfish")
                    .species("Species2")
                    .ownerId(1L)
                    .waterType(WaterType.SALTWATER)
                    .color("Orange")
                    .build();

            // When & Then
            assertNotEquals(fish1, fish2);
        }

        @Test
        @DisplayName("Should not be equal when water types differ")
        void shouldNotBeEqualWhenWaterTypesDiffer() {
            // Given
            Fish fish1 = Fish.builder()
                    .id(1L)
                    .name("Goldfish")
                    .species("Test Species")
                    .ownerId(1L)
                    .waterType(WaterType.FRESHWATER)
                    .color("Gold")
                    .build();
            Fish fish2 = Fish.builder()
                    .id(2L)
                    .name("Goldfish")
                    .species("Test Species")
                    .ownerId(1L)
                    .waterType(WaterType.SALTWATER)
                    .color("Gold")
                    .build();

            // When & Then
            assertNotEquals(fish1, fish2);
        }

        @Test
        @DisplayName("Should not be equal when color patterns differ")
        void shouldNotBeEqualWhenColorPatternsDiffer() {
            // Given
            Fish fish1 = Fish.builder()
                    .id(1L)
                    .name("Clownfish")
                    .species("Test Species")
                    .ownerId(1L)
                    .waterType(WaterType.SALTWATER)
                    .color("Orange")
                    .build();
            Fish fish2 = Fish.builder()
                    .id(2L)
                    .name("Clownfish")
                    .species("Test Species")
                    .ownerId(1L)
                    .waterType(WaterType.SALTWATER)
                    .color("Black")
                    .build();

            // When & Then
            assertNotEquals(fish1, fish2);
        }
    }

    @Nested
    @DisplayName("Fish toString")
    class FishToString {

        @Test
        @DisplayName("Should include relevant information in toString")
        void shouldIncludeRelevantInformationInToString() {
            // Given
            Fish fish = Fish.builder()
                    .name("Clownfish")
                    .species("Test Species")
                    .ownerId(1L)
                    .waterType(WaterType.SALTWATER)
                    .color("Orange")
                    .build();

            // When
            String toString = fish.toString();

            // Then
            assertTrue(toString.contains("Clownfish"));
            assertTrue(toString.contains("SALTWATER"));
            assertTrue(toString.contains("Orange"));
        }
    }
}
