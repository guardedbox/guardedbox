package com.guardedbox.service.transactional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.AccountWithEncryptionPublicKeyDto;
import com.guardedbox.dto.AccountWithSaltDto;
import com.guardedbox.dto.AccountWithSigningPublicKeyDto;
import com.guardedbox.dto.CreateAccountDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.AccountFullEntity;
import com.guardedbox.entity.AccountWithEncryptionPublicKeyEntity;
import com.guardedbox.entity.AccountWithSaltEntity;
import com.guardedbox.entity.AccountWithSigningPublicKeyEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.repository.AccountEntitiesRepository;
import com.guardedbox.repository.AccountFullEntitiesRepository;
import com.guardedbox.repository.AccountWithEncryptionPublicKeyEntitiesRepository;
import com.guardedbox.repository.AccountWithSaltEntitiesRepository;
import com.guardedbox.repository.AccountWithSigningPublicKeyEntitiesRepository;

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

    /** AccountEntitiesRepository. */
    private final AccountEntitiesRepository accountEntitiesRepository;

    /** AccountWithSaltEntitiesRepository. */
    private final AccountWithSaltEntitiesRepository accountWithSaltEntitiesRepository;

    /** AccountWithEncryptionPublicKeyEntitiesRepository. */
    private final AccountWithEncryptionPublicKeyEntitiesRepository accountWithEncryptionPublicKeyEntitiesRepository;

    /** AccountWithSigningPublicKeyEntitiesRepository. */
    private final AccountWithSigningPublicKeyEntitiesRepository accountWithSigningPublicKeyEntitiesRepository;

    /** AccountFullEntitiesRepository. */
    private final AccountFullEntitiesRepository accountFullEntitiesRepository;

    /** AccountsMapper. */
    private final AccountsMapper accountsMapper;

    /**
     * @param email Account.email.
     * @return Boolean indicating if an Account corresponding to the introduced email exists.
     */
    public boolean existsAccountByEmail(
            String email) {

        return accountFullEntitiesRepository.existsByEmail(email);

    }

    /**
     * @param salt Account.salt.
     * @return Boolean indicating if an Account corresponding to the introduced salt exists.
     */
    public boolean existsAccountBySalt(
            String salt) {

        return accountFullEntitiesRepository.existsBySalt(salt);

    }

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
     * @return The AccountWithSaltDto corresponding to the introduced email. Checks if it exists.
     */
    public AccountWithSaltDto getAndCheckAccountWithSaltByEmail(
            String email) {

        return accountsMapper.toDtoWithSalt(findAndCheckAccountWithSaltByEmail(email));

    }

    /**
     * @param email Account.email.
     * @return The AccountWithEncryptionPublicKeyDto corresponding to the introduced email. Checks if it exists.
     */
    public AccountWithEncryptionPublicKeyDto getAndCheckAccountWithEncryptionPublicKeyByEmail(
            String email) {

        return accountsMapper.toDtoWithEncryptionPublicKey(findAndCheckAccountWithEncryptionPublicKeyByEmail(email));

    }

    /**
     * @param email Account.email.
     * @return The AccountWithSigningPublicKeyDto corresponding to the introduced email. Checks if it exists.
     */
    public AccountWithSigningPublicKeyDto getAndCheckAccountWithSigningPublicKeyByEmail(
            String email) {

        return accountsMapper.toDtoWithSigningPublicKey(findAndCheckAccountWithSigningPublicKeyByEmail(email));

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
        if (existsAccountByEmail(createAccountDto.getEmail())) {
            throw new ServiceException(
                    String.format("Account was not created since email %s is already registered", createAccountDto.getEmail()))
                            .setErrorCode("accounts.email-already-registered").addAdditionalData("email", createAccountDto.getEmail());
        }

        // Check if the salt exists.
        if (existsAccountBySalt(createAccountDto.getSalt())) {
            throw new ServiceException(
                    String.format("Account was not created since salt %s already exists", createAccountDto.getSalt()));
        }

        // Create the new account.
        AccountFullEntity accountFullEntity = accountsMapper.fromDto(createAccountDto);
        return accountsMapper.toDto(accountFullEntitiesRepository.save(accountFullEntity));

    }

    /**
     * Finds an AccountEntity by email and checks if it exists.
     *
     * @param email The email.
     * @return The AccountEntity.
     */
    protected AccountEntity findAndCheckAccountByEmail(
            String email) {

        AccountEntity account = accountEntitiesRepository.findByEmail(email);

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("accounts.email-not-registered").addAdditionalData("email", email);
        }

        return account;

    }

    /**
     * Finds an AccountWithSalt by email and checks if it exists.
     *
     * @param email The email.
     * @return The AccountWithSalt.
     */
    protected AccountWithSaltEntity findAndCheckAccountWithSaltByEmail(
            String email) {

        AccountWithSaltEntity account = accountWithSaltEntitiesRepository.findByEmail(email);

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("accounts.email-not-registered").addAdditionalData("email", email);
        }

        return account;

    }

    /**
     * Finds an AccountWithEncryptionPublicKey by email and checks if it exists.
     *
     * @param email The email.
     * @return The AccountWithEncryptionPublicKeyEntity.
     */
    protected AccountWithEncryptionPublicKeyEntity findAndCheckAccountWithEncryptionPublicKeyByEmail(
            String email) {

        AccountWithEncryptionPublicKeyEntity account = accountWithEncryptionPublicKeyEntitiesRepository.findByEmail(email);

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("accounts.email-not-registered").addAdditionalData("email", email);
        }

        return account;

    }

    /**
     * Finds an AccountWithSigningPublicKey by email and checks if it exists.
     *
     * @param email The email.
     * @return The AccountWithSigningPublicKeyEntity.
     */
    protected AccountWithSigningPublicKeyEntity findAndCheckAccountWithSigningPublicKeyByEmail(
            String email) {

        AccountWithSigningPublicKeyEntity account = accountWithSigningPublicKeyEntitiesRepository.findByEmail(email);

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("accounts.email-not-registered").addAdditionalData("email", email);
        }

        return account;

    }

}
