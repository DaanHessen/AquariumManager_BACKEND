package nl.hu.bep.domain.utils;

import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.config.AquariumConstants;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Validator {

    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public static String notEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ApplicationException.ValidationException(fieldName + " cannot be empty");
        }
        return value;
    }
    
    public static String validateLength(String value, String fieldName, int minLength, int maxLength) {
        notEmpty(value, fieldName);
        if (value.length() < minLength || value.length() > maxLength) {
            throw new ApplicationException.ValidationException(
                String.format("%s must be between %d and %d characters", fieldName, minLength, maxLength));
        }
        return value;
    }

    public static double positive(double value, String fieldName) {
        if (value <= 0) {
            throw new ApplicationException.ValidationException(fieldName + " must be positive");
        }
        return value;
    }
    
    public static double validateRange(double value, String fieldName, double min, double max) {
        if (value < min || value > max) {
            throw new ApplicationException.ValidationException(
                String.format("%s must be between %.2f and %.2f", fieldName, min, max));
        }
        return value;
    }

    public static int positive(int value, String fieldName) {
        if (value <= 0) {
            throw new ApplicationException.ValidationException(fieldName + " must be positive");
        }
        return value;
    }

    public static <T> T notNull(T value, String fieldName) {
        if (value == null) {
            throw new ApplicationException.ValidationException(fieldName + " cannot be null");
        }
        return value;
    }

    public static String email(String email) {
        notEmpty(email, "Email");

        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new ApplicationException.ValidationException("Invalid email format");
        }
        return email;
    }
    
    public static String validatePassword(String password) {
        if (password == null || password.length() < AquariumConstants.MIN_PASSWORD_LENGTH) {
            throw new ApplicationException.ValidationException(
                "Password must be at least " + AquariumConstants.MIN_PASSWORD_LENGTH + " characters long");
        }
        return password;
    }
}