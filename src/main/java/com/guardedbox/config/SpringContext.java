package com.guardedbox.config;

import org.springframework.stereotype.Component;

import com.guardedbox.repository.AccountsRepository;

import lombok.Getter;

/**
 * Spring Context. Holds Spring beans in static attributes.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Component
public final class SpringContext {

    /** AccountsRepository. */
    @Getter
    private static AccountsRepository accountsRepository;

    /**
     * @param accountsRepository AccountsRepository.
     */
    private SpringContext(
            AccountsRepository accountsRepository) {

        SpringContext.accountsRepository = accountsRepository;

    }

}
