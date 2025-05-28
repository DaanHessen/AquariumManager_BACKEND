package nl.hu.bep.domain.utils;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.hu.bep.domain.exception.DomainException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Validator {

    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public static String notEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException.ValidationException(fieldName + " cannot be empty");
        }
        return value;
    }

    public static double positive(double value, String fieldName) {
        if (value <= 0) {
            throw new DomainException.ValidationException(fieldName + " must be positive");
        }
        return value;
    }

    public static int positive(int value, String fieldName) {
        if (value <= 0) {
            throw new DomainException.ValidationException(fieldName + " must be positive");
        }
        return value;
    }

    public static <T> T notNull(T value, String fieldName) {
        if (value == null) {
            throw new DomainException.ValidationException(fieldName + " cannot be null");
        }
        return value;
    }

    public static String email(String email) {
        notEmpty(email, "Email");

        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new DomainException.ValidationException("Invalid email format");
        }
        return email;
    }
}