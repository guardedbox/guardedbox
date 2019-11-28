package com.guardedbox.service.transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountWithPublicKeyDto;
import com.guardedbox.dto.AccountWithSecretsDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.dto.ShareSecretDto;
import com.guardedbox.entity.AccountWithPublicKeyEntity;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.entity.SecretWithOwnerAccountPublicKeyEntity;
import com.guardedbox.entity.SharedSecretEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.mapper.SecretsMapper;
import com.guardedbox.repository.SharedSecretEntitiesRepository;

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
     * @param receiverAccountId Account.accountId of the shared secrets receiver.
     * @return The List of secrets shared with the introduced accountId, grouped by owner account.
     */
    public List<AccountWithSecretsDto> getSecretsSharedWithAccount(
            Long receiverAccountId) {

        Map<Long, AccountWithSecretsDto> ownerAccountsWithSecrets = new LinkedHashMap<>();
        List<SharedSecretEntity> receivedSharedSecrets = sharedSecretEntitiesRepository
                .findByReceiverAccountAccountIdOrderBySecretOwnerAccountEmailAscSecretNameAsc(receiverAccountId);

        for (SharedSecretEntity receivedSharedSecret : receivedSharedSecrets) {

            AccountWithPublicKeyEntity ownerAccount = receivedSharedSecret.getSecret().getOwnerAccount();
            AccountWithSecretsDto ownerAccountWithSecrets = ownerAccountsWithSecrets.get(ownerAccount.getAccountId());
            if (ownerAccountWithSecrets == null) {
                ownerAccountWithSecrets = accountsMapper.toDtoWithSecrets(ownerAccount);
                ownerAccountsWithSecrets.put(ownerAccount.getAccountId(), ownerAccountWithSecrets);
            }

            SecretDto receivedSecret = secretsMapper.toDto(receivedSharedSecret.getSecret());
            receivedSecret.setValue(receivedSharedSecret.getValue());

            ownerAccountWithSecrets.getSecrets().add(receivedSecret);

        }

        return new ArrayList<>(ownerAccountsWithSecrets.values());

    }

    /**
     * @param ownerAccountId Account.accountId of the secret owner.
     * @param secretId Secret.secretId of the shared secret.
     * @return The List of accounts with which the introduced secretId is shared.
     */
    public List<AccountWithPublicKeyDto> getSharedSecretReceiverAccounts(
            Long ownerAccountId,
            Long secretId) {

        SecretEntity secret = secretsService.findAndCheckSecret(secretId, ownerAccountId);
        List<SharedSecretEntity> sharedSecrets = secret.getSharedSecrets();

        List<AccountWithPublicKeyDto> receiverAccounts = new ArrayList<>(sharedSecrets.size());
        for (SharedSecretEntity sharedSecret : sharedSecrets)
            receiverAccounts.add(accountsMapper.toDtoWithPublicKey(sharedSecret.getReceiverAccount()));

        return receiverAccounts;

    }

    /**
     * Shares a secret.
     *
     * @param ownerAccountId Account.accountId of the secret owner.
     * @param secretId Secret.secretId of the secret to be shared.
     * @param shareSecretDto DTO with the secret to be shared data.
     */
    public void shareSecret(
            Long ownerAccountId,
            Long secretId,
            ShareSecretDto shareSecretDto) {

        SecretWithOwnerAccountPublicKeyEntity secret =
                secretsService.findAndCheckSecret(secretId, ownerAccountId);

        AccountWithPublicKeyEntity receiverAccount =
                accountsService.findAndCheckAccountWithPublicKeyByEmail(shareSecretDto.getReceiverEmail());
        if (receiverAccount.getAccountId().equals(ownerAccountId)) {
            throw new ServiceException(String.format(
                    "Secret %s belongs to email %s", secretId, shareSecretDto.getReceiverEmail()))
                            .setErrorCode("shared-secrets.do-not-self-share");
        }

        SharedSecretEntity alreadySharedSecret =
                sharedSecretEntitiesRepository.findBySecretSecretIdAndReceiverAccountAccountId(secret.getSecretId(), receiverAccount.getAccountId());
        if (alreadySharedSecret != null) {
            throw new ServiceException(String.format(
                    "Secret %s is already shared with email %s", secretId, shareSecretDto.getReceiverEmail()))
                            .setErrorCode("shared-secrets.secret-already-shared-with-email")
                            .addAdditionalData("email", shareSecretDto.getReceiverEmail());
        }

        SharedSecretEntity sharedSecret = new SharedSecretEntity();
        sharedSecret.setSecret(secret);
        sharedSecret.setReceiverAccount(receiverAccount);
        sharedSecret.setValue(shareSecretDto.getValue());

        sharedSecretEntitiesRepository.save(sharedSecret);

    }

    /**
     * Unshares a secret.
     *
     * @param ownerAccountId Account.accountId of the secret owner.
     * @param secretId Secret.secretId of the secret to be unshared.
     * @param receiverEmail Account.email of the account from which the secret will be unshared.
     */
    public void unshareSecret(
            Long ownerAccountId,
            Long secretId,
            String receiverEmail) {

        secretsService.findAndCheckSecret(secretId, ownerAccountId);

        AccountWithPublicKeyEntity reveiverAccount =
                accountsService.findAndCheckAccountWithPublicKeyByEmail(receiverEmail);

        SharedSecretEntity sharedSecret =
                sharedSecretEntitiesRepository.findBySecretSecretIdAndReceiverAccountAccountId(secretId, reveiverAccount.getAccountId());
        if (sharedSecret == null) {
            throw new ServiceException(String.format(
                    "Secret %s is not shared with account %s", secretId, reveiverAccount.getAccountId()))
                            .setErrorCode("shared-secrets.secret-not-shared-with-email").addAdditionalData("email", receiverEmail);
        }

        sharedSecretEntitiesRepository.delete(sharedSecret);

    }

    /**
     * Unshares a secret.
     *
     * @param secretId Secret.secretId of the secret to be unshared.
     * @param receiverAccountId Account.accountId of the account from which the secret will be unshared.
     */
    public void rejectSharedSecret(
            Long secretId,
            Long receiverAccountId) {

        SharedSecretEntity sharedSecret =
                sharedSecretEntitiesRepository.findBySecretSecretIdAndReceiverAccountAccountId(secretId, receiverAccountId);
        if (sharedSecret == null) {
            throw new ServiceException(String.format(
                    "Secret %s is not shared with account %s", secretId, receiverAccountId))
                            .setErrorCode("shared-secrets.secret-not-shared-with-you");
        }

        sharedSecretEntitiesRepository.delete(sharedSecret);

    }

}
