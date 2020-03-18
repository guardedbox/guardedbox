package com.guardedbox.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.SharedSecretEntity;

/**
 * Repository: SharedSecret.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface SharedSecretsRepository
        extends JpaRepository<SharedSecretEntity, UUID>,
        JpaSpecificationExecutor<SharedSecretEntity> {

    /**
     * @param secretId Secret.secretId.
     * @param accountId Account.accountId.
     * @return The SharedSecretEntity corresponding to the introduced secretId and receiver accountId.
     */
    SharedSecretEntity findBySecretSecretIdAndReceiverAccountAccountId(
            UUID secretId,
            UUID accountId);

    /**
     * @param accountId Account.accountId.
     * @return The List of SharedSecretEntity corresponding to the introduced receiver accountId, ordered by owner Account.email and Secret.name.
     */
    List<SharedSecretEntity> findByReceiverAccountAccountIdOrderBySecretOwnerAccountEmailAscSecretNameAsc(
            UUID accountId);

    /**
     * @param accountId Account.accountId.
     * @return The List of SharedSecretEntity corresponding to Secrets corresponding to the introduced owner accountId, ordered by owner Account.email
     *         and Secret.name.
     */
    List<SharedSecretEntity> findBySecretOwnerAccountAccountIdOrderBySecretOwnerAccountEmailAscSecretNameAsc(
            UUID accountId);

}
