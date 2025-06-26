package nl.hu.bep.domain;

import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.exception.ApplicationException;
import nl.hu.bep.domain.enums.Role;
import nl.hu.bep.config.AquariumConstants;

import lombok.*;
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

    public static Owner create(String firstName, String lastName, String email, String password) {
        Validator.notEmpty(firstName, "First name");
        Validator.notEmpty(lastName, "Last name");
        Validator.email(email);
        Validator.validatePassword(password);
        
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(AquariumConstants.BCRYPT_ROUNDS));
        
        return createWithHashedPassword(firstName, lastName, email, hashedPassword);
    }
    
    public static Owner createWithHashedPassword(String firstName, String lastName, String email, String hashedPassword) {
        return Owner.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(hashedPassword)
                .role(Role.OWNER)
                .dateCreated(LocalDateTime.now())
                .aquariumIds(new HashSet<>())
                .aquariumManagerId(AquariumConstants.DEFAULT_AQUARIUM_MANAGER_ID)
                .build();
    }

    public void changePassword(String currentPassword, String newPassword) {
        if (!verifyPassword(currentPassword)) {
            throw new ApplicationException.BusinessRuleException("Current password is incorrect");
        }
        updatePassword(newPassword);
    }

    public void updatePassword(String newPassword) {
        this.password = BCrypt.hashpw(Validator.notEmpty(newPassword, "Password"), BCrypt.gensalt(AquariumConstants.BCRYPT_ROUNDS));
    }

    public void changeEmail(String newEmail) {
        String validatedEmail = Validator.email(newEmail);
        if (validatedEmail.equals(this.email)) {
            throw new ApplicationException.BusinessRuleException("New email is the same as current email");
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
            throw new ApplicationException.BusinessRuleException("Owner is already an admin");
        }
        this.role = Role.ADMIN;
    }

    public void demoteToOwner() {
        if (this.role == Role.OWNER) {
            throw new ApplicationException.BusinessRuleException("Owner is already a regular owner");
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
            throw new ApplicationException.BusinessRuleException("Aquarium is already registered to this owner");
        }
        this.aquariumIds.add(aquariumId);
    }

    public void unregisterAquarium(Long aquariumId) {
        if (!this.aquariumIds.remove(aquariumId)) {
            throw new ApplicationException.BusinessRuleException("Aquarium is not registered to this owner");
        }
    }

    public void assignToManager(Long managerId) {
        this.aquariumManagerId = managerId;
    }

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

    public void addToAquariums(Aquarium aquarium) {
        if (aquarium != null && aquarium.getId() != null) {
            this.aquariumIds.add(aquarium.getId());
        }
    }

    public void validateOwnsAquarium(Long aquariumId) {
        if (aquariumId == null) {
            throw new ApplicationException.BusinessRuleException("Aquarium ID is required for ownership validation");
        }
        
        if (isAdmin()) {
            return;
        }
        
        if (!aquariumIds.contains(aquariumId)) {
            throw new ApplicationException.BusinessRuleException("Access denied: You do not own this aquarium");
        }
    }

    public void validateCanModifyEntity(Long entityOwnerId) {
        if (entityOwnerId == null) {
            throw new ApplicationException.BusinessRuleException("Entity owner ID is required for validation");
        }
        
        if (isAdmin()) {
            return;
        }
        
        if (!this.id.equals(entityOwnerId)) {
            throw new ApplicationException.BusinessRuleException("Access denied: You can only modify your own entities");
        }
    }

    public void validateCanAssignToAquarium(Long aquariumId, Long entityOwnerId) {
        validateOwnsAquarium(aquariumId);
        validateCanModifyEntity(entityOwnerId);
    }

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
