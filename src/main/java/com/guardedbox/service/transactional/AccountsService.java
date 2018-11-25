package com.guardedbox.service.transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountWithPasswordDto;
import com.guardedbox.dto.NewAccountDto;
import com.guardedbox.entity.AccountFullEntity;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.repository.AccountFullEntitiesRepository;
import com.guardedbox.repository.AccountWithPasswordEntitiesRepository;

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

    /** AccountWithPasswordEntitiesRepository. */
    private final AccountWithPasswordEntitiesRepository accountWithPasswordEntitiesRepository;

    /** AccountsMapper. */
    private final AccountsMapper accountsMapper;

    /**
     * Constructor with Attributes.
     * 
     * @param accountFullEntitiesRepository AccountFullEntitiesRepository.
     * @param accountWithPasswordEntitiesRepository AccountWithPasswordEntitiesRepository.
     * @param accountsMapper AccountsMapper.
     */
    public AccountsService(
            @Autowired AccountFullEntitiesRepository accountFullEntitiesRepository,
            @Autowired AccountWithPasswordEntitiesRepository accountWithPasswordEntitiesRepository,
            @Autowired AccountsMapper accountsMapper) {
        this.accountFullEntitiesRepository = accountFullEntitiesRepository;
        this.accountWithPasswordEntitiesRepository = accountWithPasswordEntitiesRepository;
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
     * @return The AccountWithPasswordDto corresponding to the introduced email.
     */
    public AccountWithPasswordDto getAccountWithPassword(
            String email) {
        return accountsMapper.toDtoWithPassword(accountWithPasswordEntitiesRepository.findByEmail(email));
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
