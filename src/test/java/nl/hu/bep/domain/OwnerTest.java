package nl.hu.bep.domain;

import nl.hu.bep.domain.enums.Role;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.config.AquariumConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Owner Domain Tests")
class OwnerTest {

    @Nested
    @DisplayName("Owner Creation")
    class OwnerCreation {

        @Test
        @DisplayName("Should create owner with valid data")
        void shouldCreateOwnerWithValidData() {
            // Given
            String firstName = "John";
            String lastName = "Doe";
            String email = "john.doe@example.com";
            String password = "ValidPassword123!";

            // When
            Owner owner = Owner.create(firstName, lastName, email, password);

            // Then
            assertNotNull(owner);
            assertEquals(firstName, owner.getFirstName());
            assertEquals(lastName, owner.getLastName());
            assertEquals(email, owner.getEmail());
            assertEquals(Role.OWNER, owner.getRole());
            assertNotNull(owner.getDateCreated());
            assertNotNull(owner.getAquariumIds());
            assertTrue(owner.getAquariumIds().isEmpty());
            assertEquals(AquariumConstants.DEFAULT_AQUARIUM_MANAGER_ID, owner.getAquariumManagerId());
            assertTrue(owner.verifyPassword(password));
        }

        @Test
        @DisplayName("Should create owner with hashed password")
        void shouldCreateOwnerWithHashedPassword() {
            // Given
            String firstName = "Jane";
            String lastName = "Smith";
            String email = "jane.smith@example.com";
            String hashedPassword = BCrypt.hashpw("password123", BCrypt.gensalt());

            // When
            Owner owner = Owner.createWithHashedPassword(firstName, lastName, email, hashedPassword);

            // Then
            assertNotNull(owner);
            assertEquals(firstName, owner.getFirstName());
            assertEquals(lastName, owner.getLastName());
            assertEquals(email, owner.getEmail());
            assertEquals(hashedPassword, owner.getPassword());
            assertEquals(Role.OWNER, owner.getRole());
            assertNotNull(owner.getDateCreated());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("Should throw exception for invalid first name")
        void shouldThrowExceptionForInvalidFirstName(String firstName) {
            // Given
            String lastName = "Doe";
            String email = "john.doe@example.com";
            String password = "ValidPassword123!";

            // When & Then
            assertThrows(ApplicationException.class, () -> 
                Owner.create(firstName, lastName, email, password));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("Should throw exception for invalid last name")
        void shouldThrowExceptionForInvalidLastName(String lastName) {
            // Given
            String firstName = "John";
            String email = "john.doe@example.com";
            String password = "ValidPassword123!";

            // When & Then
            assertThrows(ApplicationException.class, () -> 
                Owner.create(firstName, lastName, email, password));
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid-email", "test@", "@example.com", "test.example.com"})
        @DisplayName("Should throw exception for invalid email")
        void shouldThrowExceptionForInvalidEmail(String email) {
            // Given
            String firstName = "John";
            String lastName = "Doe";
            String password = "ValidPassword123!";

            // When & Then
            assertThrows(ApplicationException.class, () -> 
                Owner.create(firstName, lastName, email, password));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "123", "abc"})
        @DisplayName("Should throw exception for invalid password")
        void shouldThrowExceptionForInvalidPassword(String password) {
            // Given
            String firstName = "John";
            String lastName = "Doe";
            String email = "john.doe@example.com";

            // When & Then
            assertThrows(ApplicationException.class, () -> 
                Owner.create(firstName, lastName, email, password));
        }
    }

    @Nested
    @DisplayName("Password Management")
    class PasswordManagement {

        @Test
        @DisplayName("Should verify password correctly")
        void shouldVerifyPasswordCorrectly() {
            // Given
            String password = "ValidPassword123!";
            Owner owner = Owner.create("John", "Doe", "john@example.com", password);

            // When & Then
            assertTrue(owner.verifyPassword(password));
            assertFalse(owner.verifyPassword("WrongPassword"));
        }

        @Test
        @DisplayName("Should change password with valid current password")
        void shouldChangePasswordWithValidCurrentPassword() {
            // Given
            String currentPassword = "ValidPassword123!";
            String newPassword = "NewValidPassword456!";
            Owner owner = Owner.create("John", "Doe", "john@example.com", currentPassword);

            // When
            owner.changePassword(currentPassword, newPassword);

            // Then
            assertFalse(owner.verifyPassword(currentPassword));
            assertTrue(owner.verifyPassword(newPassword));
        }

        @Test
        @DisplayName("Should throw exception when changing password with wrong current password")
        void shouldThrowExceptionWhenChangingPasswordWithWrongCurrentPassword() {
            // Given
            String currentPassword = "ValidPassword123!";
            String wrongPassword = "WrongPassword123!";
            String newPassword = "NewValidPassword456!";
            Owner owner = Owner.create("John", "Doe", "john@example.com", currentPassword);

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class, 
                () -> owner.changePassword(wrongPassword, newPassword)
            );
            assertEquals("Current password is incorrect", exception.getMessage());
        }

        @Test
        @DisplayName("Should update password directly")
        void shouldUpdatePasswordDirectly() {
            // Given
            String initialPassword = "ValidPassword123!";
            String newPassword = "NewValidPassword456!";
            Owner owner = Owner.create("John", "Doe", "john@example.com", initialPassword);

            // When
            owner.updatePassword(newPassword);

            // Then
            assertTrue(owner.verifyPassword(newPassword));
            assertFalse(owner.verifyPassword(initialPassword));
        }
    }

    @Nested
    @DisplayName("Profile Management")
    class ProfileManagement {

        @Test
        @DisplayName("Should change email to valid new email")
        void shouldChangeEmailToValidNewEmail() {
            // Given
            String initialEmail = "john@example.com";
            String newEmail = "john.doe@example.com";
            Owner owner = Owner.create("John", "Doe", initialEmail, "ValidPassword123!");

            // When
            owner.changeEmail(newEmail);

            // Then
            assertEquals(newEmail, owner.getEmail());
        }

        @Test
        @DisplayName("Should throw exception when changing to same email")
        void shouldThrowExceptionWhenChangingToSameEmail() {
            // Given
            String email = "john@example.com";
            Owner owner = Owner.create("John", "Doe", email, "ValidPassword123!");

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class, 
                () -> owner.changeEmail(email)
            );
            assertEquals("New email is the same as current email", exception.getMessage());
        }

        @Test
        @DisplayName("Should update profile with new first and last name")
        void shouldUpdateProfileWithNewFirstAndLastName() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            String newFirstName = "Johnny";
            String newLastName = "Smith";

            // When
            owner.updateProfile(newFirstName, newLastName);

            // Then
            assertEquals(newFirstName, owner.getFirstName());
            assertEquals(newLastName, owner.getLastName());
        }

        @Test
        @DisplayName("Should update profile with only first name")
        void shouldUpdateProfileWithOnlyFirstName() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            String newFirstName = "Johnny";
            String originalLastName = owner.getLastName();

            // When
            owner.updateProfile(newFirstName, null);

            // Then
            assertEquals(newFirstName, owner.getFirstName());
            assertEquals(originalLastName, owner.getLastName());
        }

        @Test
        @DisplayName("Should get full name")
        void shouldGetFullName() {
            // Given
            String firstName = "John";
            String lastName = "Doe";
            Owner owner = Owner.create(firstName, lastName, "john@example.com", "ValidPassword123!");

            // When
            String fullName = owner.getFullName();

            // Then
            assertEquals(firstName + " " + lastName, fullName);
        }
    }

    @Nested
    @DisplayName("Role Management")
    class RoleManagement {

        @Test
        @DisplayName("Should promote owner to admin")
        void shouldPromoteOwnerToAdmin() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");

            // When
            owner.promoteToAdmin();

            // Then
            assertEquals(Role.ADMIN, owner.getRole());
            assertTrue(owner.isAdmin());
            assertFalse(owner.isOwner());
        }

        @Test
        @DisplayName("Should throw exception when promoting admin to admin")
        void shouldThrowExceptionWhenPromotingAdminToAdmin() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            owner.promoteToAdmin();

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class, 
                owner::promoteToAdmin
            );
            assertEquals("Owner is already an admin", exception.getMessage());
        }

        @Test
        @DisplayName("Should demote admin to owner")
        void shouldDemoteAdminToOwner() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            owner.promoteToAdmin();

            // When
            owner.demoteToOwner();

            // Then
            assertEquals(Role.OWNER, owner.getRole());
            assertTrue(owner.isOwner());
            assertFalse(owner.isAdmin());
        }

        @Test
        @DisplayName("Should throw exception when demoting owner to owner")
        void shouldThrowExceptionWhenDemotingOwnerToOwner() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class, 
                owner::demoteToOwner
            );
            assertEquals("Owner is already a regular owner", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Aquarium Management")
    class AquariumManagement {

        @Test
        @DisplayName("Should register aquarium successfully")
        void shouldRegisterAquariumSuccessfully() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            Long aquariumId = 1L;

            // When
            owner.registerAquarium(aquariumId);

            // Then
            assertTrue(owner.getAquariumIds().contains(aquariumId));
            assertTrue(owner.hasAquariums());
            assertEquals(1, owner.getAquariumCount());
        }

        @Test
        @DisplayName("Should throw exception when registering duplicate aquarium")
        void shouldThrowExceptionWhenRegisteringDuplicateAquarium() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            Long aquariumId = 1L;
            owner.registerAquarium(aquariumId);

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class, 
                () -> owner.registerAquarium(aquariumId)
            );
            assertEquals("Aquarium is already registered to this owner", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when registering null aquarium ID")
        void shouldThrowExceptionWhenRegisteringNullAquariumId() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");

            // When & Then
            assertThrows(ApplicationException.class, () -> owner.registerAquarium(null));
        }

        @Test
        @DisplayName("Should unregister aquarium successfully")
        void shouldUnregisterAquariumSuccessfully() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            Long aquariumId = 1L;
            owner.registerAquarium(aquariumId);

            // When
            owner.unregisterAquarium(aquariumId);

            // Then
            assertFalse(owner.getAquariumIds().contains(aquariumId));
            assertFalse(owner.hasAquariums());
            assertEquals(0, owner.getAquariumCount());
        }

        @Test
        @DisplayName("Should throw exception when unregistering non-existent aquarium")
        void shouldThrowExceptionWhenUnregisteringNonExistentAquarium() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            Long aquariumId = 1L;

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class, 
                () -> owner.unregisterAquarium(aquariumId)
            );
            assertEquals("Aquarium is not registered to this owner", exception.getMessage());
        }

        @Test
        @DisplayName("Should add aquarium from Aquarium object")
        void shouldAddAquariumFromAquariumObject() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            // Create a mock aquarium with ID for testing
            Aquarium aquarium = Aquarium.reconstruct(1L, "Test Tank", null, null, null, 20.0, 
                null, null, null, null, null, null, owner.getId());

            // When
            owner.addToAquariums(aquarium);

            // Then
            assertTrue(owner.getAquariumIds().contains(aquarium.getId()));
        }
    }

    @Nested
    @DisplayName("Manager Assignment")
    class ManagerAssignment {

        @Test
        @DisplayName("Should assign to manager")
        void shouldAssignToManager() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            Long managerId = 2L;

            // When
            owner.assignToManager(managerId);

            // Then
            assertEquals(managerId, owner.getAquariumManagerId());
            assertTrue(owner.isManagedBy(managerId));
            assertFalse(owner.isUnassigned());
        }

        @Test
        @DisplayName("Should detect unassigned owner")
        void shouldDetectUnassignedOwner() {
            // Given - Owner created with default manager ID
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            owner.assignToManager(null);

            // When & Then
            assertTrue(owner.isUnassigned());
            assertFalse(owner.isManagedBy(1L));
        }
    }

    @Nested
    @DisplayName("Authorization and Validation")
    class AuthorizationAndValidation {

        @Test
        @DisplayName("Should validate owner owns aquarium successfully")
        void shouldValidateOwnerOwnsAquariumSuccessfully() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            Long aquariumId = 1L;
            owner.registerAquarium(aquariumId);

            // When & Then
            assertDoesNotThrow(() -> owner.validateOwnsAquarium(aquariumId));
        }

        @Test
        @DisplayName("Should throw exception when owner doesn't own aquarium")
        void shouldThrowExceptionWhenOwnerDoesntOwnAquarium() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            Long aquariumId = 1L;

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class, 
                () -> owner.validateOwnsAquarium(aquariumId)
            );
            assertEquals("Access denied: You do not own this aquarium", exception.getMessage());
        }

        @Test
        @DisplayName("Should allow admin to validate any aquarium")
        void shouldAllowAdminToValidateAnyAquarium() {
            // Given
            Owner admin = Owner.create("Admin", "User", "admin@example.com", "ValidPassword123!");
            admin.promoteToAdmin();
            Long aquariumId = 1L;

            // When & Then
            assertDoesNotThrow(() -> admin.validateOwnsAquarium(aquariumId));
        }

        @Test
        @DisplayName("Should validate can modify own entity")
        void shouldValidateCanModifyOwnEntity() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            // Simulate owner having ID 1
            Owner reconstructedOwner = Owner.reconstruct(1L, owner.getFirstName(), owner.getLastName(), 
                owner.getEmail(), owner.getPassword(), owner.getRole(), owner.getLastLogin(), 
                owner.getDateCreated(), owner.getAquariumManagerId(), owner.getAquariumIds());

            // When & Then
            assertDoesNotThrow(() -> reconstructedOwner.validateCanModifyEntity(1L));
        }

        @Test
        @DisplayName("Should throw exception when trying to modify other's entity")
        void shouldThrowExceptionWhenTryingToModifyOthersEntity() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            Owner reconstructedOwner = Owner.reconstruct(1L, owner.getFirstName(), owner.getLastName(), 
                owner.getEmail(), owner.getPassword(), owner.getRole(), owner.getLastLogin(), 
                owner.getDateCreated(), owner.getAquariumManagerId(), owner.getAquariumIds());

            // When & Then
            ApplicationException.BusinessRuleException exception = assertThrows(
                ApplicationException.BusinessRuleException.class, 
                () -> reconstructedOwner.validateCanModifyEntity(2L)
            );
            assertEquals("Access denied: You can only modify your own entities", exception.getMessage());
        }

        @Test
        @DisplayName("Should allow admin to modify any entity")
        void shouldAllowAdminToModifyAnyEntity() {
            // Given
            Owner admin = Owner.create("Admin", "User", "admin@example.com", "ValidPassword123!");
            admin.promoteToAdmin();
            Owner reconstructedAdmin = Owner.reconstruct(1L, admin.getFirstName(), admin.getLastName(), 
                admin.getEmail(), admin.getPassword(), admin.getRole(), admin.getLastLogin(), 
                admin.getDateCreated(), admin.getAquariumManagerId(), admin.getAquariumIds());

            // When & Then
            assertDoesNotThrow(() -> reconstructedAdmin.validateCanModifyEntity(2L));
        }
    }

    @Nested
    @DisplayName("Login Tracking")
    class LoginTracking {

        @Test
        @DisplayName("Should record login time")
        void shouldRecordLoginTime() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");
            LocalDateTime beforeLogin = LocalDateTime.now().minusSeconds(1);

            // When
            owner.recordLogin();

            // Then
            LocalDateTime afterLogin = LocalDateTime.now().plusSeconds(1);
            assertNotNull(owner.getLastLogin());
            assertTrue(owner.getLastLogin().isAfter(beforeLogin));
            assertTrue(owner.getLastLogin().isBefore(afterLogin));
        }
    }

    @Nested
    @DisplayName("Owner Reconstruction")
    class OwnerReconstruction {

        @Test
        @DisplayName("Should reconstruct owner with all data")
        void shouldReconstructOwnerWithAllData() {
            // Given
            Long id = 1L;
            String firstName = "John";
            String lastName = "Doe";
            String email = "john@example.com";
            String password = "hashedPassword";
            Role role = Role.ADMIN;
            LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);
            LocalDateTime dateCreated = LocalDateTime.now().minusWeeks(1);
            Long managerId = 2L;
            Set<Long> aquariumIds = Set.of(3L, 4L);

            // When
            Owner owner = Owner.reconstruct(id, firstName, lastName, email, password, 
                role, lastLogin, dateCreated, managerId, aquariumIds);

            // Then
            assertEquals(id, owner.getId());
            assertEquals(firstName, owner.getFirstName());
            assertEquals(lastName, owner.getLastName());
            assertEquals(email, owner.getEmail());
            assertEquals(password, owner.getPassword());
            assertEquals(role, owner.getRole());
            assertEquals(lastLogin, owner.getLastLogin());
            assertEquals(dateCreated, owner.getDateCreated());
            assertEquals(managerId, owner.getAquariumManagerId());
            assertEquals(aquariumIds, owner.getAquariumIds());
        }

        @Test
        @DisplayName("Should reconstruct owner with null aquarium IDs")
        void shouldReconstructOwnerWithNullAquariumIds() {
            // Given
            Long id = 1L;
            String firstName = "John";
            String lastName = "Doe";
            String email = "john@example.com";
            String password = "hashedPassword";
            Role role = Role.OWNER;
            LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);
            LocalDateTime dateCreated = LocalDateTime.now().minusWeeks(1);
            Long managerId = null;

            // When
            Owner owner = Owner.reconstruct(id, firstName, lastName, email, password, 
                role, lastLogin, dateCreated, managerId, null);

            // Then
            assertNotNull(owner.getAquariumIds());
            assertTrue(owner.getAquariumIds().isEmpty());
        }
    }

    @Nested
    @DisplayName("Owner Equality and HashCode")
    class OwnerEqualityAndHashCode {

        @Test
        @DisplayName("Should be equal when IDs are equal")
        void shouldBeEqualWhenIdsAreEqual() {
            // Given
            Owner owner1 = Owner.reconstruct(1L, "John", "Doe", "john@example.com", 
                "password", Role.OWNER, null, LocalDateTime.now(), null, new HashSet<>());
            Owner owner2 = Owner.reconstruct(1L, "Jane", "Smith", "jane@example.com", 
                "differentPassword", Role.ADMIN, LocalDateTime.now(), LocalDateTime.now(), 2L, Set.of(3L));

            // When & Then
            assertEquals(owner1, owner2);
            assertEquals(owner1.hashCode(), owner2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when IDs are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            // Given
            Owner owner1 = Owner.reconstruct(1L, "John", "Doe", "john@example.com", 
                "password", Role.OWNER, null, LocalDateTime.now(), null, new HashSet<>());
            Owner owner2 = Owner.reconstruct(2L, "John", "Doe", "john@example.com", 
                "password", Role.OWNER, null, LocalDateTime.now(), null, new HashSet<>());

            // When & Then
            assertNotEquals(owner1, owner2);
        }

        @Test
        @DisplayName("Should not include password in toString")
        void shouldNotIncludePasswordInToString() {
            // Given
            Owner owner = Owner.create("John", "Doe", "john@example.com", "ValidPassword123!");

            // When
            String toString = owner.toString();

            // Then
            assertFalse(toString.contains("ValidPassword123!"));
            assertFalse(toString.contains("password"));
        }
    }
}
