package nl.hu.bep.data.interfaces;

import nl.hu.bep.domain.Owner;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository extends Repository<Owner, Long> {
    
    Owner findByUsername(String username);
    List<Owner> findAllOwners();
    Optional<Owner> findByEmail(String email);
}
