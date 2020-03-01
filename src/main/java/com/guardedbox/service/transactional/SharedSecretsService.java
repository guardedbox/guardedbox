package com.guardedbox.service.transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountWithEncryptionPublicKeyDto;
import com.guardedbox.dto.AccountWithSecretsDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.dto.ShareSecretDto;
import com.guardedbox.entity.AccountWithEncryptionPublicKeyEntity;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.entity.SecretWithOwnerAccountEncryptionPublicKeyEntity;
import com.guardedbox.entity.SharedSecretEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.mapper.SecretsMapper;
import com.guardedbox.repository.SharedSecretEntitiesRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service: SharedSecret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
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
     * @param receiverAccountId Account.accountId of the shared secrets receiver.
     * @return The List of secrets shared with the introduced accountId, grouped by owner account.
     */
    public List<AccountWithSecretsDto> getSecretsSharedWithAccount(
            UUID receiverAccountId) {

        Map<UUID, AccountWithSecretsDto> ownerAccountsWithSecrets = new LinkedHashMap<>();
        List<SharedSecretEntity> receivedSharedSecrets = sharedSecretEntitiesRepository
                .findByReceiverAccountAccountIdOrderBySecretOwnerAccountEmailAscSecretNameAsc(receiverAccountId);

        for (SharedSecretEntity receivedSharedSecret : receivedSharedSecrets) {

            AccountWithEncryptionPublicKeyEntity ownerAccount = receivedSharedSecret.getSecret().getOwnerAccount();
            AccountWithSecretsDto ownerAccountWithSecrets = ownerAccountsWithSecrets.get(ownerAccount.getAccountId());
            if (ownerAccountWithSecrets == null) {
                ownerAccountWithSecrets = accountsMapper.toDtoWithSecrets(ownerAccount);
                ownerAccountsWithSecrets.put(ownerAccount.getAccountId(), ownerAccountWithSecrets);
            }

            SecretDto receivedSecret = secretsMapper.toDto(receivedSharedSecret.getSecret())
                    .setValue(receivedSharedSecret.getValue());

            ownerAccountWithSecrets.getSecrets().add(receivedSecret);

        }

        return new ArrayList<>(ownerAccountsWithSecrets.values());

    }

    /**
     * @param ownerAccountId Account.accountId of the secret owner.
     * @param secretId Secret.secretId of the shared secret.
     * @return The List of accounts with which the introduced secretId is shared.
     */
    public List<AccountWithEncryptionPublicKeyDto> getSharedSecretReceiverAccounts(
            UUID ownerAccountId,
            UUID secretId) {

        SecretEntity secret = secretsService.findAndCheckSecret(secretId, ownerAccountId);
        List<SharedSecretEntity> sharedSecrets = secret.getSharedSecrets();

        List<AccountWithEncryptionPublicKeyDto> receiverAccounts = new ArrayList<>(sharedSecrets.size());
        for (SharedSecretEntity sharedSecret : sharedSecrets)
            receiverAccounts.add(accountsMapper.toDtoWithEncryptionPublicKey(sharedSecret.getReceiverAccount()));

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
            UUID ownerAccountId,
            UUID secretId,
            ShareSecretDto shareSecretDto) {

        SecretWithOwnerAccountEncryptionPublicKeyEntity secret =
                secretsService.findAndCheckSecretWithOwnerAccountEncryptionPublicKey(secretId, ownerAccountId);

        AccountWithEncryptionPublicKeyEntity receiverAccount =
                accountsService.findAndCheckAccountWithEncryptionPublicKeyByEmail(shareSecretDto.getReceiverEmail());
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

        SharedSecretEntity sharedSecret = new SharedSecretEntity()
                .setSecret(secret)
                .setReceiverAccount(receiverAccount)
                .setValue(shareSecretDto.getValue());

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
            UUID ownerAccountId,
            UUID secretId,
            String receiverEmail) {

        secretsService.findAndCheckSecret(secretId, ownerAccountId);

        AccountWithEncryptionPublicKeyEntity reveiverAccount =
                accountsService.findAndCheckAccountWithEncryptionPublicKeyByEmail(receiverEmail);

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
            UUID secretId,
            UUID receiverAccountId) {

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
