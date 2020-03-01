package com.guardedbox.mapper;

import java.util.LinkedList;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.AccountWithEncryptionPublicKeyDto;
import com.guardedbox.dto.AccountWithSaltDto;
import com.guardedbox.dto.AccountWithSecretsDto;
import com.guardedbox.dto.AccountWithSigningPublicKeyDto;
import com.guardedbox.dto.CreateAccountDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.AccountFullEntity;
import com.guardedbox.entity.AccountWithEncryptionPublicKeyEntity;
import com.guardedbox.entity.AccountWithSaltEntity;
import com.guardedbox.entity.AccountWithSigningPublicKeyEntity;

/**
 * Mapper: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class AccountsMapper {

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountDto toDto(
            AccountEntity accountEntity) {

        return accountEntity == null ? null : new AccountDto()
                .setAccountId(accountEntity.getAccountId())
                .setEmail(accountEntity.getEmail());

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountDto toDto(
            AccountFullEntity accountEntity) {

        return accountEntity == null ? null : new AccountDto()
                .setAccountId(accountEntity.getAccountId())
                .setEmail(accountEntity.getEmail());

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithSaltDto toDtoWithSalt(
            AccountWithSaltEntity accountEntity) {

        return accountEntity == null ? null : new AccountWithSaltDto()
                .setAccountId(accountEntity.getAccountId())
                .setEmail(accountEntity.getEmail())
                .setSalt(accountEntity.getSalt());

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithEncryptionPublicKeyDto toDtoWithEncryptionPublicKey(
            AccountWithEncryptionPublicKeyEntity accountEntity) {

        return accountEntity == null ? null : new AccountWithEncryptionPublicKeyDto()
                .setAccountId(accountEntity.getAccountId())
                .setEmail(accountEntity.getEmail())
                .setEncryptionPublicKey(accountEntity.getEncryptionPublicKey());

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithSigningPublicKeyDto toDtoWithSigningPublicKey(
            AccountWithSigningPublicKeyEntity accountEntity) {

        return accountEntity == null ? null : new AccountWithSigningPublicKeyDto()
                .setAccountId(accountEntity.getAccountId())
                .setEmail(accountEntity.getEmail())
                .setSigningPublicKey(accountEntity.getSigningPublicKey());

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithSecretsDto toDtoWithSecrets(
            AccountWithEncryptionPublicKeyEntity accountEntity) {

        return accountEntity == null ? null : new AccountWithSecretsDto()
                .setAccountId(accountEntity.getAccountId())
                .setEmail(accountEntity.getEmail())
                .setEncryptionPublicKey(accountEntity.getEncryptionPublicKey())
                .setSecrets(new LinkedList<>());

    }

    /**
     * Maps an Account DTO to Entity.
     *
     * @param accountDto The Account DTO.
     * @return The Account Entity.
     */
    public AccountFullEntity fromDto(
            CreateAccountDto accountDto) {

        return accountDto == null ? null : new AccountFullEntity()
                .setEmail(accountDto.getEmail())
                .setSalt(accountDto.getSalt())
                .setEncryptionPublicKey(accountDto.getEncryptionPublicKey())
                .setSigningPublicKey(accountDto.getSigningPublicKey());

    }

}
