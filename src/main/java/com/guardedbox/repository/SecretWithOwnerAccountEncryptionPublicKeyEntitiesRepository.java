package com.guardedbox.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.SecretWithOwnerAccountEncryptionPublicKeyEntity;

/**
 * Repository: Secret.
 * Entity: SecretWithOwnerAccountEncryptionPublicKeyEntity.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface SecretWithOwnerAccountEncryptionPublicKeyEntitiesRepository
        extends JpaRepository<SecretWithOwnerAccountEncryptionPublicKeyEntity, UUID>,
        JpaSpecificationExecutor<SecretWithOwnerAccountEncryptionPublicKeyEntity> {

}
