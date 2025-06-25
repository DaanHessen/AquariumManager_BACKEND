// package nl.hu.bep.application.service;

// import nl.hu.bep.config.AquariumConstants;
// import nl.hu.bep.domain.utils.Validator;
// import org.mindrot.jbcrypt.BCrypt;
// import jakarta.enterprise.context.ApplicationScoped;

// @ApplicationScoped
// public class SecurityService {
    
//     public String hashPassword(String plainPassword) {
//         String validatedPassword = Validator.validatePassword(plainPassword);
//         return BCrypt.hashpw(validatedPassword, BCrypt.gensalt(AquariumConstants.BCRYPT_ROUNDS));
//     }
    
//     /**
//      * Verifies a password against a hash.
//      */
//     public boolean verifyPassword(String plainPassword, String hashedPassword) {
//         if (plainPassword == null || hashedPassword == null) {
//             return false;
//         }
//         try {
//             return BCrypt.checkpw(plainPassword, hashedPassword);
//         } catch (IllegaalArgumentException e) {
//             // Invalid hash format
//             return false;
//         }
//     }
    
//     /**
//      * Validates password strength according to business rules.
//      */
//     public void validatePasswordStrength(String password) {
//         Validator.validatePassword(password);
        
//         if (password == null || password.length() < 8) {
//             throw new IllegalArgumentException("Password must be at least 8 characters long");
//         }
        
//         boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
//         boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
//         boolean hasDigit = password.chars().anyMatch(Character::isDigit);
//         boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
//         if (!hasUppercase) {
//             throw new IllegalArgumentException("Password must contain at least one uppercase letter");
//         }
//         if (!hasLowercase) {
//             throw new IllegalArgumentException("Password must contain at least one lowercase letter");
//         }
//         if (!hasDigit) {
//             throw new IllegalArgumentException("Password must contain at least one digit");
//         }
//         if (!hasSpecialChar) {
//             throw new IllegalArgumentException("Password must contain at least one special character");
//         }
        
//         // Check for common passwords
//         if (isCommonPassword(password)) {
//             throw new IllegalArgumentException("Password is too common, please choose a more secure password");
//         }
//     }
    
//     /**
//      * Checks if the password is in a list of common passwords.
//      */
//     private boolean isCommonPassword(String password) {
//         String[] commonPasswords = {
//             "password", "123456", "password123", "admin", "qwerty", 
//             "letmein", "welcome", "monkey", "1234567890", "password1"
//         };
//         String lowerPassword = password.toLowerCase();
//         for (String common : commonPasswords) {
//             if (lowerPassword.equals(common)) {
//                 return true;
//             }
//         }
//         return false;
//     }
// }
