package nl.hu.bep.domain.species;

import nl.hu.bep.domain.Inhabitant;
import nl.hu.bep.domain.enums.WaterType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Species Domain Tests")
class SpeciesTest {

    private static final Long OWNER_ID = 1L;
    private static final Long AQUARIUM_ID = 10L;
    private static final String TEST_NAME = "Test Species";
    private static final String TEST_SPECIES = "Test Species Name";
    private static final String TEST_COLOR = "Blue";
    private static final String TEST_DESCRIPTION = "Test description";
    private static final LocalDateTime TEST_DATE = LocalDateTime.now();

    @Nested
    @DisplayName("Fish Tests")
    class FishTests {

        @Test
        @DisplayName("Should create fish with default properties")
        void shouldCreateFishWithDefaultProperties() {
            // Act
            Fish fish = Fish.builder()
                    .name(TEST_NAME)
                    .species(TEST_SPECIES)
                    .ownerId(OWNER_ID)
                    .color(TEST_COLOR)
                    .count(1)
                    .isSchooling(false)
                    .waterType(WaterType.FRESHWATER)
                    .description(TEST_DESCRIPTION)
                    .dateCreated(TEST_DATE)
                    .aquariumId(AQUARIUM_ID)
                    .build();

            // Assert
            assertNotNull(fish);
            assertEquals(TEST_NAME, fish.getName());
            assertEquals(TEST_SPECIES, fish.getSpecies());
            assertEquals(OWNER_ID, fish.getOwnerId());
            assertEquals("Fish", fish.getType());
            assertEquals("Fish", fish.getInhabitantType());
            assertFalse(fish.getAggressiveEater());
            assertFalse(fish.getRequiresSpecialFood());
            assertFalse(fish.getSnailEater());
        }

        @Test
        @DisplayName("Should create fish with custom properties")
        void shouldCreateFishWithCustomProperties() {
            // Act
            Fish fish = Fish.builder()
                    .name(TEST_NAME)
                    .species(TEST_SPECIES)
                    .ownerId(OWNER_ID)
                    .color(TEST_COLOR)
                    .count(5)
                    .isSchooling(true)
                    .waterType(WaterType.SALTWATER)
                    .description(TEST_DESCRIPTION)
                    .dateCreated(TEST_DATE)
                    .aquariumId(AQUARIUM_ID)
                    .isAggressiveEater(true)
                    .requiresSpecialFood(true)
                    .isSnailEater(true)
                    .build();

            // Assert
            assertNotNull(fish);
            assertEquals(5, fish.getCount());
            assertTrue(fish.isSchooling());
            assertEquals(WaterType.SALTWATER, fish.getWaterType());
            assertTrue(fish.getAggressiveEater());
            assertTrue(fish.getRequiresSpecialFood());
            assertTrue(fish.getSnailEater());
        }

        @Test
        @DisplayName("Should update fish properties")
        void shouldUpdateFishProperties() {
            // Arrange
            Fish fish = Fish.builder()
                    .name(TEST_NAME)
                    .species(TEST_SPECIES)
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act
            fish.updateProperties(true, true, true);

            // Assert
            assertTrue(fish.getAggressiveEater());
            assertTrue(fish.getRequiresSpecialFood());
            assertTrue(fish.getSnailEater());
        }

        @Test
        @DisplayName("Should be incompatible with snails when fish eats snails")
        void shouldBeIncompatibleWithSnailsWhenFishEatsSnails() {
            // Arrange
            Fish predatorFish = Fish.builder()
                    .name("Predator Fish")
                    .species("Predator")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .isSnailEater(true)
                    .build();

            Snail snail = Snail.builder()
                    .name("Test Snail")
                    .species("Garden Snail")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act & Assert
            assertFalse(predatorFish.isCompatibleWith(snail));
        }

        @Test
        @DisplayName("Should be compatible with snails when fish doesn't eat snails")
        void shouldBeCompatibleWithSnailsWhenFishDoesntEatSnails() {
            // Arrange
            Fish peacefulFish = Fish.builder()
                    .name("Peaceful Fish")
                    .species("Peaceful")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .isSnailEater(false)
                    .build();

            Snail snail = Snail.builder()
                    .name("Test Snail")
                    .species("Garden Snail")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act & Assert
            assertTrue(peacefulFish.isCompatibleWith(snail));
        }

        @Test
        @DisplayName("Should be incompatible when aggressive fish with larger count meets non-aggressive fish")
        void shouldBeIncompatibleWhenAggressiveFishMeetsNonAggressiveFish() {
            // Arrange
            Fish aggressiveFish = Fish.builder()
                    .name("Aggressive Fish")
                    .species("Predator")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .count(5)
                    .isAggressiveEater(true)
                    .build();

            Fish peacefulFish = Fish.builder()
                    .name("Peaceful Fish")
                    .species("Peaceful")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .count(3)
                    .isAggressiveEater(false)
                    .build();

            // Act & Assert
            assertFalse(aggressiveFish.isCompatibleWith(peacefulFish));
        }

        @Test
        @DisplayName("Should be compatible with non-fish inhabitants")
        void shouldBeCompatibleWithNonFishInhabitants() {
            // Arrange
            Fish fish = Fish.builder()
                    .name("Test Fish")
                    .species("Test")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            Plant plant = Plant.builder()
                    .name("Test Plant")
                    .species("Aquatic Plant")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act & Assert
            assertTrue(fish.isCompatibleWith(plant));
        }

        @Test
        @DisplayName("Should return correct type-specific properties")
        void shouldReturnCorrectTypeSpecificProperties() {
            // Arrange
            Fish fish = Fish.builder()
                    .name(TEST_NAME)
                    .species(TEST_SPECIES)
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .isAggressiveEater(true)
                    .requiresSpecialFood(false)
                    .isSnailEater(true)
                    .build();

            // Act
            Inhabitant.InhabitantProperties properties = fish.getTypeSpecificProperties();

            // Assert
            assertNotNull(properties);
            assertTrue(properties.isAggressiveEater);
            assertFalse(properties.requiresSpecialFood);
            assertTrue(properties.isSnailEater);
        }
    }

    @Nested
    @DisplayName("Plant Tests")
    class PlantTests {

        @Test
        @DisplayName("Should create plant with correct properties")
        void shouldCreatePlantWithCorrectProperties() {
            // Act
            Plant plant = Plant.builder()
                    .name(TEST_NAME)
                    .species(TEST_SPECIES)
                    .ownerId(OWNER_ID)
                    .color(TEST_COLOR)
                    .count(1)
                    .isSchooling(false)
                    .waterType(WaterType.FRESHWATER)
                    .description(TEST_DESCRIPTION)
                    .dateCreated(TEST_DATE)
                    .aquariumId(AQUARIUM_ID)
                    .build();

            // Assert
            assertNotNull(plant);
            assertEquals(TEST_NAME, plant.getName());
            assertEquals(TEST_SPECIES, plant.getSpecies());
            assertEquals(OWNER_ID, plant.getOwnerId());
            assertEquals("Plant", plant.getType());
            assertEquals("Plant", plant.getInhabitantType());
            assertFalse(plant.getAggressiveEater());
            assertFalse(plant.getRequiresSpecialFood());
            assertFalse(plant.getSnailEater());
        }

        @Test
        @DisplayName("Should be compatible with all other inhabitants")
        void shouldBeCompatibleWithAllOtherInhabitants() {
            // Arrange
            Plant plant = Plant.builder()
                    .name("Test Plant")
                    .species("Aquatic Plant")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            Fish fish = Fish.builder()
                    .name("Test Fish")
                    .species("Test Fish")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .isAggressiveEater(true)
                    .isSnailEater(true)
                    .build();

            Snail snail = Snail.builder()
                    .name("Test Snail")
                    .species("Garden Snail")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act & Assert
            assertTrue(plant.isCompatibleWith(fish));
            assertTrue(plant.isCompatibleWith(snail));
            assertTrue(plant.isCompatibleWith(plant));
        }

        @Test
        @DisplayName("Should return default type-specific properties")
        void shouldReturnDefaultTypeSpecificProperties() {
            // Arrange
            Plant plant = Plant.builder()
                    .name(TEST_NAME)
                    .species(TEST_SPECIES)
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act
            Inhabitant.InhabitantProperties properties = plant.getTypeSpecificProperties();

            // Assert
            assertNotNull(properties);
            assertFalse(properties.isAggressiveEater);
            assertFalse(properties.requiresSpecialFood);
            assertFalse(properties.isSnailEater);
        }
    }

    @Nested
    @DisplayName("Snail Tests")
    class SnailTests {

        @Test
        @DisplayName("Should create snail with default properties")
        void shouldCreateSnailWithDefaultProperties() {
            // Act
            Snail snail = Snail.builder()
                    .name(TEST_NAME)
                    .species(TEST_SPECIES)
                    .ownerId(OWNER_ID)
                    .color(TEST_COLOR)
                    .count(1)
                    .isSchooling(false)
                    .waterType(WaterType.FRESHWATER)
                    .description(TEST_DESCRIPTION)
                    .dateCreated(TEST_DATE)
                    .aquariumId(AQUARIUM_ID)
                    .build();

            // Assert
            assertNotNull(snail);
            assertEquals(TEST_NAME, snail.getName());
            assertEquals(TEST_SPECIES, snail.getSpecies());
            assertEquals(OWNER_ID, snail.getOwnerId());
            assertEquals("Snail", snail.getType());
            assertEquals("Snail", snail.getInhabitantType());
            assertFalse(snail.getAggressiveEater());
            assertFalse(snail.getRequiresSpecialFood());
            assertFalse(snail.getSnailEater());
        }

        @Test
        @DisplayName("Should create snail with custom properties")
        void shouldCreateSnailWithCustomProperties() {
            // Act
            Snail snail = Snail.builder()
                    .name(TEST_NAME)
                    .species(TEST_SPECIES)
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.SALTWATER)
                    .isSnailEater(true)
                    .build();

            // Assert
            assertNotNull(snail);
            assertEquals(WaterType.SALTWATER, snail.getWaterType());
            assertTrue(snail.getSnailEater());
        }

        @Test
        @DisplayName("Should be incompatible with snail-eating fish")
        void shouldBeIncompatibleWithSnailEatingFish() {
            // Arrange
            Snail snail = Snail.builder()
                    .name("Test Snail")
                    .species("Garden Snail")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            Fish predatorFish = Fish.builder()
                    .name("Predator Fish")
                    .species("Predator")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .isSnailEater(true)
                    .build();

            // Act & Assert
            assertFalse(snail.isCompatibleWith(predatorFish));
        }

        @Test
        @DisplayName("Should be compatible with non-snail-eating fish")
        void shouldBeCompatibleWithNonSnailEatingFish() {
            // Arrange
            Snail snail = Snail.builder()
                    .name("Test Snail")
                    .species("Garden Snail")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            Fish peacefulFish = Fish.builder()
                    .name("Peaceful Fish")
                    .species("Peaceful")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .isSnailEater(false)
                    .build();

            // Act & Assert
            assertTrue(snail.isCompatibleWith(peacefulFish));
        }

        @Test
        @DisplayName("Should be compatible with plants and other snails")
        void shouldBeCompatibleWithPlantsAndOtherSnails() {
            // Arrange
            Snail snail1 = Snail.builder()
                    .name("Test Snail 1")
                    .species("Garden Snail")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            Snail snail2 = Snail.builder()
                    .name("Test Snail 2")
                    .species("Garden Snail")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            Plant plant = Plant.builder()
                    .name("Test Plant")
                    .species("Aquatic Plant")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act & Assert
            assertTrue(snail1.isCompatibleWith(snail2));
            assertTrue(snail1.isCompatibleWith(plant));
        }

        @Test
        @DisplayName("Should return correct type-specific properties")
        void shouldReturnCorrectTypeSpecificProperties() {
            // Arrange
            Snail snail = Snail.builder()
                    .name(TEST_NAME)
                    .species(TEST_SPECIES)
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .isSnailEater(true)
                    .build();

            // Act
            Inhabitant.InhabitantProperties properties = snail.getTypeSpecificProperties();

            // Assert
            assertNotNull(properties);
            assertFalse(properties.isAggressiveEater);
            assertFalse(properties.requiresSpecialFood);
            assertTrue(properties.isSnailEater);
        }
    }

    @Nested
    @DisplayName("Species Compatibility Matrix Tests")
    class SpeciesCompatibilityMatrixTests {

        @Test
        @DisplayName("Should test comprehensive compatibility matrix")
        void shouldTestComprehensiveCompatibilityMatrix() {
            // Arrange - Create different species with various properties
            Fish aggressiveFish = Fish.builder()
                    .name("Aggressive Fish")
                    .species("Piranha")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .count(3)
                    .isAggressiveEater(true)
                    .isSnailEater(true)
                    .build();

            Fish peacefulFish = Fish.builder()
                    .name("Peaceful Fish")
                    .species("Neon Tetra")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .count(2)
                    .isAggressiveEater(false)
                    .isSnailEater(false)
                    .build();

            Plant plant = Plant.builder()
                    .name("Aquatic Plant")
                    .species("Java Moss")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            Snail snail = Snail.builder()
                    .name("Garden Snail")
                    .species("Mystery Snail")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act & Assert - Test all compatibility combinations
            // Aggressive fish incompatible with snails (eats them) and peaceful fish (size difference)
            assertFalse(aggressiveFish.isCompatibleWith(snail), "Aggressive fish should not be compatible with snails");
            assertFalse(aggressiveFish.isCompatibleWith(peacefulFish), "Aggressive fish should not be compatible with smaller peaceful fish");
            assertTrue(aggressiveFish.isCompatibleWith(plant), "Aggressive fish should be compatible with plants");

            // Peaceful fish compatible with plants but not with snail-eating fish
            assertTrue(peacefulFish.isCompatibleWith(plant), "Peaceful fish should be compatible with plants");
            assertTrue(peacefulFish.isCompatibleWith(snail), "Peaceful fish should be compatible with snails");

            // Plants compatible with everything
            assertTrue(plant.isCompatibleWith(aggressiveFish), "Plants should be compatible with all species");
            assertTrue(plant.isCompatibleWith(peacefulFish), "Plants should be compatible with all species");
            assertTrue(plant.isCompatibleWith(snail), "Plants should be compatible with all species");

            // Snails incompatible with snail-eating fish but compatible with others
            assertFalse(snail.isCompatibleWith(aggressiveFish), "Snails should not be compatible with snail-eating fish");
            assertTrue(snail.isCompatibleWith(peacefulFish), "Snails should be compatible with peaceful fish");
            assertTrue(snail.isCompatibleWith(plant), "Snails should be compatible with plants");
        }

        @Test
        @DisplayName("Should handle edge cases in compatibility")
        void shouldHandleEdgeCasesInCompatibility() {
            // Arrange - Create fish with equal sizes
            Fish aggressiveFish1 = Fish.builder()
                    .name("Aggressive Fish 1")
                    .species("Predator")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .count(5)
                    .isAggressiveEater(true)
                    .build();

            Fish aggressiveFish2 = Fish.builder()
                    .name("Aggressive Fish 2")
                    .species("Predator")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .count(5)
                    .isAggressiveEater(true)
                    .build();

            Fish peacefulFishSameSize = Fish.builder()
                    .name("Peaceful Fish Same Size")
                    .species("Peaceful")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .count(5)
                    .isAggressiveEater(false)
                    .build();

            // Act & Assert
            // Two aggressive fish of same size should be compatible
            assertTrue(aggressiveFish1.isCompatibleWith(aggressiveFish2), 
                "Two aggressive fish should be compatible with each other");

            // Aggressive fish with peaceful fish of same size should be incompatible
            assertFalse(aggressiveFish1.isCompatibleWith(peacefulFishSameSize),
                "Aggressive fish should not be compatible with peaceful fish of same size");
        }
    }

    @Nested
    @DisplayName("Species Polymorphism Tests")
    class SpeciesPolymorphismTests {

        @Test
        @DisplayName("Should handle polymorphic behavior correctly")
        void shouldHandlePolymorphicBehaviorCorrectly() {
            // Arrange - Create instances as Inhabitant references
            Inhabitant fish = Fish.builder()
                    .name("Test Fish")
                    .species("Test Species")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .isAggressiveEater(true)
                    .build();

            Inhabitant plant = Plant.builder()
                    .name("Test Plant")
                    .species("Test Plant Species")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            Inhabitant snail = Snail.builder()
                    .name("Test Snail")
                    .species("Test Snail Species")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act & Assert - Test polymorphic method calls
            assertEquals("Fish", fish.getType());
            assertEquals("Fish", fish.getInhabitantType());
            assertTrue(fish.getAggressiveEater());

            assertEquals("Plant", plant.getType());
            assertEquals("Plant", plant.getInhabitantType());
            assertFalse(plant.getAggressiveEater());

            assertEquals("Snail", snail.getType());
            assertEquals("Snail", snail.getInhabitantType());
            assertFalse(snail.getAggressiveEater());
        }

        @Test
        @DisplayName("Should correctly identify species types in collections")
        void shouldCorrectlyIdentifySpeciesTypesInCollections() {
            // Arrange
            Inhabitant fish = Fish.builder()
                    .name("Test Fish")
                    .species("Test Species")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            Inhabitant plant = Plant.builder()
                    .name("Test Plant")
                    .species("Test Plant Species")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            Inhabitant snail = Snail.builder()
                    .name("Test Snail")
                    .species("Test Snail Species")
                    .ownerId(OWNER_ID)
                    .waterType(WaterType.FRESHWATER)
                    .build();

            // Act & Assert - Test instanceof checks
            assertTrue(fish instanceof Fish);
            assertTrue(fish instanceof Inhabitant);

            assertTrue(plant instanceof Plant);
            assertTrue(plant instanceof Inhabitant);

            assertTrue(snail instanceof Snail);
            assertTrue(snail instanceof Inhabitant);
        }
    }
}
