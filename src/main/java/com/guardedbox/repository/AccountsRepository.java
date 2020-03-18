package com.guardedbox.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.AccountPublicKeysProjection;
import com.guardedbox.entity.projection.AccountSaltProjection;

/**
 * Repository: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountsRepository
        extends JpaRepository<AccountEntity, UUID>,
        JpaSpecificationExecutor<AccountEntity> {

    /**
     * @param accountId AccountEntity.accountId.
     * @return The AccountBaseProjection corresponding to the introduced accountId.
     */
    AccountBaseProjection findBaseByAccountId(
            UUID accountId);

    /**
     * @param accountId AccountEntity.accountId.
     * @return The AccountPublicKeysProjection corresponding to the introduced accountId.
     */
    AccountPublicKeysProjection findPublicKeysByAccountId(
            UUID accountId);

    /**
     * @param email AccountEntity.email.
     * @return Boolean indicating if an AccountFullEntity corresponding to the introduced email exists.
     */
    boolean existsByEmail(
            String email);

    /**
     * @param salt AccountEntity.salt.
     * @return Boolean indicating if an AccountFullEntity corresponding to the introduced salt exists.
     */
    boolean existsBySalt(
            String salt);

    /**
     * @param email AccountEntity.email.
     * @return The AccountEntity corresponding to the introduced email.
     */
    AccountEntity findByEmail(
            String email);

    /**
     * @param email AccountEntity.email.
     * @return The AccountBaseProjection corresponding to the introduced email.
     */
    AccountBaseProjection findBaseByEmail(
            String email);

    /**
     * @param email AccountEntity.email.
     * @return The AccountSaltProjection corresponding to the introduced email.
     */
    AccountSaltProjection findSaltByEmail(
            String email);

    /**
     * @param email AccountEntity.email.
     * @return The AccountPublicKeysProjection corresponding to the introduced email.
     */
    AccountPublicKeysProjection findPublicKeysByEmail(
            String email);

}
