package nl.hu.bep.security.application.annotation;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RequiresOwnership {
    String paramName() default "id";
    
    ResourceType resourceType() default ResourceType.AQUARIUM;
    
    enum ResourceType {
        AQUARIUM,
        INHABITANT,
        ACCESSORY,
        ORNAMENT
    }
    
    @NameBinding
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Checker {}
} 