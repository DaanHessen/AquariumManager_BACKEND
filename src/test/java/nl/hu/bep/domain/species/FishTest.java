package nl.hu.bep.domain.species;

import nl.hu.bep.domain.enums.WaterType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FishTest {

    @Test
    void testFishCreationWithValidData() {
        Fish fish = Fish.builder()
                .name("Goldfish")
                .species("Carassius auratus")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Orange")
                .count(1)
                .isSchooling(false)
                .isAggressiveEater(false)
                .requiresSpecialFood(false)
                .isSnailEater(false)
                .dateCreated(LocalDateTime.now())
                .build();

        assertEquals("Fish", fish.getType());
        assertEquals("Goldfish", fish.getName());
        assertEquals("Carassius auratus", fish.getSpecies());
        assertEquals("Orange", fish.getColor());
        assertEquals(WaterType.FRESHWATER, fish.getWaterType());
    }

    @Test
    @Disabled
    void testFishCreationWithNullSpecies() {
        assertThrows(IllegalArgumentException.class, () -> {
            Fish.builder()
                    .name("Goldfish")
                    .species(null)
                    .ownerId(1L)
                    .waterType(WaterType.FRESHWATER)
                    .color("Orange")
                    .build();
        });
    }

    @Test
    @Disabled
    void testFishCreationWithEmptySpecies() {
        assertThrows(IllegalArgumentException.class, () -> {
            Fish.builder()
                    .name("Goldfish")
                    .species("")
                    .ownerId(1L)
                    .waterType(WaterType.FRESHWATER)
                    .color("Orange")
                    .build();
        });
    }

    @Test
    @Disabled
    void testFishCreationWithNullWaterType() {
        assertThrows(IllegalArgumentException.class, () -> {
            Fish.builder()
                    .name("Goldfish")
                    .species("Carassius auratus")
                    .ownerId(1L)
                    .waterType(null)
                    .color("Orange")
                    .build();
        });
    }

    @Test
    @Disabled
    void testFishCreationWithNullColor() {
        assertThrows(IllegalArgumentException.class, () -> {
            Fish.builder()
                    .name("Goldfish")
                    .species("Carassius auratus")
                    .ownerId(1L)
                    .waterType(WaterType.FRESHWATER)
                    .color(null)
                    .build();
        });
    }

    @Test
    void testFishEquals() {
        Fish fish1 = Fish.builder()
                .id(1L)
                .name("Goldfish")
                .species("Carassius auratus")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Orange")
                .build();
        
        Fish fish2 = Fish.builder()
                .id(1L)
                .name("Goldfish")
                .species("Carassius auratus")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Orange")
                .build();

        assertEquals(fish1, fish2);
    }

    @Test
    void testFishNotEquals() {
        Fish fish1 = Fish.builder()
                .id(1L)
                .name("Goldfish")
                .species("Carassius auratus")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Orange")
                .build();
        
        Fish fish2 = Fish.builder()
                .id(2L)
                .name("Goldfish")
                .species("Carassius auratus")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Orange")
                .build();

        assertNotEquals(fish1, fish2);
    }

    @Test
    void testFishHashCode() {
        Fish fish1 = Fish.builder()
                .id(1L)
                .name("Goldfish")
                .species("Carassius auratus")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Orange")
                .build();
        
        Fish fish2 = Fish.builder()
                .id(1L)
                .name("Goldfish")
                .species("Carassius auratus")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Orange")
                .build();

        assertEquals(fish1.hashCode(), fish2.hashCode());
    }

    @Test
    @Disabled
    void testFishToString() {
        Fish fish = Fish.builder()
                .id(1L)
                .name("Goldfish")
                .species("Carassius auratus")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Orange")
                .build();

        String result = fish.toString();
        assertNotNull(result);
        assertTrue(result.contains("Fish"));
    }

    @Test
    void testAggressiveEaterCompatibility() {
        Fish aggressiveFish = Fish.builder()
                .name("Aggressive Fish")
                .species("Aggressive species")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Red")
                .count(5)
                .isAggressiveEater(true)
                .build();

        Fish peacefulFish = Fish.builder()
                .name("Peaceful Fish")
                .species("Peaceful species")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Blue")
                .count(3)
                .isAggressiveEater(false)
                .build();

        assertFalse(aggressiveFish.isCompatibleWith(peacefulFish));
    }

    @Test
    void testSnailEaterCompatibility() {
        Fish snailEater = Fish.builder()
                .name("Snail Eater")
                .species("Snail eating species")
                .ownerId(1L)
                .waterType(WaterType.FRESHWATER)
                .color("Green")
                .isSnailEater(true)
                .build();

        // Create a mock snail for testing
        nl.hu.bep.domain.Inhabitant mockSnail = new nl.hu.bep.domain.Inhabitant() {
            @Override
            public String getInhabitantType() { return "Snail"; }
            @Override
            public String getType() { return "Snail"; }
            @Override
            public InhabitantProperties getTypeSpecificProperties() { return null; }
            @Override
            public boolean isCompatibleWith(nl.hu.bep.domain.Inhabitant other) { return true; }
            @Override
            public Boolean getAggressiveEater() { return false; }
            @Override
            public Boolean getRequiresSpecialFood() { return false; }
            @Override
            public Boolean getSnailEater() { return false; }
        };

        assertFalse(snailEater.isCompatibleWith(mockSnail));
    }
}
