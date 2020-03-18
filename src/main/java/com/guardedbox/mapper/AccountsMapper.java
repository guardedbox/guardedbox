package com.guardedbox.mapper;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.CreateAccountDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.AccountPublicKeysProjection;
import com.guardedbox.entity.projection.AccountSaltProjection;

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
                .setSalt(accountEntity.getSalt())
                .setEncryptionPublicKey(accountEntity.getEncryptionPublicKey())
                .setSigningPublicKey(accountEntity.getSigningPublicKey());

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountDto toDto(
            AccountBaseProjection accountEntity) {

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
            AccountSaltProjection accountEntity) {

        return accountEntity == null ? null : toDto((AccountBaseProjection) accountEntity)
                .setSalt(accountEntity.getSalt());

    }

    /**
     * Maps an Account Entity to DTO.
     *
     * @param accountEntity The Account Entity.
     * @return The Account DTO.
     */
    public AccountDto toDto(
            AccountPublicKeysProjection accountEntity) {

        return accountEntity == null ? null : toDto((AccountBaseProjection) accountEntity)
                .setEncryptionPublicKey(accountEntity.getEncryptionPublicKey())
                .setSigningPublicKey(accountEntity.getSigningPublicKey());

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
                .setSalt(accountDto.getSalt())
                .setEncryptionPublicKey(accountDto.getEncryptionPublicKey())
                .setSigningPublicKey(accountDto.getSigningPublicKey())
                .setEmail(accountDto.getEmail());

    }

}
