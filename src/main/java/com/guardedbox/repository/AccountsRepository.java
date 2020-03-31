package com.guardedbox.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.config.SpringContext;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.AccountLoginPublicKeyProjection;
import com.guardedbox.entity.projection.AccountLoginSaltProjection;
import com.guardedbox.entity.projection.AccountPublicKeysProjection;
import com.guardedbox.entity.projection.AccountPublicKeysSaltsProjection;

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
     * @param email AccountEntity.email.
     * @return Boolean indicating if an AccountEntity corresponding to the introduced email exists.
     */
    boolean existsByEmail(
            String email);

    /**
     * @param loginSalts List of AccountEntity.loginSalt.
     * @param encryptionSalts List of AccountEntity.encryptionSalt.
     * @param signingSalts List of AccountEntity.signingSalt.
     * @return Boolean indicating if an AccountEntity corresponding to any of the introduced salts exists.
     */
    boolean existsByLoginSaltInOrEncryptionSaltInOrSigningSaltIn(
            List<String> loginSalts,
            List<String> encryptionSalts,
            List<String> signingSalts);

    /**
     * @param accountId AccountEntity.accountId.
     * @return The AccountBaseProjection corresponding to the introduced accountId.
     */
    AccountBaseProjection findBaseByAccountId(
            UUID accountId);

    /**
     * @param accountId AccountEntity.accountId.
     * @return The AccountLoginSaltProjection corresponding to the introduced accountId.
     */
    AccountLoginSaltProjection findLoginSaltByAccountId(
            UUID accountId);

    /**
     * @param accountId AccountEntity.accountId.
     * @return The AccountLoginPublicKeyProjection corresponding to the introduced accountId.
     */
    AccountLoginPublicKeyProjection findLoginPublicKeyByAccountId(
            UUID accountId);

    /**
     * @param accountId AccountEntity.accountId.
     * @return The AccountPublicKeysSaltsProjection corresponding to the introduced accountId.
     */
    AccountPublicKeysSaltsProjection findPublicKeysSaltsByAccountId(
            UUID accountId);

    /**
     * @param accountId AccountEntity.accountId.
     * @return The AccountPublicKeysProjection corresponding to the introduced accountId.
     */
    AccountPublicKeysProjection findPublicKeysByAccountId(
            UUID accountId);

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
    AccountLoginSaltProjection findLoginSaltByEmail(
            String email);

    /**
     * @param email AccountEntity.email.
     * @return The AccountPublicKeyProjection corresponding to the introduced email.
     */
    AccountLoginPublicKeyProjection findLoginPublicKeyByEmail(
            String email);

    /**
     * @param email AccountEntity.email.
     * @return The AccountPublicKeysSaltsProjection corresponding to the introduced email.
     */
    AccountPublicKeysSaltsProjection findPublicKeysSaltsByEmail(
            String email);

    /**
     * @param email AccountEntity.email.
     * @return The AccountPublicKeysProjection corresponding to the introduced email.
     */
    AccountPublicKeysProjection findPublicKeysByEmail(
            String email);

    /**
     * @param <T> A projection type.
     * @param entity An entity.
     * @param type The class of the projection.
     * @return The projection corresponding to the introduced entity and projection class.
     */
    static <T extends AccountBaseProjection> T getProjection(
            AccountEntity entity,
            Class<T> type) {

        if (entity == null)
            return null;

        AccountsRepository repository = SpringContext.getAccountsRepository();
        UUID id = entity.getAccountId();

        if (AccountLoginSaltProjection.class.equals(type)) {

            return type.cast(repository.findLoginSaltByAccountId(id));

        } else if (AccountLoginPublicKeyProjection.class.equals(type)) {

            return type.cast(repository.findLoginPublicKeyByAccountId(id));

        } else if (AccountPublicKeysSaltsProjection.class.equals(type)) {

            return type.cast(repository.findPublicKeysSaltsByAccountId(id));

        } else if (AccountPublicKeysProjection.class.equals(type)) {

            return type.cast(repository.findPublicKeysByAccountId(id));

        } else if (AccountBaseProjection.class.equals(type)) {

            return type.cast(repository.findBaseByAccountId(id));

        } else {

            throw new IllegalArgumentException("Type must extend AccountBaseProjection");

        }

    }

}
