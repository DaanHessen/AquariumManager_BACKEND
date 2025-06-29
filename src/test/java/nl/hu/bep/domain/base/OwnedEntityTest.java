package nl.hu.bep.domain.base;

import nl.hu.bep.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OwnedEntity Unit Tests")
class OwnedEntityTest {

    private TestOwnedEntity ownedEntity;

    @BeforeEach
    void setUp() {
        // Create a concrete implementation for testing
        ownedEntity = new TestOwnedEntity();
    }

    @Nested
    @DisplayName("Ownership Validation")
    class OwnershipValidation {

        @Test
        @DisplayName("Should validate ownership successfully when owner IDs match")
        void shouldValidateOwnershipSuccessfully() {
            // Given
            Long ownerId = 1L;
            ownedEntity.setOwnerId(ownerId);

            // When & Then
            assertDoesNotThrow(() -> ownedEntity.validateOwnership(ownerId));
        }

        @Test
        @DisplayName("Should throw exception when requesting owner ID is null")
        void shouldThrowExceptionWhenRequestingOwnerIdIsNull() {
            // Given
            ownedEntity.setOwnerId(1L);

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> ownedEntity.validateOwnership(null)
            );
            
            assertTrue(exception.getMessage().contains("Requesting Owner ID"));
        }

        @Test
        @DisplayName("Should throw exception when entity has no owner")
        void shouldThrowExceptionWhenEntityHasNoOwner() {
            // Given
            ownedEntity.setOwnerId(null);
            Long requestingOwnerId = 1L;

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class,
                () -> ownedEntity.validateOwnership(requestingOwnerId)
            );
            
            assertEquals("Entity does not belong to the current user.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when owner IDs don't match")
        void shouldThrowExceptionWhenOwnerIdsDontMatch() {
            // Given
            ownedEntity.setOwnerId(1L);
            Long differentOwnerId = 2L;

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class,
                () -> ownedEntity.validateOwnership(differentOwnerId)
            );
            
            assertEquals("Entity does not belong to the current user.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Owner ID Management")
    class OwnerIdManagement {

        @Test
        @DisplayName("Should return null when no owner is set")
        void shouldReturnNullWhenNoOwnerIsSet() {
            // When & Then
            assertNull(ownedEntity.getOwnerId());
        }

        @Test
        @DisplayName("Should return correct owner ID when set")
        void shouldReturnCorrectOwnerIdWhenSet() {
            // Given
            Long expectedOwnerId = 42L;
            ownedEntity.setOwnerId(expectedOwnerId);

            // When & Then
            assertEquals(expectedOwnerId, ownedEntity.getOwnerId());
        }
    }

    // Test implementation of OwnedEntity for testing purposes
    private static class TestOwnedEntity extends OwnedEntity {
        public void setOwnerId(Long ownerId) {
            this.ownerId = ownerId;
        }
    }
}
