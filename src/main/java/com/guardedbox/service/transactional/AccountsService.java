package com.guardedbox.service.transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountWithEntropyExpanderDto;
import com.guardedbox.dto.AccountWithPublicKeyDto;
import com.guardedbox.dto.NewAccountDto;
import com.guardedbox.entity.AccountFullEntity;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.repository.AccountFullEntitiesRepository;
import com.guardedbox.repository.AccountWithEntropyExpanderEntitiesRepository;
import com.guardedbox.repository.AccountWithPublicKeyEntitiesRepository;
import com.guardedbox.service.RandomService;

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

    /** RandomService. */
    private final RandomService randomService;

    /**
     * Constructor with Attributes.
     * 
     * @param accountFullEntitiesRepository AccountFullEntitiesRepository.
     * @param accountWithEntropyExpanderEntitiesRepository AccountWithEntropyExpanderEntitiesRepository.
     * @param accountWithPublicKeyEntitiesRepository AccountWithPublicKeyEntitiesRepository.
     * @param accountsMapper AccountsMapper.
     * @param randomService RandomService.
     */
    public AccountsService(
            @Autowired AccountFullEntitiesRepository accountFullEntitiesRepository,
            @Autowired AccountWithEntropyExpanderEntitiesRepository accountWithEntropyExpanderEntitiesRepository,
            @Autowired AccountWithPublicKeyEntitiesRepository accountWithPublicKeyEntitiesRepository,
            @Autowired AccountsMapper accountsMapper,
            @Autowired RandomService randomService) {
        this.accountFullEntitiesRepository = accountFullEntitiesRepository;
        this.accountWithEntropyExpanderEntitiesRepository = accountWithEntropyExpanderEntitiesRepository;
        this.accountWithPublicKeyEntitiesRepository = accountWithPublicKeyEntitiesRepository;
        this.accountsMapper = accountsMapper;
        this.randomService = randomService;
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
     * Creates an Account.
     * 
     * @param newAccountDto NewAccountDto with the new Account data.
     */
    public void newAccount(
            NewAccountDto newAccountDto) {
        AccountFullEntity accountFullEntity = accountsMapper.fromDto(newAccountDto);
        accountFullEntitiesRepository.save(accountFullEntity);
    }

}
