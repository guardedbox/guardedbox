package com.guardedbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.SharedSecretEntity;

import java.util.List;

/**
 * Repository: SharedSecret.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public interface SharedSecretEntitiesRepository
        extends JpaRepository<SharedSecretEntity, Long>,
        JpaSpecificationExecutor<SharedSecretEntity> {

    /**
     * @param secretId Secret.secretId.
     * @param accountId Account.accountId.
     * @return The SharedSecretEntity corresponding to the introduced secretId and receiver accountId.
     */
    SharedSecretEntity findBySecretSecretIdAndReceiverAccountAccountId(
            Long secretId,
            Long accountId);

    /**
     * @param accountId Account.accountId.
     * @return The List of SharedSecretEntity corresponding to the introduced receiver accountId, ordered by owner Account.email and Secret.name.
     */
    List<SharedSecretEntity> findByReceiverAccountAccountIdOrderBySecretOwnerAccountEmailAscSecretNameAsc(
            Long accountId);

    /**
     * @param accountId Account.accountId.
     * @return The List of SharedSecretEntity corresponding to Secrets corresponding to the introduced owner accountId, ordered by owner Account.email
     *         and Secret.name.
     */
    List<SharedSecretEntity> findBySecretOwnerAccountAccountIdOrderBySecretOwnerAccountEmailAscSecretNameAsc(
            Long accountId);

}
