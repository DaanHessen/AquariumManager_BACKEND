package nl.hu.bep.domain.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AssignableEntity Unit Tests")
class AssignableEntityTest {

    private TestAssignableEntity assignableEntity;

    @BeforeEach
    void setUp() {
        assignableEntity = new TestAssignableEntity();
    }

    @Nested
    @DisplayName("Aquarium Assignment")
    class AquariumAssignment {

        @Test
        @DisplayName("Should assign entity to aquarium")
        void shouldAssignEntityToAquarium() {
            // Given
            Long aquariumId = 1L;

            // When
            assignableEntity.assignToAquarium(aquariumId);

            // Then
            assertEquals(aquariumId, assignableEntity.getAquariumId());
            assertTrue(assignableEntity.isAssignedToAquarium());
        }

        @Test
        @DisplayName("Should reassign entity to different aquarium")
        void shouldReassignEntityToDifferentAquarium() {
            // Given
            Long firstAquariumId = 1L;
            Long secondAquariumId = 2L;
            assignableEntity.assignToAquarium(firstAquariumId);

            // When
            assignableEntity.assignToAquarium(secondAquariumId);

            // Then
            assertEquals(secondAquariumId, assignableEntity.getAquariumId());
            assertTrue(assignableEntity.isAssignedToAquarium());
        }

        @Test
        @DisplayName("Should handle null aquarium ID assignment")
        void shouldHandleNullAquariumIdAssignment() {
            // Given
            assignableEntity.assignToAquarium(1L);

            // When
            assignableEntity.assignToAquarium(null);

            // Then
            assertNull(assignableEntity.getAquariumId());
            assertFalse(assignableEntity.isAssignedToAquarium());
        }
    }

    @Nested
    @DisplayName("Aquarium Removal")
    class AquariumRemoval {

        @Test
        @DisplayName("Should remove entity from aquarium")
        void shouldRemoveEntityFromAquarium() {
            // Given
            assignableEntity.assignToAquarium(1L);

            // When
            assignableEntity.removeFromAquarium();

            // Then
            assertNull(assignableEntity.getAquariumId());
            assertFalse(assignableEntity.isAssignedToAquarium());
        }

        @Test
        @DisplayName("Should handle removal when not assigned")
        void shouldHandleRemovalWhenNotAssigned() {
            // Given - entity is not assigned to any aquarium

            // When
            assignableEntity.removeFromAquarium();

            // Then
            assertNull(assignableEntity.getAquariumId());
            assertFalse(assignableEntity.isAssignedToAquarium());
        }
    }

    @Nested
    @DisplayName("Assignment Status")
    class AssignmentStatus {

        @Test
        @DisplayName("Should return false when not assigned to aquarium")
        void shouldReturnFalseWhenNotAssignedToAquarium() {
            // When & Then
            assertFalse(assignableEntity.isAssignedToAquarium());
            assertNull(assignableEntity.getAquariumId());
        }

        @Test
        @DisplayName("Should return true when assigned to aquarium")
        void shouldReturnTrueWhenAssignedToAquarium() {
            // Given
            assignableEntity.assignToAquarium(1L);

            // When & Then
            assertTrue(assignableEntity.isAssignedToAquarium());
            assertNotNull(assignableEntity.getAquariumId());
        }
    }

    @Nested
    @DisplayName("Aquarium ID Retrieval")
    class AquariumIdRetrieval {

        @Test
        @DisplayName("Should return null when no aquarium assigned")
        void shouldReturnNullWhenNoAquariumAssigned() {
            // When & Then
            assertNull(assignableEntity.getAquariumId());
        }

        @Test
        @DisplayName("Should return correct aquarium ID when assigned")
        void shouldReturnCorrectAquariumIdWhenAssigned() {
            // Given
            Long expectedAquariumId = 42L;
            assignableEntity.assignToAquarium(expectedAquariumId);

            // When & Then
            assertEquals(expectedAquariumId, assignableEntity.getAquariumId());
        }
    }

    // Test implementation of AssignableEntity for testing purposes
    private static class TestAssignableEntity extends AssignableEntity {
        
        // Make protected methods accessible for testing
        @Override
        public void assignToAquarium(Long aquariumId) {
            super.assignToAquarium(aquariumId);
        }

        @Override
        public void removeFromAquarium() {
            super.removeFromAquarium();
        }
    }
}
