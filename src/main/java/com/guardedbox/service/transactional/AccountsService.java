package com.guardedbox.service.transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountWithEntropyExpanderDto;
import com.guardedbox.dto.AccountWithPublicKeyDto;
import com.guardedbox.dto.NewAccountDto;
import com.guardedbox.entity.AccountFullEntity;
import com.guardedbox.entity.AccountWithPublicKeyEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.repository.AccountFullEntitiesRepository;
import com.guardedbox.repository.AccountWithEntropyExpanderEntitiesRepository;
import com.guardedbox.repository.AccountWithPublicKeyEntitiesRepository;

import javax.transaction.Transactional;

/**
 * Service: Account.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
public class AccountsService {

    /** AccountFullEntitiesRepository. */
    private final AccountFullEntitiesRepository accountFullEntitiesRepository;

    /** AccountWithEntropyExpanderEntitiesRepository. */
    private final AccountWithEntropyExpanderEntitiesRepository accountWithEntropyExpanderEntitiesRepository;

    /** AccountWithPublicKeyEntitiesRepository. */
    private final AccountWithPublicKeyEntitiesRepository accountWithPublicKeyEntitiesRepository;

    /** AccountsMapper. */
    private final AccountsMapper accountsMapper;

    /**
     * Constructor with Attributes.
     * 
     * @param accountFullEntitiesRepository AccountFullEntitiesRepository.
     * @param accountWithEntropyExpanderEntitiesRepository AccountWithEntropyExpanderEntitiesRepository.
     * @param accountWithPublicKeyEntitiesRepository AccountWithPublicKeyEntitiesRepository.
     * @param accountsMapper AccountsMapper.
     */
    public AccountsService(
            @Autowired AccountFullEntitiesRepository accountFullEntitiesRepository,
            @Autowired AccountWithEntropyExpanderEntitiesRepository accountWithEntropyExpanderEntitiesRepository,
            @Autowired AccountWithPublicKeyEntitiesRepository accountWithPublicKeyEntitiesRepository,
            @Autowired AccountsMapper accountsMapper) {
        this.accountFullEntitiesRepository = accountFullEntitiesRepository;
        this.accountWithEntropyExpanderEntitiesRepository = accountWithEntropyExpanderEntitiesRepository;
        this.accountWithPublicKeyEntitiesRepository = accountWithPublicKeyEntitiesRepository;
        this.accountsMapper = accountsMapper;
    }

    /**
     * @param email Account.email.
     * @return Boolean indicating if an Account corresponding to the introduced email exists.
     */
    public boolean isEmailRegistered(
            String email) {
        return accountFullEntitiesRepository.existsByEmail(email);
    }

    /**
     * @param email Account.email.
     * @return The AccountWithEntropyExpanderDto corresponding to the introduced email.
     */
    public AccountWithEntropyExpanderDto getAccountWithEntropyExpander(
            String email) {
        return accountsMapper.toDtoWithEntropyExpander(accountWithEntropyExpanderEntitiesRepository.findByEmail(email));
    }

    /**
     * @param email Account.email.
     * @return The AccountWithPublicKeyDto corresponding to the introduced email.
     */
    public AccountWithPublicKeyDto getAccountWithPublicKey(
            String email) {
        return accountsMapper.toDtoWithPublicKey(accountWithPublicKeyEntitiesRepository.findByEmail(email));
    }

    /**
     * @param email Account.email.
     * @param accountId Account.accountId to check if its own public key is being requested.
     * @return The AccountWithPublicKeyDto corresponding to the introduced email. Checks if it exists and does not correspond to the introduced
     *         accoundId.
     */
    public AccountWithPublicKeyDto getAndCheckAccountWithPublicKey(
            String email,
            Long accountId) {
        return accountsMapper.toDtoWithPublicKey(findAndCheckAccountWithPublicKey(email, accountId));
    }

    /**
     * Creates an Account.
     * 
     * @param newAccountDto NewAccountDto with the new Account data.
     */
    public void newAccount(
            NewAccountDto newAccountDto) {
        AccountFullEntity accountFullEntity = accountsMapper.fromDto(newAccountDto);
        accountFullEntitiesRepository.save(accountFullEntity);
    }

    /**
     * Finds an AccountWithPublicKey by email and checks if it exists and does not correspond to an accoundId.
     * 
     * @param email The email.
     * @param accountId The accountId.
     * @return The AccountWithPublicKey.
     */
    protected AccountWithPublicKeyEntity findAndCheckAccountWithPublicKey(
            String email,
            Long accountId) {

        AccountWithPublicKeyEntity account = accountWithPublicKeyEntitiesRepository.findByEmail(email);

        if (account == null) {
            throw new ServiceException(
                    String.format("Email %s is not registered", email))
                            .setErrorCode("shared-secrets.email-not-registered");
        }

        if (accountId != null && accountId.equals(account.getAccountId())) {
            throw new ServiceException(
                    String.format("Email %s public key should not be retrieved by account %s since it is its own public key", email, accountId))
                            .setErrorCode("shared-secrets.do-not-self-share");
        }

        return account;

    }

}
