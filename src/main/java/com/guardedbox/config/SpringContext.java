package com.guardedbox.config;

import org.springframework.stereotype.Component;

import com.guardedbox.repository.AccountsRepository;
import com.guardedbox.repository.SecretsRepository;

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

    /** SecretsRepository. */
    @Getter
    private static SecretsRepository secretsRepository;

    /**
     * @param accountsRepository AccountsRepository.
     * @param secretsRepository SecretsRepository.
     */
    private SpringContext(
            AccountsRepository accountsRepository,
            SecretsRepository secretsRepository) {

        SpringContext.accountsRepository = accountsRepository;
        SpringContext.secretsRepository = secretsRepository;

    }

}
