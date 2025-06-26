package nl.hu.bep.domain;

import nl.hu.bep.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class OrnamentTest {

    @Test
    void createOrnament() {
        Ornament ornament = Ornament.create("Shipwreck", 1L, Optional.of("A sunken ship."), Optional.of("brown"), Optional.of("resin"), Optional.of(true));
        assertEquals("Shipwreck", ornament.getName());
        assertEquals(1L, ornament.getOwnerId());
        assertEquals("A sunken ship.", ornament.getDescription());
        assertTrue(ornament.isAirPumpCompatible());
    }

    @Test
    void createOrnamentWithMinimalInfo() {
        Ornament ornament = Ornament.create("Rock", 1L, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertEquals("Rock", ornament.getName());
        assertEquals(1L, ornament.getOwnerId());
        assertNull(ornament.getDescription());
        assertFalse(ornament.isAirPumpCompatible());
    }

    @Test
    void updateOrnament() {
        Ornament ornament = Ornament.create("Castle", 1L, Optional.of("A small castle."), Optional.of("grey"), Optional.of("stone"), Optional.of(false));
        ornament.update(Optional.of("Large Castle"), Optional.of("A large, imposing castle."), Optional.empty(), Optional.empty(), Optional.of(true));
        assertEquals("Large Castle", ornament.getName());
        assertEquals("A large, imposing castle.", ornament.getDescription());
        assertEquals("grey", ornament.getColor());
        assertTrue(ornament.isAirPumpCompatible());
    }

    @Test
    void assignToAquarium() {
        Ornament ornament = Ornament.create("Cave", 1L, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        ornament.assignToAquarium(10L, 1L);
        assertEquals(10L, ornament.getAquariumId());
    }

    @Test
    void assignToAquariumNotOwner() {
        Ornament ornament = Ornament.create("Cave", 1L, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertThrows(ApplicationException.BusinessRuleException.class, () -> {
            ornament.assignToAquarium(10L, 2L);
        });
    }
}
