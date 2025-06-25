package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.species.*;
import nl.hu.bep.application.factory.InhabitantFactory;
import nl.hu.bep.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InhabitantTest {

    private InhabitantFactory inhabitantFactory;

    @BeforeEach
    void setUp() {
        inhabitantFactory = new InhabitantFactory();
    }

    @Test
    void testInhabitantFactory() {
        Inhabitant.InhabitantProperties fishProperties = new Inhabitant.InhabitantProperties(true, true, false);
        Inhabitant fish = inhabitantFactory.createInhabitant("fish", "Clownfish", "Nemo", 1L, Optional.of("Orange"), Optional.of(1), Optional.of(false), Optional.of(WaterType.SALTWATER), Optional.of("A friendly fish"), fishProperties);
        assertTrue(fish instanceof Fish);
        assertEquals("Nemo", fish.getName());
        assertEquals(fishProperties, fish.getTypeSpecificProperties());

        Inhabitant.InhabitantProperties snailProperties = new Inhabitant.InhabitantProperties(false, false, true);
        Inhabitant snail = inhabitantFactory.createInhabitant("snail", "Garden Snail", "Gary", 1L, Optional.of("Brown"), Optional.of(1), Optional.of(false), Optional.of(WaterType.FRESHWATER), Optional.of("A slow snail"), snailProperties);
        assertTrue(snail instanceof Snail);
        assertEquals("Gary", snail.getName());
        assertEquals(snailProperties, snail.getTypeSpecificProperties());

        Inhabitant shrimp = inhabitantFactory.createInhabitant("shrimp", "Cherry Shrimp", "Shrimpy", 1L, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Inhabitant.InhabitantProperties.defaults());
        assertTrue(shrimp instanceof Shrimp);

        assertThrows(ApplicationException.ValidationException.class, () -> inhabitantFactory.createInhabitant("unknown", "Unknown", "Unknown", 1L, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Inhabitant.InhabitantProperties.defaults()));
    }
}
