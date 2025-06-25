package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.species.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InhabitantTest {

    @Test
    void testInhabitantFactory() {
        Inhabitant.InhabitantProperties fishProperties = new Inhabitant.InhabitantProperties(true, true, false);
        Inhabitant fish = Inhabitant.create("fish", "Nemo", "Clownfish", 1L, Optional.of("Orange"), Optional.of(1), Optional.of(false), Optional.of(WaterType.SALTWATER), Optional.of("A friendly fish"), fishProperties);
        assertTrue(fish instanceof Fish);
        assertEquals("Nemo", fish.getName());
        assertEquals(fishProperties, fish.getTypeSpecificProperties());

        Inhabitant.InhabitantProperties snailProperties = new Inhabitant.InhabitantProperties(false, false, true);
        Inhabitant snail = Inhabitant.create("snail", "Gary", "Garden Snail", 1L, Optional.of("Brown"), Optional.of(1), Optional.of(false), Optional.of(WaterType.FRESHWATER), Optional.of("A slow snail"), snailProperties);
        assertTrue(snail instanceof Snail);
        assertEquals("Gary", snail.getName());
        assertEquals(snailProperties, snail.getTypeSpecificProperties());

        Inhabitant shrimp = Inhabitant.create("shrimp", "Shrimpy", "Cherry Shrimp", 1L, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Inhabitant.InhabitantProperties.defaults());
        assertTrue(shrimp instanceof Shrimp);

        assertThrows(IllegalArgumentException.class, () -> Inhabitant.create("unknown", "Unknown", "Unknown", 1L, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Inhabitant.InhabitantProperties.defaults()));
    }
}
