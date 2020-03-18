package com.guardedbox.service.transactional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.CreateAccountDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.AccountPublicKeysProjection;
import com.guardedbox.entity.projection.AccountSaltProjection;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.repository.AccountsRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AccountsService {

    /** AccountsRepository. */
    private final AccountsRepository accountsRepository;

    /** AccountsMapper. */
    private final AccountsMapper accountsMapper;

    /**
     * @param email Account.email.
     * @return The AccountDto corresponding to the introduced email. Checks if it exists.
     */
    public AccountDto getAndCheckAccountByEmail(
            String email) {

        return accountsMapper.toDto(findAndCheckAccountByEmail(email));

    }

    /**
     * @param email Account.email.
     * @return The AccountDto corresponding to the introduced email. Checks if it exists.
     */
    public AccountDto getAndCheckAccountSaltByEmail(
            String email) {

        return accountsMapper.toDto(findAndCheckAccountSaltByEmail(email));

    }

    /**
     * @param email Account.email.
     * @return The AccountDto corresponding to the introduced email. Checks if it exists.
     */
    public AccountDto getAndCheckAccountPublicKeysByEmail(
            String email) {

        return accountsMapper.toDto(findAndCheckAccountPublicKeysByEmail(email));

    }

    /**
     * Creates an Account.
     *
     * @param createAccountDto CreateAccountDto with the new Account data.
     * @return AccountDto with the new Account data.
     */
    public AccountDto createAccount(
            CreateAccountDto createAccountDto) {

        // Check if the email is already registered.
        if (accountsRepository.existsByEmail(createAccountDto.getEmail())) {
            throw new ServiceException(
                    String.format("Account was not created since email %s is already registered", createAccountDto.getEmail()))
                            .setErrorCode("accounts.email-already-registered").addAdditionalData("email", createAccountDto.getEmail());
        }

        // Check if the salt exists.
        if (accountsRepository.existsBySalt(createAccountDto.getSalt())) {
            throw new ServiceException(
                    String.format("Account was not created since salt %s already exists", createAccountDto.getSalt()));
        }

        // Create the new account.
        AccountEntity accountFullEntity = accountsMapper.fromDto(createAccountDto);
        return accountsMapper.toDto(accountsRepository.save(accountFullEntity));

    }

    /**
     * Finds an AccountEntity by email and checks if it exists.
     *
     * @param email The email.
     * @return The AccountEntity.
     */
    protected AccountEntity findAndCheckAccountByEmail(
            String email) {

        AccountEntity account = accountsRepository.findByEmail(email);

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("accounts.email-not-registered").addAdditionalData("email", email);
        }

        return account;

    }

    /**
     * Finds an AccountBaseProjection by email and checks if it exists.
     *
     * @param email The email.
     * @return The AccountSaltProjection.
     */
    protected AccountBaseProjection findAndCheckAccountBaseByEmail(
            String email) {

        AccountBaseProjection account = accountsRepository.findBaseByEmail(email);

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("accounts.email-not-registered").addAdditionalData("email", email);
        }

        return account;

    }

    /**
     * Finds an AccountSaltProjection by email and checks if it exists.
     *
     * @param email The email.
     * @return The AccountSaltProjection.
     */
    protected AccountSaltProjection findAndCheckAccountSaltByEmail(
            String email) {

        AccountSaltProjection account = accountsRepository.findSaltByEmail(email);

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("accounts.email-not-registered").addAdditionalData("email", email);
        }

        return account;

    }

    /**
     * Finds an AccountPublicKeysProjection by email and checks if it exists.
     *
     * @param email The email.
     * @return The AccountPublicKeysProjection.
     */
    protected AccountPublicKeysProjection findAndCheckAccountPublicKeysByEmail(
            String email) {

        AccountPublicKeysProjection account = accountsRepository.findPublicKeysByEmail(email);

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("accounts.email-not-registered").addAdditionalData("email", email);
        }

        return account;

    }

}
