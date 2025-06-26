package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.species.Fish;
import nl.hu.bep.domain.species.Plant;
import nl.hu.bep.domain.species.Snail;
import nl.hu.bep.domain.species.Shrimp;
import nl.hu.bep.application.factory.InhabitantFactory;
import nl.hu.bep.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inhabitant Domain Tests")
class InhabitantTest {

    private static final Long OWNER_ID = 1L;
    private static final String SPECIES = "Goldfish";
    private static final String NAME = "Nemo";
    private static final String COLOR = "Orange";
    private static final String DESCRIPTION = "A friendly fish";

    @Nested
    @DisplayName("Fish Tests")
    class FishTests {

        @Test
        @DisplayName("Should create fish with valid parameters")
        void shouldCreateFishWithValidParameters() {
            // Act
            Fish fish = Fish.builder()
                    .name(NAME)
                    .species(SPECIES)
                    .ownerId(OWNER_ID)
                    .color(COLOR)
                    .count(1)
                    .isSchooling(false)
                    .waterType(WaterType.FRESHWATER)
                    .description(DESCRIPTION)
                    .isAggressiveEater(false)
                    .requiresSpecialFood(false)
                    .isSnailEater(false)
                    .build();

            // Assert
            assertNotNull(fish);
            assertEquals(NAME, fish.getName());
            assertEquals(SPECIES, fish.getSpecies());
            assertEquals(OWNER_ID, fish.getOwnerId());
            assertEquals(COLOR, fish.getColor());
            assertEquals(1, fish.getCount());
            assertFalse(fish.isSchooling());
            assertEquals(WaterType.FRESHWATER, fish.getWaterType());
            assertEquals(DESCRIPTION, fish.getDescription());
            assertEquals("Fish", fish.getType());
            assertEquals("Fish", fish.getInhabitantType());
            assertFalse(fish.getAggressiveEater());
            assertFalse(fish.getRequiresSpecialFood());
            assertFalse(fish.getSnailEater());
        }

        @Test
        @DisplayName("Should create aggressive fish")
        void shouldCreateAggressiveFish() {
            // Act
            Fish fish = Fish.builder()
                    .name("Shark")
                    .species("Great White")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.SALTWATER)
                    .isAggressiveEater(true)
                    .isSnailEater(true)
                    .build();

            // Assert
            assertTrue(fish.getAggressiveEater());
            assertTrue(fish.getSnailEater());
            assertEquals("Fish", fish.getType());
        }

        @Test
        @DisplayName("Should test fish compatibility")
        void shouldTestFishCompatibility() {
            // Arrange
            Fish peacefulFish = Fish.builder()
                    .name("Peaceful")
                    .species("Guppy")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .isAggressiveEater(false)
                    .isSnailEater(false)
                    .build();

            Fish aggressiveFish = Fish.builder()
                    .name("Aggressive")
                    .species("Puffer")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .isAggressiveEater(true)
                    .isSnailEater(true)
                    .build();

            Snail snail = Snail.builder()
                    .name("Gary")
                    .species("Garden Snail")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act & Assert
            assertTrue(peacefulFish.isCompatibleWith(peacefulFish));
            assertFalse(aggressiveFish.isCompatibleWith(snail)); // Snail eater should not be compatible with snails
            assertTrue(peacefulFish.isCompatibleWith(snail)); // Peaceful fish should be compatible with snails
        }
    }

    @Nested
    @DisplayName("Plant Tests") 
    class PlantTests {

        @Test
        @DisplayName("Should create plant with valid parameters")
        void shouldCreatePlantWithValidParameters() {
            // Act
            Plant plant = Plant.builder()
                    .name("Java Fern")
                    .species("Microsorum pteropus")
                    .ownerId(OWNER_ID)
                    .color("Green")
                    .count(3)
                    .waterType(WaterType.FRESHWATER)
                    .description("Beautiful aquatic plant")
                    .build();

            // Assert
            assertNotNull(plant);
            assertEquals("Java Fern", plant.getName());
            assertEquals("Microsorum pteropus", plant.getSpecies());
            assertEquals(OWNER_ID, plant.getOwnerId());
            assertEquals("Green", plant.getColor());
            assertEquals(3, plant.getCount());
            assertEquals(WaterType.FRESHWATER, plant.getWaterType());
            assertEquals("Plant", plant.getType());
            assertEquals("Plant", plant.getInhabitantType());
        }
    }

    @Nested
    @DisplayName("Snail Tests")
    class SnailTests {

        @Test
        @DisplayName("Should create snail with valid parameters")
        void shouldCreateSnailWithValidParameters() {
            // Act
            Snail snail = Snail.builder()
                    .name("Gary")
                    .species("Nerite Snail")
                    .ownerId(OWNER_ID)
                    .color("Brown")
                    .count(2)
                    .waterType(WaterType.FRESHWATER)
                    .description("Algae eating snail")
                    .build();

            // Assert
            assertNotNull(snail);
            assertEquals("Gary", snail.getName());
            assertEquals("Nerite Snail", snail.getSpecies());
            assertEquals(OWNER_ID, snail.getOwnerId());
            assertEquals("Brown", snail.getColor());
            assertEquals(2, snail.getCount());
            assertEquals(WaterType.FRESHWATER, snail.getWaterType());
            assertEquals("Snail", snail.getType());
            assertEquals("Snail", snail.getInhabitantType());
        }
    }

    @Nested
    @DisplayName("Inhabitant Factory Tests")
    class InhabitantFactoryTests {

        private InhabitantFactory inhabitantFactory;

        @BeforeEach
        void setUp() {
            inhabitantFactory = new InhabitantFactory();
        }

        @Test
        @DisplayName("Should create fish through factory")
        void shouldCreateFishThroughFactory() {
            // Arrange
            Inhabitant.InhabitantProperties fishProperties = new Inhabitant.InhabitantProperties(true, true, false);

            // Act
            Inhabitant fish = inhabitantFactory.createInhabitant("fish", "Clownfish", "Nemo", 
                    OWNER_ID, Optional.of("Orange"), Optional.of(1), Optional.of(false), 
                    Optional.of(WaterType.SALTWATER), Optional.of("A friendly fish"), fishProperties);

            // Assert
            assertTrue(fish instanceof Fish);
            assertEquals("Nemo", fish.getName());
            assertEquals(fishProperties, fish.getTypeSpecificProperties());
        }

        @Test
        @DisplayName("Should create snail through factory")
        void shouldCreateSnailThroughFactory() {
            // Arrange
            Inhabitant.InhabitantProperties snailProperties = new Inhabitant.InhabitantProperties(false, false, true);

            // Act
            Inhabitant snail = inhabitantFactory.createInhabitant("snail", "Garden Snail", "Gary", 
                    OWNER_ID, Optional.of("Brown"), Optional.of(1), Optional.of(false), 
                    Optional.of(WaterType.FRESHWATER), Optional.of("A slow snail"), snailProperties);

            // Assert
            assertTrue(snail instanceof Snail);
            assertEquals("Gary", snail.getName());
            assertEquals(snailProperties, snail.getTypeSpecificProperties());
        }

        @Test
        @DisplayName("Should create shrimp through factory")
        void shouldCreateShrimpThroughFactory() {
            // Act
            Inhabitant shrimp = inhabitantFactory.createInhabitant("shrimp", "Cherry Shrimp", "Shrimpy", 
                    OWNER_ID, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 
                    Optional.empty(), Inhabitant.InhabitantProperties.defaults());

            // Assert
            assertTrue(shrimp instanceof Shrimp);
        }

        @Test
        @DisplayName("Should throw exception for unknown inhabitant type")
        void shouldThrowExceptionForUnknownInhabitantType() {
            // Act & Assert
            assertThrows(ApplicationException.ValidationException.class, () -> 
                    inhabitantFactory.createInhabitant("unknown", "Unknown", "Unknown", OWNER_ID, 
                            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 
                            Optional.empty(), Inhabitant.InhabitantProperties.defaults()));
        }
    }

    @Nested
    @DisplayName("Inhabitant Properties Tests")
    class InhabitantPropertiesTests {

        @Test
        @DisplayName("Should create default inhabitant properties")
        void shouldCreateDefaultInhabitantProperties() {
            // Act
            Inhabitant.InhabitantProperties properties = Inhabitant.InhabitantProperties.defaults();

            // Assert
            assertNotNull(properties);
            assertFalse(properties.isAggressiveEater);
            assertFalse(properties.requiresSpecialFood);
            assertFalse(properties.isSnailEater);
        }

        @Test
        @DisplayName("Should create custom inhabitant properties")
        void shouldCreateCustomInhabitantProperties() {
            // Act
            Inhabitant.InhabitantProperties properties = 
                new Inhabitant.InhabitantProperties(true, true, true);

            // Assert
            assertNotNull(properties);
            assertTrue(properties.isAggressiveEater);
            assertTrue(properties.requiresSpecialFood);
            assertTrue(properties.isSnailEater);
        }

        @Test
        @DisplayName("Should test inhabitant properties equality")
        void shouldTestInhabitantPropertiesEquality() {
            // Arrange
            Inhabitant.InhabitantProperties properties1 = 
                new Inhabitant.InhabitantProperties(true, false, true);
            Inhabitant.InhabitantProperties properties2 = 
                new Inhabitant.InhabitantProperties(true, false, true);
            Inhabitant.InhabitantProperties properties3 = 
                new Inhabitant.InhabitantProperties(false, false, true);

            // Act & Assert
            assertEquals(properties1, properties2);
            assertNotEquals(properties1, properties3);
            assertEquals(properties1.hashCode(), properties2.hashCode());
        }
    }
}
