package nl.hu.bep.domain;

import nl.hu.bep.exception.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AquariumManager Domain Tests")
class AquariumManagerTest {

    @Nested
    @DisplayName("AquariumManager Creation")
    class AquariumManagerCreation {

        @Test
        @DisplayName("Should create aquarium manager with valid data")
        void shouldCreateAquariumManagerWithValidData() {
            // Given
            LocalDate installationDate = LocalDate.of(2025, 1, 15);
            String description = "Main aquarium management system";

            // When
            AquariumManager manager = AquariumManager.create(installationDate, description);

            // Then
            assertNotNull(manager);
            assertEquals(installationDate, manager.getInstallationDate());
            assertEquals(description, manager.getDescription());
            assertNotNull(manager.getDateCreated());
            assertNotNull(manager.getOwnerIds());
            assertNotNull(manager.getAquariumIds());
            assertNotNull(manager.getInhabitantIds());
            assertTrue(manager.getOwnerIds().isEmpty());
            assertTrue(manager.getAquariumIds().isEmpty());
            assertTrue(manager.getInhabitantIds().isEmpty());
        }

        @Test
        @DisplayName("Should create aquarium manager with null description")
        void shouldCreateAquariumManagerWithNullDescription() {
            // Given
            LocalDate installationDate = LocalDate.of(2025, 1, 15);

            // When
            AquariumManager manager = AquariumManager.create(installationDate, null);

            // Then
            assertNotNull(manager);
            assertEquals(installationDate, manager.getInstallationDate());
            assertNull(manager.getDescription());
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Should throw exception for null installation date")
        void shouldThrowExceptionForNullInstallationDate(LocalDate installationDate) {
            // Given
            String description = "Test description";

            // When & Then
            assertThrows(ApplicationException.class, () -> 
                AquariumManager.create(installationDate, description));
        }
    }

    @Nested
    @DisplayName("AquariumManager Reconstruction")
    class AquariumManagerReconstruction {

        @Test
        @DisplayName("Should reconstruct aquarium manager with all data")
        void shouldReconstructAquariumManagerWithAllData() {
            // Given
            Long id = 1L;
            LocalDate installationDate = LocalDate.of(2025, 1, 15);
            String description = "Reconstructed manager";
            LocalDateTime dateCreated = LocalDateTime.now().minusDays(5);
            Set<Long> ownerIds = Set.of(1L, 2L);
            Set<Long> aquariumIds = Set.of(10L, 20L);
            Set<Long> inhabitantIds = Set.of(100L, 200L);

            // When
            AquariumManager manager = AquariumManager.reconstruct(id, installationDate, description,
                dateCreated, ownerIds, aquariumIds, inhabitantIds);

            // Then
            assertEquals(id, manager.getId());
            assertEquals(installationDate, manager.getInstallationDate());
            assertEquals(description, manager.getDescription());
            assertEquals(dateCreated, manager.getDateCreated());
            assertEquals(ownerIds, manager.getOwnerIds());
            assertEquals(aquariumIds, manager.getAquariumIds());
            assertEquals(inhabitantIds, manager.getInhabitantIds());
        }

        @Test
        @DisplayName("Should reconstruct aquarium manager with null collections")
        void shouldReconstructAquariumManagerWithNullCollections() {
            // Given
            Long id = 1L;
            LocalDate installationDate = LocalDate.of(2025, 1, 15);
            String description = "Test manager";
            LocalDateTime dateCreated = LocalDateTime.now();

            // When
            AquariumManager manager = AquariumManager.reconstruct(id, installationDate, description,
                dateCreated, null, null, null);

            // Then
            assertNotNull(manager.getOwnerIds());
            assertNotNull(manager.getAquariumIds());
            assertNotNull(manager.getInhabitantIds());
            assertTrue(manager.getOwnerIds().isEmpty());
            assertTrue(manager.getAquariumIds().isEmpty());
            assertTrue(manager.getInhabitantIds().isEmpty());
        }
    }

    @Nested
    @DisplayName("Description Management")
    class DescriptionManagement {

        @Test
        @DisplayName("Should update description")
        void shouldUpdateDescription() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Original description");
            String newDescription = "Updated description";

            // When
            manager.updateDescription(newDescription);

            // Then
            assertEquals(newDescription, manager.getDescription());
        }

        @Test
        @DisplayName("Should update description to null")
        void shouldUpdateDescriptionToNull() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Original description");

            // When
            manager.updateDescription(null);

            // Then
            assertNull(manager.getDescription());
        }
    }

    @Nested
    @DisplayName("Owner Management")
    class OwnerManagement {

        @Test
        @DisplayName("Should add owner successfully")
        void shouldAddOwnerSuccessfully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long ownerId = 1L;

            // When
            manager.addOwner(ownerId);

            // Then
            assertTrue(manager.getOwnerIds().contains(ownerId));
            assertTrue(manager.hasOwners());
        }

        @Test
        @DisplayName("Should handle null owner ID gracefully")
        void shouldHandleNullOwnerIdGracefully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");

            // When
            manager.addOwner(null);

            // Then
            assertTrue(manager.getOwnerIds().isEmpty());
            assertFalse(manager.hasOwners());
        }

        @Test
        @DisplayName("Should add multiple owners")
        void shouldAddMultipleOwners() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long ownerId1 = 1L;
            Long ownerId2 = 2L;

            // When
            manager.addOwner(ownerId1);
            manager.addOwner(ownerId2);

            // Then
            assertTrue(manager.getOwnerIds().contains(ownerId1));
            assertTrue(manager.getOwnerIds().contains(ownerId2));
            assertEquals(2, manager.getOwnerIds().size());
        }

        @Test
        @DisplayName("Should not add duplicate owner")
        void shouldNotAddDuplicateOwner() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long ownerId = 1L;

            // When
            manager.addOwner(ownerId);
            manager.addOwner(ownerId); // Add again

            // Then
            assertEquals(1, manager.getOwnerIds().size());
            assertTrue(manager.getOwnerIds().contains(ownerId));
        }

        @Test
        @DisplayName("Should remove owner successfully")
        void shouldRemoveOwnerSuccessfully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long ownerId = 1L;
            manager.addOwner(ownerId);

            // When
            manager.removeOwner(ownerId);

            // Then
            assertFalse(manager.getOwnerIds().contains(ownerId));
            assertFalse(manager.hasOwners());
        }

        @Test
        @DisplayName("Should handle removing non-existent owner gracefully")
        void shouldHandleRemovingNonExistentOwnerGracefully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long ownerId = 1L;

            // When
            manager.removeOwner(ownerId);

            // Then
            assertFalse(manager.getOwnerIds().contains(ownerId));
        }
    }

    @Nested
    @DisplayName("Aquarium Management")
    class AquariumManagement {

        @Test
        @DisplayName("Should add aquarium successfully")
        void shouldAddAquariumSuccessfully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long aquariumId = 10L;

            // When
            manager.addAquarium(aquariumId);

            // Then
            assertTrue(manager.getAquariumIds().contains(aquariumId));
            assertTrue(manager.hasAquariums());
            assertEquals(1, manager.getTotalAquariumCount());
        }

        @Test
        @DisplayName("Should handle null aquarium ID gracefully")
        void shouldHandleNullAquariumIdGracefully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");

            // When
            manager.addAquarium(null);

            // Then
            assertTrue(manager.getAquariumIds().isEmpty());
            assertFalse(manager.hasAquariums());
            assertEquals(0, manager.getTotalAquariumCount());
        }

        @Test
        @DisplayName("Should add multiple aquariums")
        void shouldAddMultipleAquariums() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long aquariumId1 = 10L;
            Long aquariumId2 = 20L;

            // When
            manager.addAquarium(aquariumId1);
            manager.addAquarium(aquariumId2);

            // Then
            assertTrue(manager.getAquariumIds().contains(aquariumId1));
            assertTrue(manager.getAquariumIds().contains(aquariumId2));
            assertEquals(2, manager.getTotalAquariumCount());
        }

        @Test
        @DisplayName("Should not add duplicate aquarium")
        void shouldNotAddDuplicateAquarium() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long aquariumId = 10L;

            // When
            manager.addAquarium(aquariumId);
            manager.addAquarium(aquariumId); // Add again

            // Then
            assertEquals(1, manager.getAquariumIds().size());
            assertTrue(manager.getAquariumIds().contains(aquariumId));
            assertEquals(1, manager.getTotalAquariumCount());
        }

        @Test
        @DisplayName("Should remove aquarium successfully")
        void shouldRemoveAquariumSuccessfully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long aquariumId = 10L;
            manager.addAquarium(aquariumId);

            // When
            manager.removeAquarium(aquariumId);

            // Then
            assertFalse(manager.getAquariumIds().contains(aquariumId));
            assertFalse(manager.hasAquariums());
            assertEquals(0, manager.getTotalAquariumCount());
        }

        @Test
        @DisplayName("Should handle removing non-existent aquarium gracefully")
        void shouldHandleRemovingNonExistentAquariumGracefully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long aquariumId = 10L;

            // When
            manager.removeAquarium(aquariumId);

            // Then
            assertFalse(manager.getAquariumIds().contains(aquariumId));
        }
    }

    @Nested
    @DisplayName("Inhabitant Management")
    class InhabitantManagement {

        @Test
        @DisplayName("Should add inhabitant successfully")
        void shouldAddInhabitantSuccessfully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long inhabitantId = 100L;

            // When
            manager.addInhabitant(inhabitantId);

            // Then
            assertTrue(manager.getInhabitantIds().contains(inhabitantId));
        }

        @Test
        @DisplayName("Should handle null inhabitant ID gracefully")
        void shouldHandleNullInhabitantIdGracefully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");

            // When
            manager.addInhabitant(null);

            // Then
            assertTrue(manager.getInhabitantIds().isEmpty());
        }

        @Test
        @DisplayName("Should add multiple inhabitants")
        void shouldAddMultipleInhabitants() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long inhabitantId1 = 100L;
            Long inhabitantId2 = 200L;

            // When
            manager.addInhabitant(inhabitantId1);
            manager.addInhabitant(inhabitantId2);

            // Then
            assertTrue(manager.getInhabitantIds().contains(inhabitantId1));
            assertTrue(manager.getInhabitantIds().contains(inhabitantId2));
            assertEquals(2, manager.getInhabitantIds().size());
        }

        @Test
        @DisplayName("Should not add duplicate inhabitant")
        void shouldNotAddDuplicateInhabitant() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long inhabitantId = 100L;

            // When
            manager.addInhabitant(inhabitantId);
            manager.addInhabitant(inhabitantId); // Add again

            // Then
            assertEquals(1, manager.getInhabitantIds().size());
            assertTrue(manager.getInhabitantIds().contains(inhabitantId));
        }

        @Test
        @DisplayName("Should remove inhabitant successfully")
        void shouldRemoveInhabitantSuccessfully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long inhabitantId = 100L;
            manager.addInhabitant(inhabitantId);

            // When
            manager.removeInhabitant(inhabitantId);

            // Then
            assertFalse(manager.getInhabitantIds().contains(inhabitantId));
        }

        @Test
        @DisplayName("Should handle removing non-existent inhabitant gracefully")
        void shouldHandleRemovingNonExistentInhabitantGracefully() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            Long inhabitantId = 100L;

            // When
            manager.removeInhabitant(inhabitantId);

            // Then
            assertFalse(manager.getInhabitantIds().contains(inhabitantId));
        }
    }

    @Nested
    @DisplayName("AquariumManager State Queries")
    class AquariumManagerStateQueries {

        @Test
        @DisplayName("Should detect when manager has no aquariums")
        void shouldDetectWhenManagerHasNoAquariums() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");

            // When & Then
            assertFalse(manager.hasAquariums());
            assertEquals(0, manager.getTotalAquariumCount());
        }

        @Test
        @DisplayName("Should detect when manager has aquariums")
        void shouldDetectWhenManagerHasAquariums() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            manager.addAquarium(1L);

            // When & Then
            assertTrue(manager.hasAquariums());
            assertEquals(1, manager.getTotalAquariumCount());
        }

        @Test
        @DisplayName("Should detect when manager has no owners")
        void shouldDetectWhenManagerHasNoOwners() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");

            // When & Then
            assertFalse(manager.hasOwners());
        }

        @Test
        @DisplayName("Should detect when manager has owners")
        void shouldDetectWhenManagerHasOwners() {
            // Given
            AquariumManager manager = AquariumManager.create(LocalDate.now(), "Test manager");
            manager.addOwner(1L);

            // When & Then
            assertTrue(manager.hasOwners());
        }
    }

    @Nested
    @DisplayName("AquariumManager Equality and HashCode")
    class AquariumManagerEqualityAndHashCode {

        @Test
        @DisplayName("Should be equal when IDs are equal")
        void shouldBeEqualWhenIdsAreEqual() {
            // Given
            AquariumManager manager1 = AquariumManager.reconstruct(1L, LocalDate.now(), "Manager 1",
                LocalDateTime.now(), Set.of(1L), Set.of(10L), Set.of(100L));
            AquariumManager manager2 = AquariumManager.reconstruct(1L, LocalDate.of(2020, 1, 1), "Different Manager",
                LocalDateTime.now().minusDays(10), Set.of(2L), Set.of(20L), Set.of(200L));

            // When & Then
            assertEquals(manager1, manager2);
            assertEquals(manager1.hashCode(), manager2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when IDs are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            // Given
            AquariumManager manager1 = AquariumManager.reconstruct(1L, LocalDate.now(), "Manager 1",
                LocalDateTime.now(), new HashSet<>(), new HashSet<>(), new HashSet<>());
            AquariumManager manager2 = AquariumManager.reconstruct(2L, LocalDate.now(), "Manager 1",
                LocalDateTime.now(), new HashSet<>(), new HashSet<>(), new HashSet<>());

            // When & Then
            assertNotEquals(manager1, manager2);
        }

        @Test
        @DisplayName("Should exclude sensitive data from toString")
        void shouldExcludeSensitiveDataFromToString() {
            // Given
            AquariumManager manager = AquariumManager.reconstruct(1L, LocalDate.now(), "Test Manager",
                LocalDateTime.now(), Set.of(1L), Set.of(10L), Set.of(100L));

            // When
            String toString = manager.toString();

            // Then
            assertFalse(toString.contains("ownerIds"));
            assertFalse(toString.contains("aquariumIds"));
            assertFalse(toString.contains("inhabitantIds"));
        }
    }
}
