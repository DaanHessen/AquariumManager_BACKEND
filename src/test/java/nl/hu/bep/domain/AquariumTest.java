package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.domain.Inhabitant.InhabitantProperties;
import nl.hu.bep.exception.domain.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AquariumTest {

    private Aquarium aquarium;
    private static final Long OWNER_ID = 1L;

    @BeforeEach
    void setUp() {
        aquarium = Aquarium.create("MyAquarium", 100, 50, 50, SubstrateType.SAND, WaterType.FRESHWATER, "blue", "A beautiful aquarium", AquariumState.RUNNING);
        aquarium.assignToOwner(OWNER_ID);
    }

    @Test
    void testAddCompatibleInhabitant() {
        Inhabitant fish = Inhabitant.create("fish", "Nemo", "Clownfish", OWNER_ID, Optional.of("Orange"), Optional.of(1), Optional.of(false), Optional.of(WaterType.FRESHWATER), Optional.of("A friendly fish"), new InhabitantProperties(false, false, false));
        assertDoesNotThrow(() -> aquarium.addInhabitant(fish, OWNER_ID));
        assertEquals(1, aquarium.getInhabitants().size());
    }

    @Test
    void testAddIncompatibleInhabitant() {
        Inhabitant snailEater = Inhabitant.create("fish", "KillerFish", "Puffer", OWNER_ID, Optional.of("Yellow"), Optional.of(1), Optional.of(false), Optional.of(WaterType.FRESHWATER), Optional.of("Eats snails"), new InhabitantProperties(false, false, true));
        Inhabitant snail = Inhabitant.create("snail", "Gary", "Garden Snail", OWNER_ID, Optional.of("Brown"), Optional.of(1), Optional.of(false), Optional.of(WaterType.FRESHWATER), Optional.of("A slow snail"), new InhabitantProperties(false, false, false));

        assertDoesNotThrow(() -> aquarium.addInhabitant(snail, OWNER_ID));
        assertThrows(DomainException.class, () -> aquarium.addInhabitant(snailEater, OWNER_ID));
    }
}
