package com.guardedbox.mapper;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.CreateAccountDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.AccountLoginPublicKeyProjection;
import com.guardedbox.entity.projection.AccountLoginSaltProjection;
import com.guardedbox.entity.projection.AccountPublicKeysProjection;
import com.guardedbox.entity.projection.AccountPublicKeysSaltsProjection;

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
                .setEmail(accountEntity.getEmail())
                .setLoginSalt(accountEntity.getLoginSalt())
                .setLoginPublicKey(accountEntity.getLoginPublicKey())
                .setEncryptionSalt(accountEntity.getEncryptionSalt())
                .setEncryptionPublicKey(accountEntity.getEncryptionPublicKey())
                .setSigningSalt(accountEntity.getSigningSalt())
                .setSigningPublicKey(accountEntity.getSigningPublicKey());

    }

    /**
     * Maps an Account Projection to DTO.
     *
     * @param accountProjection The Account Projection.
     * @return The Account DTO.
     */
    public AccountDto toDto(
            AccountBaseProjection accountProjection) {

        if (accountProjection == null)
            return null;

        AccountDto accountDto = new AccountDto()
                .setAccountId(accountProjection.getAccountId())
                .setEmail(accountProjection.getEmail());

        if (accountProjection instanceof AccountLoginSaltProjection) {

            AccountLoginSaltProjection accountSubProjection = (AccountLoginSaltProjection) accountProjection;
            return accountDto
                    .setLoginSalt(accountSubProjection.getLoginSalt());

        } else if (accountProjection instanceof AccountLoginPublicKeyProjection) {

            AccountLoginPublicKeyProjection accountSubProjection = (AccountLoginPublicKeyProjection) accountProjection;
            return accountDto
                    .setLoginPublicKey(accountSubProjection.getLoginPublicKey());

        } else if (accountProjection instanceof AccountPublicKeysSaltsProjection) {

            AccountPublicKeysSaltsProjection accountSubProjection = (AccountPublicKeysSaltsProjection) accountProjection;
            return accountDto
                    .setEncryptionSalt(accountSubProjection.getEncryptionSalt())
                    .setSigningSalt(accountSubProjection.getSigningSalt());

        } else if (accountProjection instanceof AccountPublicKeysProjection) {

            AccountPublicKeysProjection accountSubProjection = (AccountPublicKeysProjection) accountProjection;
            return accountDto
                    .setEncryptionPublicKey(accountSubProjection.getEncryptionPublicKey())
                    .setSigningPublicKey(accountSubProjection.getSigningPublicKey());

        } else if (accountProjection instanceof AccountBaseProjection) {

            return accountDto;

        } else {

            throw new IllegalArgumentException("Type must extend AccountBaseProjection");

        }

    }

    /**
     * Maps an Account DTO to Entity.
     *
     * @param accountDto The Account DTO.
     * @return The Account Entity.
     */
    public AccountEntity fromDto(
            CreateAccountDto accountDto) {

        return accountDto == null ? null : new AccountEntity()
                .setEmail(accountDto.getEmail())
                .setLoginSalt(accountDto.getLoginSalt())
                .setLoginPublicKey(accountDto.getLoginPublicKey())
                .setEncryptionSalt(accountDto.getEncryptionSalt())
                .setEncryptionPublicKey(accountDto.getEncryptionPublicKey())
                .setSigningSalt(accountDto.getSigningSalt())
                .setSigningPublicKey(accountDto.getSigningPublicKey());

    }

}
