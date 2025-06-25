package nl.hu.bep.domain.base;

import nl.hu.bep.domain.utils.Validator;
import nl.hu.bep.exception.domain.OwnershipException;

public abstract class OwnedEntity {
    protected Long ownerId;

    public Long getOwnerId() {
        return ownerId;
    }

    public void validateOwnership(Long requestingOwnerId) {
        Validator.notNull(requestingOwnerId, "Requesting Owner ID");
        if (this.ownerId == null || !this.ownerId.equals(requestingOwnerId)) {
            throw new OwnershipException("Entity does not belong to the current user.");
        }
    }
}
