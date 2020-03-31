package com.guardedbox.entity.projection;

import java.util.UUID;

import javax.persistence.Transient;

import com.guardedbox.entity.AccountEntity;
import com.guardedbox.repository.AccountsRepository;

/**
 * Projection of Entity: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface SecretBaseProjection {

    UUID getSecretId();

    AccountEntity getOwnerAccount();

    String getValue();

    /**
     * @param <T> A projection type.
     * @param type The class of the projection.
     * @return The ownerAccount corresponding to the introduced projection class.
     */
    @Transient
    default <T extends AccountBaseProjection> T getOwnerAccount(
            Class<T> type) {

        return AccountsRepository.getProjection(this.getOwnerAccount(), type);

    }

}
