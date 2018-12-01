package com.guardedbox.service.transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountWithPublicKeyDto;
import com.guardedbox.dto.AccountWithSecretsDto;
import com.guardedbox.dto.RejectSharedSecretDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.dto.ShareSecretDto;
import com.guardedbox.dto.UnshareSecretDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.AccountWithPublicKeyEntity;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.entity.SharedSecretEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.mapper.SecretsMapper;
import com.guardedbox.repository.SharedSecretEntitiesRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

/**
 * Service: SharedSecret.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
public class SharedSecretsService {

    /** SharedSecretEntitiesRepository. */
    private final SharedSecretEntitiesRepository sharedSecretEntitiesRepository;

    /** SecretsService. */
    private final SecretsService secretsService;

    /** AccountsService. */
    private final AccountsService accountsService;

    /** SecretsMapper. */
    private final SecretsMapper secretsMapper;

    /** AccountsMapper. */
    private final AccountsMapper accountsMapper;

    /**
     * Constructor with Attributes.
     * 
     * @param sharedSecretEntitiesRepository SharedSecretEntitiesRepository.
     * @param secretsService SecretsService.
     * @param accountsService AccountsService.
     * @param secretsMapper SecretsMapper.
     * @param accountsMapper AccountsMapper.
     */
    public SharedSecretsService(
            @Autowired SharedSecretEntitiesRepository sharedSecretEntitiesRepository,
            @Autowired SecretsService secretsService,
            @Autowired AccountsService accountsService,
            @Autowired SecretsMapper secretsMapper,
            @Autowired AccountsMapper accountsMapper) {
        this.sharedSecretEntitiesRepository = sharedSecretEntitiesRepository;
        this.secretsService = secretsService;
        this.accountsService = accountsService;
        this.secretsMapper = secretsMapper;
        this.accountsMapper = accountsMapper;
    }

    /**
     * @param accountId Account.accountId.
     * @param secretId Secret.secretId.
     * @return The List of accounts with which the introduced secretId is shared.
     */
    public List<AccountWithPublicKeyDto> getSharedSecretAccounts(
            Long accountId,
            Long secretId) {

        SecretEntity secret = secretsService.findAndCheckSecret(secretId, accountId);
        List<SharedSecretEntity> sharedSecrets = secret.getSharedSecrets();

        List<AccountWithPublicKeyDto> accounts = new ArrayList<>(sharedSecrets.size());
        for (SharedSecretEntity sharedSecret : sharedSecrets)
            accounts.add(accountsMapper.toDtoWithPublicKey(sharedSecret.getAccount()));

        return accounts;

    }

    /**
     * @param accountId Account.accountId.
     * @return The List of AccountWithSecretsDto representing the secrets shared with the introduced accountId.
     */
    public List<AccountWithSecretsDto> getSecretsSharedWithAccount(
            Long accountId) {

        Map<Long, AccountWithSecretsDto> accountsWithSecrets = new LinkedHashMap<>();
        List<SharedSecretEntity> sharedSecrets = sharedSecretEntitiesRepository.findByAccountAccountIdOrderByAccountEmailAscSecretNameAsc(accountId);

        for (SharedSecretEntity sharedSecret : sharedSecrets) {

            AccountEntity account = sharedSecret.getSecret().getAccount();
            AccountWithSecretsDto accountWithSecrets = accountsWithSecrets.get(account.getAccountId());
            if (accountWithSecrets == null) {
                accountWithSecrets = accountsMapper.toDtoWithSecrets(account);
                accountsWithSecrets.put(account.getAccountId(), accountWithSecrets);
            }

            SecretDto secret = secretsMapper.toDto(sharedSecret.getSecret());
            secret.setValue(sharedSecret.getValue());

            accountWithSecrets.getSecrets().add(secret);

        }

        return new ArrayList<>(accountsWithSecrets.values());

    }

    /**
     * Shares a secret.
     * 
     * @param accountId Account.accountId of the secret owner.
     * @param shareSecretDto DTO with the secret to be shared data.
     */
    public void shareSecret(
            Long accountId,
            ShareSecretDto shareSecretDto) {

        SecretEntity secret = secretsService.findAndCheckSecret(shareSecretDto.getSecretId(), accountId);
        AccountWithPublicKeyEntity account = accountsService.findAndCheckAccountWithPublicKey(shareSecretDto.getEmail(), accountId);

        SharedSecretEntity alreadySharedSecret = sharedSecretEntitiesRepository.findBySecretSecretIdAndAccountAccountId(
                secret.getSecretId(), account.getAccountId());
        if (alreadySharedSecret != null) {
            throw new ServiceException(String.format(
                    "Secret %s is already shared with email %s", shareSecretDto.getSecretId(), shareSecretDto.getEmail()))
                            .setErrorCode("shared-secrets.secret-already-shared-with-email");
        }

        SharedSecretEntity sharedSecret = new SharedSecretEntity();
        sharedSecret.setSecret(secret);
        sharedSecret.setAccount(account);
        sharedSecret.setValue(shareSecretDto.getValue());

        sharedSecretEntitiesRepository.save(sharedSecret);

    }

    /**
     * Unshares a secret.
     * 
     * @param accountId Account.accountId of the secret owner.
     * @param unshareSecretDto DTO with the secret to be unshared data.
     */
    public void unshareSecret(
            Long accountId,
            UnshareSecretDto unshareSecretDto) {

        SecretEntity secret = secretsService.findAndCheckSecret(unshareSecretDto.getSecretId(), accountId);
        AccountWithPublicKeyEntity account = accountsService.findAndCheckAccountWithPublicKey(unshareSecretDto.getEmail(), accountId);

        SharedSecretEntity sharedSecret = sharedSecretEntitiesRepository.findBySecretSecretIdAndAccountAccountId(
                secret.getSecretId(), account.getAccountId());
        if (sharedSecret == null) {
            throw new ServiceException(String.format(
                    "Secret %s is not shared with account %s", unshareSecretDto.getSecretId(), account.getAccountId()))
                            .setErrorCode("shared-secrets.secret-not-shared-with-email");
        }

        sharedSecretEntitiesRepository.delete(sharedSecret);

    }

    /**
     * Unshares a secret currently shared to an account.
     * 
     * @param accountId Account.accountId of the shared secret receiver.
     * @param rejectSharedSecretDto DTO with the secret to be unshared data.
     */
    public void rejectSharedSecret(
            Long accountId,
            RejectSharedSecretDto rejectSharedSecretDto) {

        SharedSecretEntity sharedSecret = sharedSecretEntitiesRepository.findBySecretSecretIdAndAccountAccountId(
                rejectSharedSecretDto.getSecretId(), accountId);
        if (sharedSecret == null) {
            throw new ServiceException(String.format(
                    "Secret %s is not shared with account %s", rejectSharedSecretDto.getSecretId(), accountId))
                            .setErrorCode("shared-secrets.secret-not-shared-with-you");
        }

        sharedSecretEntitiesRepository.delete(sharedSecret);

    }

}
