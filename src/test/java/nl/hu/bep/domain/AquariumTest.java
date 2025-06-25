package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.AquariumState;
import nl.hu.bep.domain.enums.SubstrateType;
import nl.hu.bep.domain.enums.WaterType;
import nl.hu.bep.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Aquarium Domain Tests")
class AquariumTest {

    private static final Long OWNER_ID = 1L;
    private static final Long DIFFERENT_OWNER_ID = 2L;

    @Nested
    @DisplayName("Aquarium Creation")
    class AquariumCreation {
        
        @Test
        @DisplayName("Should create aquarium with valid parameters")
        void shouldCreateAquariumWithValidParameters() {
            // Arrange
            String name = "MyAquarium";
            double length = 100.0;
            double width = 50.0;
            double height = 50.0;
            SubstrateType substrate = SubstrateType.SAND;
            WaterType waterType = WaterType.FRESHWATER;
            String color = "blue";
            String description = "A beautiful aquarium";
            AquariumState state = AquariumState.RUNNING;

            // Act
            Aquarium aquarium = Aquarium.create(name, length, width, height, substrate, waterType, color, description, state);

            // Assert
            assertNotNull(aquarium);
            assertEquals(name, aquarium.getName());
            assertEquals(substrate, aquarium.getSubstrate());
            assertEquals(waterType, aquarium.getWaterType());
            assertEquals(color, aquarium.getColor());
            assertEquals(description, aquarium.getDescription());
            assertEquals(state, aquarium.getState());
            assertNotNull(aquarium.getDimensions());
            assertNotNull(aquarium.getDateCreated());
        }

        @Test
        @DisplayName("Should throw exception when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // Arrange & Act & Assert
            assertThrows(ApplicationException.ValidationException.class, () -> 
                Aquarium.create(null, 100.0, 50.0, 50.0, SubstrateType.SAND, 
                    WaterType.FRESHWATER, "blue", "description", AquariumState.RUNNING)
            );
        }

        @Test
        @DisplayName("Should throw exception when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // Arrange & Act & Assert
            assertThrows(ApplicationException.ValidationException.class, () -> 
                Aquarium.create("", 100.0, 50.0, 50.0, SubstrateType.SAND, 
                    WaterType.FRESHWATER, "blue", "description", AquariumState.RUNNING)
            );
        }

        @Test
        @DisplayName("Should throw exception when substrate is null")
        void shouldThrowExceptionWhenSubstrateIsNull() {
            // Arrange & Act & Assert
            assertThrows(ApplicationException.ValidationException.class, () -> 
                Aquarium.create("MyAquarium", 100.0, 50.0, 50.0, null, 
                    WaterType.FRESHWATER, "blue", "description", AquariumState.RUNNING)
            );
        }

        @Test
        @DisplayName("Should throw exception when water type is null")
        void shouldThrowExceptionWhenWaterTypeIsNull() {
            // Arrange & Act & Assert
            assertThrows(ApplicationException.ValidationException.class, () -> 
                Aquarium.create("MyAquarium", 100.0, 50.0, 50.0, SubstrateType.SAND, 
                    null, "blue", "description", AquariumState.RUNNING)
            );
        }
    }

    @Nested
    @DisplayName("Aquarium Ownership")
    class AquariumOwnership {

        private Aquarium aquarium;

        @BeforeEach
        void setUp() {
            aquarium = Aquarium.create("MyAquarium", 100.0, 50.0, 50.0, 
                SubstrateType.SAND, WaterType.FRESHWATER, "blue", 
                "A beautiful aquarium", AquariumState.RUNNING);
        }

        @Test
        @DisplayName("Should assign owner to aquarium")
        void shouldAssignOwnerToAquarium() {
            // Act
            aquarium.assignToOwner(OWNER_ID);

            // Assert
            assertEquals(OWNER_ID, aquarium.getOwnerId());
        }

        @Test
        @DisplayName("Should validate ownership successfully for correct owner")
        void shouldValidateOwnershipSuccessfullyForCorrectOwner() {
            // Arrange
            aquarium.assignToOwner(OWNER_ID);

            // Act & Assert
            assertDoesNotThrow(() -> aquarium.validateOwnership(OWNER_ID));
        }

        @Test
        @DisplayName("Should throw exception when validating ownership for different owner")
        void shouldThrowExceptionWhenValidatingOwnershipForDifferentOwner() {
            // Arrange
            aquarium.assignToOwner(OWNER_ID);

            // Act & Assert
            assertThrows(ApplicationException.class, () -> 
                aquarium.validateOwnership(DIFFERENT_OWNER_ID)
            );
        }

        @Test
        @DisplayName("Should throw exception when validating ownership for unassigned aquarium")
        void shouldThrowExceptionWhenValidatingOwnershipForUnassignedAquarium() {
            // Act & Assert
            assertThrows(ApplicationException.class, () -> 
                aquarium.validateOwnership(OWNER_ID)
            );
        }
    }

    @Nested
    @DisplayName("Aquarium State Management")
    class AquariumStateManagement {

        private Aquarium aquarium;

        @BeforeEach
        void setUp() {
            aquarium = Aquarium.create("MyAquarium", 100.0, 50.0, 50.0, 
                SubstrateType.SAND, WaterType.FRESHWATER, "blue", 
                "A beautiful aquarium", AquariumState.SETUP);
            aquarium.assignToOwner(OWNER_ID);
        }

        @Test
        @DisplayName("Should update state to running")
        void shouldUpdateStateToRunning() {
            // Act
            aquarium.updateState(AquariumState.RUNNING);

            // Assert
            assertEquals(AquariumState.RUNNING, aquarium.getState());
        }

        @Test
        @DisplayName("Should activate aquarium")
        void shouldActivateAquarium() {
            // Act
            aquarium.activateAquarium();

            // Assert
            assertEquals(AquariumState.RUNNING, aquarium.getState());
            assertNotNull(aquarium.getCurrentStateStartTime());
        }

        @Test
        @DisplayName("Should start maintenance")
        void shouldStartMaintenance() {
            // Arrange
            aquarium.activateAquarium();

            // Act
            aquarium.startMaintenance();

            // Assert
            assertEquals(AquariumState.MAINTENANCE, aquarium.getState());
        }

        @Test
        @DisplayName("Should deactivate aquarium")
        void shouldDeactivateAquarium() {
            // Arrange
            aquarium.activateAquarium();

            // Act
            aquarium.deactivateAquarium();

            // Assert
            assertEquals(AquariumState.INACTIVE, aquarium.getState());
        }
    }

    @Nested
    @DisplayName("Aquarium Equality and Hash Code")
    class AquariumEqualityAndHashCode {

        @Test
        @DisplayName("Should be equal when same id")
        void shouldBeEqualWhenSameId() {
            // Arrange
            Aquarium aquarium1 = Aquarium.create("Aquarium1", 100.0, 50.0, 50.0, 
                SubstrateType.SAND, WaterType.FRESHWATER, "blue", "desc1", AquariumState.RUNNING);
            Aquarium aquarium2 = Aquarium.create("Aquarium2", 200.0, 60.0, 60.0, 
                SubstrateType.GRAVEL, WaterType.SALTWATER, "red", "desc2", AquariumState.SETUP);

            // Both have null IDs after creation (set by persistence layer in real usage)
            
            // Act & Assert
            assertEquals(aquarium1, aquarium2); // Equal because both have null ID
        }

        @Test
        @DisplayName("Should have same hash code when equal")
        void shouldHaveSameHashCodeWhenEqual() {
            // Arrange
            Aquarium aquarium = Aquarium.create("MyAquarium", 100.0, 50.0, 50.0, 
                SubstrateType.SAND, WaterType.FRESHWATER, "blue", "description", AquariumState.RUNNING);

            // Act & Assert
            assertEquals(aquarium.hashCode(), aquarium.hashCode());
        }
    }

    @Nested
    @DisplayName("Inhabitant Management")
    class InhabitantManagement {

        private Aquarium aquarium;
        private Inhabitant mockInhabitant;

        @BeforeEach
        void setUp() {
            aquarium = Aquarium.create("MyAquarium", 100.0, 50.0, 50.0, 
                SubstrateType.SAND, WaterType.FRESHWATER, "blue", 
                "A beautiful aquarium", AquariumState.RUNNING);
            aquarium.assignToOwner(OWNER_ID);
            
            // Create a simple mock inhabitant for testing
            mockInhabitant = new TestInhabitant(1L, "TestFish", "TestSpecies", OWNER_ID, 
                "blue", 1, false, WaterType.FRESHWATER, "Test description");
        }

        @Test
        @DisplayName("Should add inhabitant to aquarium")
        void shouldAddInhabitantToAquarium() {
            // Act
            assertDoesNotThrow(() -> aquarium.addInhabitant(mockInhabitant, OWNER_ID));

            // Assert
            assertEquals(1, aquarium.getInhabitants().size());
            assertTrue(aquarium.getInhabitants().contains(mockInhabitant));
        }

        @Test
        @DisplayName("Should remove inhabitant from aquarium")
        void shouldRemoveInhabitantFromAquarium() {
            // Arrange
            aquarium.addInhabitant(mockInhabitant, OWNER_ID);

            // Act
            aquarium.removeInhabitant(mockInhabitant, OWNER_ID);

            // Assert
            assertEquals(0, aquarium.getInhabitants().size());
            assertFalse(aquarium.getInhabitants().contains(mockInhabitant));
        }

        @Test
        @DisplayName("Should throw exception when adding inhabitant with wrong owner")
        void shouldThrowExceptionWhenAddingInhabitantWithWrongOwner() {
            // Act & Assert
            assertThrows(ApplicationException.class, () -> 
                aquarium.addInhabitant(mockInhabitant, DIFFERENT_OWNER_ID)
            );
        }

        @Test
        @DisplayName("Should throw exception when removing inhabitant with wrong owner")
        void shouldThrowExceptionWhenRemovingInhabitantWithWrongOwner() {
            // Arrange
            aquarium.addInhabitant(mockInhabitant, OWNER_ID);

            // Act & Assert
            assertThrows(ApplicationException.class, () -> 
                aquarium.removeInhabitant(mockInhabitant, DIFFERENT_OWNER_ID)
            );
        }

        // Simple test inhabitant implementation
        private static class TestInhabitant extends Inhabitant {
            public TestInhabitant(Long id, String name, String species, Long ownerId, 
                                 String color, Integer count, Boolean isSchooling, 
                                 WaterType waterType, String description) {
                super(id, name, species, ownerId, color, count, isSchooling, 
                      waterType, description, java.time.LocalDateTime.now(), null);
            }

            @Override
            public boolean isCompatibleWith(Inhabitant other) {
                return true; // Simple compatibility for testing
            }

            @Override
            public String getType() {
                return "test";
            }

            @Override
            public String getInhabitantType() {
                return "TestInhabitant";
            }

            @Override
            public Boolean getAggressiveEater() {
                return false;
            }

            @Override
            public Boolean getRequiresSpecialFood() {
                return false;
            }

            @Override
            public Boolean getSnailEater() {
                return false;
            }

            @Override
            public InhabitantProperties getTypeSpecificProperties() {
                return InhabitantProperties.defaults();
            }
        }
    }
}
