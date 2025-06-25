// package nl.hu.bep.application.factory;

// import nl.hu.bep.domain.Owner;
// import nl.hu.bep.domain.utils.Validator;
// import nl.hu.bep.application.service.SecurityService;
// import jakarta.enterprise.context.ApplicationScoped;
// import jakarta.inject.Inject;

// // might delete

// @ApplicationScoped
// public class OwnerFactory {
    
//     @Inject
//     private SecurityService securityService;
    
//     public Owner createOwner(String firstName, String lastName, String email, String password) {
//         Validator.notEmpty(firstName, "First name");
//         Validator.notEmpty(lastName, "Last name");
//         Validator.email(email);
        
//         String hashedPassword = securityService.hashPassword(password);
        
//         return Owner.createWithHashedPassword(firstName, lastName, email, hashedPassword);
//     }
// }
