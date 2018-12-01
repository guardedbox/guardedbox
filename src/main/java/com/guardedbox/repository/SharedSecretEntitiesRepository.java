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
     * @return The SharedSecretEntity corresponding to the introduced secretId and accountId.
     */
    SharedSecretEntity findBySecretSecretIdAndAccountAccountId(
            Long secretId,
            Long accountId);

    /**
     * @param accountId Account.accountId.
     * @return The List of SharedSecretEntity corresponding to the introduced accountId, ordered by Account.email and Secret.name.
     */
    List<SharedSecretEntity> findByAccountAccountIdOrderByAccountEmailAscSecretNameAsc(
            Long accountId);

    /**
     * @param accountId Account.accountId.
     * @return The List of SharedSecretEntity corresponding to Secrets corresponding to the introduced accountId, ordered by Account.email and
     *         Secret.name.
     */
    List<SharedSecretEntity> findBySecretAccountAccountIdOrderByAccountEmailAscSecretNameAsc(
            Long accountId);

}
