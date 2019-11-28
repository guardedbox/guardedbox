package com.guardedbox.service.transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.CreateSecretDto;
import com.guardedbox.dto.EditSecretDto;
import com.guardedbox.dto.EditSecretSharingDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.entity.SecretWithOwnerAccountEncryptionPublicKeyEntity;
import com.guardedbox.entity.SharedSecretEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.SecretsMapper;
import com.guardedbox.repository.SecretEntitiesRepository;
import com.guardedbox.repository.SecretWithOwnerAccountEncryptionPublicKeyEntitiesRepository;

/**
 * Service: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
public class SecretsService {

    /** SecretEntitiesRepository. */
    private final SecretEntitiesRepository secretEntitiesRepository;

    /** SecretWithOwnerAccountEncryptionPublicKeyEntitiesRepository. */
    private final SecretWithOwnerAccountEncryptionPublicKeyEntitiesRepository secretWithOwnerAccountEncryptionPublicKeyEntitiesRepository;

    /** SecretsMapper. */
    private final SecretsMapper secretsMapper;

    /**
     * Constructor with Attributes.
     *
     * @param secretEntitiesRepository SecretEntitiesRepository.
     * @param secretWithOwnerAccountEncryptionPublicKeyEntitiesRepository SecretWithOwnerAccountEncryptionPublicKeyEntitiesRepository.
     * @param secretsMapper SecretsMapper.
     */
    public SecretsService(
            @Autowired SecretEntitiesRepository secretEntitiesRepository,
            @Autowired SecretWithOwnerAccountEncryptionPublicKeyEntitiesRepository secretWithOwnerAccountEncryptionPublicKeyEntitiesRepository,
            @Autowired SecretsMapper secretsMapper) {
        this.secretEntitiesRepository = secretEntitiesRepository;
        this.secretWithOwnerAccountEncryptionPublicKeyEntitiesRepository = secretWithOwnerAccountEncryptionPublicKeyEntitiesRepository;
        this.secretsMapper = secretsMapper;
    }

    /**
     * @param ownerAccountId Account.accountId.
     * @return The List of SecretDtos corresponding to the introduced owner accountId.
     */
    public List<SecretDto> getSecretsByOwnerAccountId(
            Long ownerAccountId) {

        return secretsMapper.toDto(secretEntitiesRepository.findByOwnerAccountAccountIdOrderByNameAsc(ownerAccountId));

    }

    /**
     * Creates a Secret.
     *
     * @param ownerAccountId Account.accountId of the secret owner.
     * @param createSecretDto CreateSecretDto with the new Secret data.
     * @return SecretDto with the created Secret data.
     */
    public SecretDto createSecret(
            Long ownerAccountId,
            CreateSecretDto createSecretDto) {

        SecretEntity secret = secretsMapper.fromDto(createSecretDto);
        secret.setOwnerAccount(new AccountEntity(ownerAccountId));
        return secretsMapper.toDto(secretEntitiesRepository.save(secret));

    }

    /**
     * Edits a Secret.
     *
     * @param ownerAccountId Account.accountId of the secret owner.
     * @param secretId ID of the Secret to be edited.
     * @param editSecretDto EditSecretDto with the Secret edition data.
     * @return SecretDto with the edited Secret data.
     */
    public SecretDto editSecret(
            Long ownerAccountId,
            Long secretId,
            EditSecretDto editSecretDto) {

        SecretEntity secret = findAndCheckSecret(secretId, ownerAccountId);

        secret.setName(editSecretDto.getName());
        secret.setValue(editSecretDto.getValue());

        if (editSecretDto.getSharings().size() != secret.getSharedSecrets().size()) {
            throw new ServiceException(String.format(
                    "Edit secret sharings do not match secret %s current sharings", secretId));
        }
        Map<String, EditSecretSharingDto> editSecretSharings = new HashMap<>();
        for (EditSecretSharingDto editSecretSharing : editSecretDto.getSharings()) {
            editSecretSharings.put(editSecretSharing.getReceiverEmail(), editSecretSharing);
        }
        for (SharedSecretEntity sharedSecret : secret.getSharedSecrets()) {
            EditSecretSharingDto editSecretSharing = editSecretSharings.get(sharedSecret.getReceiverAccount().getEmail());
            if (editSecretSharing == null) {
                throw new ServiceException(String.format(
                        "Edit secret sharings do not match secret %s current sharings", secretId));
            }
            sharedSecret.setValue(editSecretSharing.getValue());
        }

        return secretsMapper.toDto(secretEntitiesRepository.save(secret));

    }

    /**
     * Deletes a Secret.
     *
     * @param ownerAccountId Account.accountId of the secret owner.
     * @param secretId ID of the Secret to be deleted.
     * @return SecretDto with the deleted Secret data.
     */
    public SecretDto deleteSecret(
            Long ownerAccountId,
            Long secretId) {

        SecretEntity secret = findAndCheckSecret(secretId, ownerAccountId);
        secretEntitiesRepository.delete(secret);
        return secretsMapper.toDto(secret);

    }

    /**
     * Finds a Secret by secretId and checks if it exists and belongs to an ownerAccountId.
     *
     * @param secretId The secretId.
     * @param ownerAccountId The accountId.
     * @return The Secret.
     */
    protected SecretEntity findAndCheckSecret(
            Long secretId,
            Long ownerAccountId) {

        SecretEntity secret = secretEntitiesRepository.findById(secretId).orElse(null);

        if (secret == null) {
            throw new ServiceException(String.format("Secret %s does not exist", secretId))
                    .setErrorCode("my-secrets.secret-does-not-exist");
        }

        if (ownerAccountId != null && !ownerAccountId.equals(secret.getOwnerAccount().getAccountId())) {
            throw new AuthorizationServiceException(String.format(
                    "Secret %s cannot be managed by account %s since it belongs to account %s",
                    secretId, ownerAccountId, secret.getOwnerAccount().getAccountId()));
        }

        return secret;

    }

    /**
     * Finds a Secret by secretId and checks if it exists and belongs to an ownerAccountId.
     *
     * @param secretId The secretId.
     * @param ownerAccountId The accountId.
     * @return The Secret.
     */
    protected SecretWithOwnerAccountEncryptionPublicKeyEntity findAndCheckSecretWithOwnerAccountEncryptionPublicKey(
            Long secretId,
            Long ownerAccountId) {

        SecretWithOwnerAccountEncryptionPublicKeyEntity secret =
                secretWithOwnerAccountEncryptionPublicKeyEntitiesRepository.findById(secretId).orElse(null);

        if (secret == null) {
            throw new ServiceException(String.format("Secret %s does not exist", secretId))
                    .setErrorCode("my-secrets.secret-does-not-exist");
        }

        if (ownerAccountId != null && !ownerAccountId.equals(secret.getOwnerAccount().getAccountId())) {
            throw new AuthorizationServiceException(String.format(
                    "Secret %s cannot be managed by account %s since it belongs to account %s",
                    secretId, ownerAccountId, secret.getOwnerAccount().getAccountId()));
        }

        return secret;

    }

}
