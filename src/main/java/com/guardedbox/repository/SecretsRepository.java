package com.guardedbox.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.config.SpringContext;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.entity.projection.SecretBaseProjection;
import com.guardedbox.entity.projection.SecretMustRotateKeyProjection;
import com.guardedbox.entity.projection.SecretValueProjection;

/**
 * Repository: Secret.
 * Entity: SecretEntity.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface SecretsRepository
        extends JpaRepository<SecretEntity, UUID>,
        JpaSpecificationExecutor<SecretEntity> {

    /**
     * @param secretId SecretEntity.secretId.
     * @return The SecretBaseProjection corresponding to the introduced secretId.
     */
    SecretBaseProjection findBaseBySecretId(
            UUID secretId);

    /**
     * @param secretId SecretEntity.secretId.
     * @return The SecretValueProjection corresponding to the introduced secretId.
     */
    SecretValueProjection findValueBySecretId(
            UUID secretId);

    /**
     * @param secretId SecretEntity.secretId.
     * @return The SecretMustRotateKeyProjection corresponding to the introduced secretId.
     */
    SecretMustRotateKeyProjection findMustRotateKeyBySecretId(
            UUID secretId);

    /**
     * @param ownerAccountId Account.accountId.
     * @return The List of SecretEntities corresponding to the introduced owner accountId.
     */
    List<SecretEntity> findByOwnerAccountAccountId(
            UUID ownerAccountId);

    /**
     * @param <T> A projection type.
     * @param entity An entity.
     * @param type The class of the projection.
     * @return The projection corresponding to the introduced entity and projection class.
     */
    static <T extends SecretBaseProjection> T getProjection(
            SecretEntity entity,
            Class<T> type) {

        if (entity == null)
            return null;

        SecretsRepository repository = SpringContext.getSecretsRepository();
        UUID id = entity.getSecretId();

        if (SecretValueProjection.class.equals(type)) {

            return type.cast(repository.findValueBySecretId(id));

        } else if (SecretMustRotateKeyProjection.class.equals(type)) {

            return type.cast(repository.findMustRotateKeyBySecretId(id));

        } else if (SecretBaseProjection.class.equals(type)) {

            return type.cast(repository.findBaseBySecretId(id));

        } else {

            throw new IllegalArgumentException("Type must extend SecretBaseProjection");

        }

    }

}
