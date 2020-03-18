package com.guardedbox.service.transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.dto.ShareSecretDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.entity.SharedSecretEntity;
import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.AccountPublicKeysProjection;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.mapper.SecretsMapper;
import com.guardedbox.repository.AccountsRepository;
import com.guardedbox.repository.SharedSecretsRepository;

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

    /** SharedSecretsRepository. */
    private final SharedSecretsRepository sharedSecretsRepository;

    /** AccountsRepository. */
    private final AccountsRepository accountsRepository;

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
    public List<AccountDto> getSecretsSharedWithAccount(
            UUID receiverAccountId) {

        Map<UUID, AccountDto> ownerAccountsWithSecrets = new LinkedHashMap<>();
        List<SharedSecretEntity> receivedSharedSecrets = sharedSecretsRepository
                .findByReceiverAccountAccountIdOrderBySecretOwnerAccountEmailAscSecretNameAsc(receiverAccountId);

        for (SharedSecretEntity receivedSharedSecret : receivedSharedSecrets) {

            AccountPublicKeysProjection ownerAccount = accountsRepository.findPublicKeysByAccountId(
                    receivedSharedSecret.getSecret().getOwnerAccount().getAccountId());
            AccountDto ownerAccountWithSecrets = ownerAccountsWithSecrets.get(ownerAccount.getAccountId());
            if (ownerAccountWithSecrets == null) {
                ownerAccountWithSecrets = accountsMapper.toDto(ownerAccount).setSecrets(new LinkedList<>());
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
    public List<AccountDto> getSharedSecretReceiverAccounts(
            UUID ownerAccountId,
            UUID secretId) {

        SecretEntity secret = secretsService.findAndCheckSecret(secretId, ownerAccountId);
        List<SharedSecretEntity> sharedSecrets = secret.getSharedSecrets();

        List<AccountDto> receiverAccounts = new ArrayList<>(sharedSecrets.size());
        for (SharedSecretEntity sharedSecret : sharedSecrets) {
            AccountPublicKeysProjection receiverAccount = accountsRepository.findPublicKeysByAccountId(
                    sharedSecret.getReceiverAccount().getAccountId());
            receiverAccounts.add(accountsMapper.toDto(receiverAccount));
        }

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

        SecretEntity secret = secretsService.findAndCheckSecret(secretId, ownerAccountId);

        AccountBaseProjection receiverAccount = accountsService.findAndCheckAccountBaseByEmail(shareSecretDto.getReceiverEmail());
        if (receiverAccount.getAccountId().equals(ownerAccountId)) {
            throw new ServiceException(String.format(
                    "Secret %s belongs to email %s", secretId, shareSecretDto.getReceiverEmail()))
                            .setErrorCode("shared-secrets.do-not-self-share");
        }

        SharedSecretEntity alreadySharedSecret =
                sharedSecretsRepository.findBySecretSecretIdAndReceiverAccountAccountId(secret.getSecretId(), receiverAccount.getAccountId());
        if (alreadySharedSecret != null) {
            throw new ServiceException(String.format(
                    "Secret %s is already shared with email %s", secretId, shareSecretDto.getReceiverEmail()))
                            .setErrorCode("shared-secrets.secret-already-shared-with-email")
                            .addAdditionalData("email", shareSecretDto.getReceiverEmail());
        }

        SharedSecretEntity sharedSecret = new SharedSecretEntity()
                .setSecret(secret)
                .setReceiverAccount(new AccountEntity().setAccountId(receiverAccount.getAccountId()))
                .setValue(shareSecretDto.getValue());

        sharedSecretsRepository.save(sharedSecret);

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

        AccountBaseProjection reveiverAccount = accountsService.findAndCheckAccountBaseByEmail(receiverEmail);

        SharedSecretEntity sharedSecret =
                sharedSecretsRepository.findBySecretSecretIdAndReceiverAccountAccountId(secretId, reveiverAccount.getAccountId());
        if (sharedSecret == null) {
            throw new ServiceException(String.format(
                    "Secret %s is not shared with account %s", secretId, reveiverAccount.getAccountId()))
                            .setErrorCode("shared-secrets.secret-not-shared-with-email").addAdditionalData("email", receiverEmail);
        }

        sharedSecretsRepository.delete(sharedSecret);

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
                sharedSecretsRepository.findBySecretSecretIdAndReceiverAccountAccountId(secretId, receiverAccountId);
        if (sharedSecret == null) {
            throw new ServiceException(String.format(
                    "Secret %s is not shared with account %s", secretId, receiverAccountId))
                            .setErrorCode("shared-secrets.secret-not-shared-with-you");
        }

        sharedSecretsRepository.delete(sharedSecret);

    }

}
