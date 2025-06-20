package nl.hu.bep.domain;

import lombok.*;
import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.exception.domain.DomainException;
import nl.hu.bep.domain.enums.Role;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Getter
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"password"})
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Owner {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime lastLogin;
    private LocalDateTime dateCreated;
    
    @Builder.Default
    private Set<Long> aquariumIds = new HashSet<>();
    private Long aquariumManagerId;

    // Factory method for new owners
    public static Owner create(String firstName, String lastName, String email, String password) {
        Validator.notEmpty(firstName, "First name");
        Validator.notEmpty(lastName, "Last name");
        Validator.email(email);
        Validator.notEmpty(password, "Password");
        
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        return Owner.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(hashedPassword)
                .role(Role.OWNER)
                .dateCreated(LocalDateTime.now())
                .aquariumIds(new HashSet<>())
                .build();
    }

    // Business logic methods with proper validation
    public void changePassword(String currentPassword, String newPassword) {
        if (!verifyPassword(currentPassword)) {
            throw new DomainException("Current password is incorrect");
        }
        updatePassword(newPassword);
    }

    public void updatePassword(String newPassword) {
        this.password = BCrypt.hashpw(Validator.notEmpty(newPassword, "Password"), BCrypt.gensalt());
    }

    public void changeEmail(String newEmail) {
        String validatedEmail = Validator.email(newEmail);
        if (validatedEmail.equals(this.email)) {
            throw new DomainException("New email is the same as current email");
        }
        this.email = validatedEmail;
    }

    public void updateProfile(String firstName, String lastName) {
        if (firstName != null) {
            this.firstName = Validator.notEmpty(firstName, "First name");
        }
        if (lastName != null) {
            this.lastName = Validator.notEmpty(lastName, "Last name");  
        }
    }

    public void promoteToAdmin() {
        if (this.role == Role.ADMIN) {
            throw new DomainException("Owner is already an admin");
        }
        this.role = Role.ADMIN;
    }

    public void demoteToOwner() {
        if (this.role == Role.OWNER) {
            throw new DomainException("Owner is already a regular owner");
        }
        this.role = Role.OWNER;
    }

    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public boolean verifyPassword(String password) {
        return BCrypt.checkpw(password, this.password);
    }

    public void registerAquarium(Long aquariumId) {
        Validator.notNull(aquariumId, "Aquarium ID");
        if (this.aquariumIds.contains(aquariumId)) {
            throw new DomainException("Aquarium is already registered to this owner");
        }
        this.aquariumIds.add(aquariumId);
    }

    public void unregisterAquarium(Long aquariumId) {
        if (!this.aquariumIds.remove(aquariumId)) {
            throw new DomainException("Aquarium is not registered to this owner");
        }
    }

    // Query methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isOwner() {
        return Role.OWNER.equals(role);
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(role);  
    }

    public boolean hasAquariums() {
        return !aquariumIds.isEmpty();
    }

    public int getAquariumCount() {
        return aquariumIds.size();
    }

    public boolean isManagedBy(Long managerId) {
        return managerId != null && managerId.equals(this.aquariumManagerId);
    }

    public boolean isUnassigned() {
        return this.aquariumManagerId == null;
    }

    // Public method for repository reconstruction only
    public static Owner reconstruct(Long id, String firstName, String lastName, String email, 
                           String password, Role role, LocalDateTime lastLogin, 
                           LocalDateTime dateCreated, Long aquariumManagerId, Set<Long> aquariumIds) {
        return Owner.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(password)
                .role(role)
                .lastLogin(lastLogin)
                .dateCreated(dateCreated)
                .aquariumManagerId(aquariumManagerId)
                .aquariumIds(aquariumIds != null ? aquariumIds : new HashSet<>())
                .build();
    }
}
