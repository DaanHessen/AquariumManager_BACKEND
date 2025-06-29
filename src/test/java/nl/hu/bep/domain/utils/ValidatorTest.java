package nl.hu.bep.domain.utils;

import nl.hu.bep.config.AquariumConstants;
import nl.hu.bep.exception.ApplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validator Utility Unit Tests")
class ValidatorTest {

    @Nested
    @DisplayName("String Validation - notEmpty")
    class StringValidationNotEmpty {

        @Test
        @DisplayName("Should return valid non-empty string")
        void shouldReturnValidNonEmptyString() {
            // Given
            String validString = "Valid String";
            String fieldName = "Test Field";

            // When
            String result = Validator.notEmpty(validString, fieldName);

            // Then
            assertEquals(validString, result);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n", " \t \n "})
        @DisplayName("Should throw exception for null, empty, or whitespace-only strings")
        void shouldThrowExceptionForInvalidStrings(String invalidString) {
            // Given
            String fieldName = "Test Field";

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.notEmpty(invalidString, fieldName)
            );
            
            assertEquals("Test Field cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should trim and accept string with surrounding whitespace")
        void shouldAcceptStringWithContent() {
            // Given
            String stringWithWhitespace = "  Valid Content  ";
            String fieldName = "Test Field";

            // When
            String result = Validator.notEmpty(stringWithWhitespace, fieldName);

            // Then
            assertEquals(stringWithWhitespace, result);
        }
    }

    @Nested
    @DisplayName("String Validation - validateLength")
    class StringValidationLength {

        @Test
        @DisplayName("Should return string within valid length range")
        void shouldReturnStringWithinValidLengthRange() {
            // Given
            String validString = "ValidLength";
            String fieldName = "Test Field";
            int minLength = 5;
            int maxLength = 15;

            // When
            String result = Validator.validateLength(validString, fieldName, minLength, maxLength);

            // Then
            assertEquals(validString, result);
        }

        @Test
        @DisplayName("Should accept string at minimum length boundary")
        void shouldAcceptStringAtMinimumLengthBoundary() {
            // Given
            String minLengthString = "12345"; // exactly 5 characters
            String fieldName = "Test Field";
            int minLength = 5;
            int maxLength = 10;

            // When
            String result = Validator.validateLength(minLengthString, fieldName, minLength, maxLength);

            // Then
            assertEquals(minLengthString, result);
        }

        @Test
        @DisplayName("Should accept string at maximum length boundary")
        void shouldAcceptStringAtMaximumLengthBoundary() {
            // Given
            String maxLengthString = "1234567890"; // exactly 10 characters
            String fieldName = "Test Field";
            int minLength = 5;
            int maxLength = 10;

            // When
            String result = Validator.validateLength(maxLengthString, fieldName, minLength, maxLength);

            // Then
            assertEquals(maxLengthString, result);
        }

        @Test
        @DisplayName("Should throw exception for string shorter than minimum length")
        void shouldThrowExceptionForStringShorterThanMinimumLength() {
            // Given
            String shortString = "1234"; // 4 characters
            String fieldName = "Test Field";
            int minLength = 5;
            int maxLength = 10;

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.validateLength(shortString, fieldName, minLength, maxLength)
            );
            
            assertEquals("Test Field must be between 5 and 10 characters", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for string longer than maximum length")
        void shouldThrowExceptionForStringLongerThanMaximumLength() {
            // Given
            String longString = "12345678901"; // 11 characters
            String fieldName = "Test Field";
            int minLength = 5;
            int maxLength = 10;

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.validateLength(longString, fieldName, minLength, maxLength)
            );
            
            assertEquals("Test Field must be between 5 and 10 characters", exception.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should throw exception for null or empty string in length validation")
        void shouldThrowExceptionForNullOrEmptyStringInLengthValidation(String invalidString) {
            // Given
            String fieldName = "Test Field";

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.validateLength(invalidString, fieldName, 5, 10)
            );
            
            assertEquals("Test Field cannot be empty", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Double Validation - positive")
    class DoubleValidationPositive {

        @ParameterizedTest
        @ValueSource(doubles = {0.1, 1.0, 100.5, 1000000.0})
        @DisplayName("Should return positive double values")
        void shouldReturnPositiveDoubleValues(double positiveValue) {
            // Given
            String fieldName = "Test Field";

            // When
            double result = Validator.positive(positiveValue, fieldName);

            // Then
            assertEquals(positiveValue, result);
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, -0.1, -1.0, -100.5})
        @DisplayName("Should throw exception for non-positive double values")
        void shouldThrowExceptionForNonPositiveDoubleValues(double nonPositiveValue) {
            // Given
            String fieldName = "Test Field";

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.positive(nonPositiveValue, fieldName)
            );
            
            assertEquals("Test Field must be positive", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Double Validation - validateRange")
    class DoubleValidationRange {

        @Test
        @DisplayName("Should return value within valid range")
        void shouldReturnValueWithinValidRange() {
            // Given
            double validValue = 50.0;
            String fieldName = "Test Field";
            double min = 0.0;
            double max = 100.0;

            // When
            double result = Validator.validateRange(validValue, fieldName, min, max);

            // Then
            assertEquals(validValue, result);
        }

        @Test
        @DisplayName("Should accept value at minimum boundary")
        void shouldAcceptValueAtMinimumBoundary() {
            // Given
            double minValue = 10.0;
            String fieldName = "Test Field";
            double min = 10.0;
            double max = 100.0;

            // When
            double result = Validator.validateRange(minValue, fieldName, min, max);

            // Then
            assertEquals(minValue, result);
        }

        @Test
        @DisplayName("Should accept value at maximum boundary")
        void shouldAcceptValueAtMaximumBoundary() {
            // Given
            double maxValue = 100.0;
            String fieldName = "Test Field";
            double min = 10.0;
            double max = 100.0;

            // When
            double result = Validator.validateRange(maxValue, fieldName, min, max);

            // Then
            assertEquals(maxValue, result);
        }

        @Test
        @DisplayName("Should throw exception for value below minimum")
        void shouldThrowExceptionForValueBelowMinimum() {
            // Given
            double belowMinValue = 5.0;
            String fieldName = "Test Field";
            double min = 10.0;
            double max = 100.0;

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.validateRange(belowMinValue, fieldName, min, max)
            );
            
            assertEquals("Test Field must be between 10.00 and 100.00", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for value above maximum")
        void shouldThrowExceptionForValueAboveMaximum() {
            // Given
            double aboveMaxValue = 150.0;
            String fieldName = "Test Field";
            double min = 10.0;
            double max = 100.0;

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.validateRange(aboveMaxValue, fieldName, min, max)
            );
            
            assertEquals("Test Field must be between 10.00 and 100.00", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Integer Validation - positive")
    class IntegerValidationPositive {

        @ParameterizedTest
        @ValueSource(ints = {1, 10, 100, 1000000})
        @DisplayName("Should return positive integer values")
        void shouldReturnPositiveIntegerValues(int positiveValue) {
            // Given
            String fieldName = "Test Field";

            // When
            int result = Validator.positive(positiveValue, fieldName);

            // Then
            assertEquals(positiveValue, result);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -10, -100})
        @DisplayName("Should throw exception for non-positive integer values")
        void shouldThrowExceptionForNonPositiveIntegerValues(int nonPositiveValue) {
            // Given
            String fieldName = "Test Field";

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.positive(nonPositiveValue, fieldName)
            );
            
            assertEquals("Test Field must be positive", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Null Validation - notNull")
    class NullValidation {

        @Test
        @DisplayName("Should return non-null object")
        void shouldReturnNonNullObject() {
            // Given
            String nonNullValue = "Not null";
            String fieldName = "Test Field";

            // When
            String result = Validator.notNull(nonNullValue, fieldName);

            // Then
            assertEquals(nonNullValue, result);
        }

        @Test
        @DisplayName("Should throw exception for null object")
        void shouldThrowExceptionForNullObject() {
            // Given
            String nullValue = null;
            String fieldName = "Test Field";

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.notNull(nullValue, fieldName)
            );
            
            assertEquals("Test Field cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should work with any object type")
        void shouldWorkWithAnyObjectType() {
            // Given
            Integer integerValue = 42;
            String fieldName = "Integer Field";

            // When
            Integer result = Validator.notNull(integerValue, fieldName);

            // Then
            assertEquals(integerValue, result);
        }
    }

    @Nested
    @DisplayName("Email Validation")
    class EmailValidation {

        @ParameterizedTest
        @ValueSource(strings = {
            "test@example.com",
            "user.name@domain.com",
            "user+tag@example.org",
            "user_name@example-domain.com",
            "123@numeric-domain.net",
            "a@b.co"
        })
        @DisplayName("Should accept valid email formats")
        void shouldAcceptValidEmailFormats(String validEmail) {
            // When
            String result = Validator.email(validEmail);

            // Then
            assertEquals(validEmail, result);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalid-email",
            "@example.com",
            "user@",
            "user..name@example.com",
            "user@.com",
            "user@com",
            "user name@example.com",
            "user@example.",
            ""
        })
        @DisplayName("Should throw exception for invalid email formats")
        void shouldThrowExceptionForInvalidEmailFormats(String invalidEmail) {
            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.email(invalidEmail)
            );
            
            assertTrue(exception.getMessage().contains("Invalid email format") || 
                      exception.getMessage().contains("Email cannot be empty"));
        }

        @Test
        @DisplayName("Should throw exception for null email")
        void shouldThrowExceptionForNullEmail() {
            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.email(null)
            );
            
            assertEquals("Email cannot be empty", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Password Validation")
    class PasswordValidation {

        @Test
        @DisplayName("Should accept valid password meeting minimum length")
        void shouldAcceptValidPasswordMeetingMinimumLength() {
            // Given
            String validPassword = "a".repeat(AquariumConstants.MIN_PASSWORD_LENGTH);

            // When
            String result = Validator.validatePassword(validPassword);

            // Then
            assertEquals(validPassword, result);
        }

        @Test
        @DisplayName("Should accept password longer than minimum length")
        void shouldAcceptPasswordLongerThanMinimumLength() {
            // Given
            String longPassword = "a".repeat(AquariumConstants.MIN_PASSWORD_LENGTH + 10);

            // When
            String result = Validator.validatePassword(longPassword);

            // Then
            assertEquals(longPassword, result);
        }

        @Test
        @DisplayName("Should throw exception for password shorter than minimum length")
        void shouldThrowExceptionForPasswordShorterThanMinimumLength() {
            // Given
            String shortPassword = "a".repeat(AquariumConstants.MIN_PASSWORD_LENGTH - 1);

            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.validatePassword(shortPassword)
            );
            
            assertEquals("Password must be at least " + AquariumConstants.MIN_PASSWORD_LENGTH + " characters long", 
                        exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for null password")
        void shouldThrowExceptionForNullPassword() {
            // When & Then
            ApplicationException.ValidationException exception = assertThrows(
                ApplicationException.ValidationException.class,
                () -> Validator.validatePassword(null)
            );
            
            assertEquals("Password must be at least " + AquariumConstants.MIN_PASSWORD_LENGTH + " characters long", 
                        exception.getMessage());
        }

        @Test
        @DisplayName("Should accept password with special characters")
        void shouldAcceptPasswordWithSpecialCharacters() {
            // Given
            String passwordWithSpecialChars = "P@ssw0rd123!";

            // When
            String result = Validator.validatePassword(passwordWithSpecialChars);

            // Then
            assertEquals(passwordWithSpecialChars, result);
        }
    }
}
