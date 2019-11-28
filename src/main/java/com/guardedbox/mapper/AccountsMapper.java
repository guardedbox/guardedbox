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

        if (accountEntity == null)
            return null;

        AccountDto accountDto = new AccountDto();

        accountDto.setAccountId(accountEntity.getAccountId());
        accountDto.setEmail(accountEntity.getEmail());

        return accountDto;

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountDto toDto(
            AccountFullEntity accountEntity) {

        if (accountEntity == null)
            return null;

        AccountDto accountDto = new AccountDto();

        accountDto.setAccountId(accountEntity.getAccountId());
        accountDto.setEmail(accountEntity.getEmail());

        return accountDto;

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithSaltDto toDtoWithSalt(
            AccountWithSaltEntity accountEntity) {

        if (accountEntity == null)
            return null;

        AccountWithSaltDto accountDto = new AccountWithSaltDto();

        accountDto.setAccountId(accountEntity.getAccountId());
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setSalt(accountEntity.getSalt());

        return accountDto;

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithEncryptionPublicKeyDto toDtoWithEncryptionPublicKey(
            AccountWithEncryptionPublicKeyEntity accountEntity) {

        if (accountEntity == null)
            return null;

        AccountWithEncryptionPublicKeyDto accountDto = new AccountWithEncryptionPublicKeyDto();

        accountDto.setAccountId(accountEntity.getAccountId());
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setEncryptionPublicKey(accountEntity.getEncryptionPublicKey());

        return accountDto;

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithSigningPublicKeyDto toDtoWithSigningPublicKey(
            AccountWithSigningPublicKeyEntity accountEntity) {

        if (accountEntity == null)
            return null;

        AccountWithSigningPublicKeyDto accountDto = new AccountWithSigningPublicKeyDto();

        accountDto.setAccountId(accountEntity.getAccountId());
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setSigningPublicKey(accountEntity.getSigningPublicKey());

        return accountDto;

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountWithSecretsDto toDtoWithSecrets(
            AccountWithEncryptionPublicKeyEntity accountEntity) {

        if (accountEntity == null)
            return null;

        AccountWithSecretsDto accountDto = new AccountWithSecretsDto();

        accountDto.setAccountId(accountEntity.getAccountId());
        accountDto.setEmail(accountEntity.getEmail());
        accountDto.setEncryptionPublicKey(accountEntity.getEncryptionPublicKey());
        accountDto.setSecrets(new LinkedList<>());

        return accountDto;

    }

    /**
     * Maps an Account DTO to Entity.
     *
     * @param accountDto The Account DTO.
     * @return The Account Entity.
     */
    public AccountFullEntity fromDto(
            CreateAccountDto accountDto) {

        if (accountDto == null)
            return null;

        AccountFullEntity accountEntity = new AccountFullEntity();

        accountEntity.setEmail(accountDto.getEmail());
        accountEntity.setSalt(accountDto.getSalt());
        accountEntity.setEncryptionPublicKey(accountDto.getEncryptionPublicKey());
        accountEntity.setSigningPublicKey(accountDto.getSigningPublicKey());

        return accountEntity;

    }

}
