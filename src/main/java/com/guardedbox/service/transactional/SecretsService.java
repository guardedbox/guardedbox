package com.guardedbox.service.transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.CreateSecretDto;
import com.guardedbox.dto.EditSecretDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.dto.ShareSecretDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.entity.SharedSecretEntity;
import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.SecretBaseProjection;
import com.guardedbox.entity.projection.SecretMustRotateKeyProjection;
import com.guardedbox.entity.projection.SecretValueProjection;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.SecretsMapper;
import com.guardedbox.repository.SecretsRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SecretsService {

    /** SecretsRepository. */
    private final SecretsRepository secretsRepository;

    /** SecretsMapper. */
    private final SecretsMapper secretsMapper;

    /**
     * @param ownerAccountId Account.accountId.
     * @return The List of SecretDtos corresponding to the introduced owner accountId.
     */
    public List<SecretDto> getSecretsByOwnerAccountId(
            UUID ownerAccountId) {

        return secretsMapper.toDto(secretsRepository.findByOwnerAccountAccountId(ownerAccountId));

    }

    /**
     * @param ownerAccountId Account.accountId.
     * @param secretId Secret.secretId.
     * @return SecretDto indicating if the secret corresponding to the introduced secretId must rotate its key.
     */
    public SecretDto getSecretMustRotateKey(
            UUID ownerAccountId,
            UUID secretId) {

        return secretsMapper.toDto(findAndCheckSecret(secretId, ownerAccountId, SecretMustRotateKeyProjection.class));

    }

    /**
     * Creates a Secret.
     *
     * @param ownerAccountId Account.accountId of the secret owner.
     * @param createSecretDto CreateSecretDto with the new Secret data.
     * @return SecretDto with the created Secret data.
     */
    public SecretDto createSecret(
            UUID ownerAccountId,
            CreateSecretDto createSecretDto) {

        SecretEntity secret = secretsMapper.fromDto(createSecretDto)
                .setOwnerAccount(new AccountEntity().setAccountId(ownerAccountId))
                .setMustRotateKey(false);

        return secretsMapper.toDto(secretsRepository.save(secret));

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
            UUID ownerAccountId,
            UUID secretId,
            EditSecretDto editSecretDto) {

        SecretEntity secret = findAndCheckSecret(secretId, ownerAccountId);

        if (secret.getMustRotateKey() && editSecretDto.getSharings() == null) {
            throw new ServiceException(String.format(
                    "Secret %s must rotate key", secretId));
        }

        if (editSecretDto.getSharings() != null) {
            if (editSecretDto.getSharings().size() != secret.getSharedSecrets().size()) {
                throw new ServiceException(String.format(
                        "Edit secret sharings do not match secret %s current sharings", secretId));
            }
            Map<String, ShareSecretDto> editSecretSharings = new HashMap<>();
            for (ShareSecretDto editSecretSharing : editSecretDto.getSharings()) {
                editSecretSharings.put(editSecretSharing.getEmail(), editSecretSharing);
            }
            for (SharedSecretEntity sharedSecret : secret.getSharedSecrets()) {
                ShareSecretDto editSecretSharing = editSecretSharings.get(
                        sharedSecret.getReceiverAccount(AccountBaseProjection.class).getEmail());
                if (editSecretSharing == null) {
                    throw new ServiceException(String.format(
                            "Edit secret sharings do not match secret %s current sharings", secretId));
                }
                sharedSecret.setEncryptedKey(editSecretSharing.getEncryptedKey());
            }
        }

        secret
                .setValue(editSecretDto.getValue())
                .setEncryptedKey(editSecretDto.getEncryptedKey())
                .setMustRotateKey(false);

        return secretsMapper.toDto(secretsRepository.save(secret));

    }

    /**
     * Deletes a Secret.
     *
     * @param ownerAccountId Account.accountId of the secret owner.
     * @param secretId ID of the Secret to be deleted.
     * @return SecretDto with the deleted Secret data.
     */
    public SecretDto deleteSecret(
            UUID ownerAccountId,
            UUID secretId) {

        SecretEntity secret = findAndCheckSecret(secretId, ownerAccountId);
        secretsRepository.delete(secret);
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
            UUID secretId,
            UUID ownerAccountId) {

        SecretEntity secret = secretsRepository.findById(secretId).orElse(null);

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
     * Finds a Secret Projection by secretId and checks if it exists and belongs to an ownerAccountId.
     *
     * @param <T> The projection type.
     * @param secretId The secretId.
     * @param ownerAccountId The accountId.
     * @param type The class of the projection.
     * @return The Secret Projection.
     */
    protected <T extends SecretBaseProjection> T findAndCheckSecret(
            UUID secretId,
            UUID ownerAccountId,
            Class<T> type) {

        SecretBaseProjection secret = null;

        if (SecretValueProjection.class.equals(type)) {

            secret = secretsRepository.findValueBySecretId(secretId);

        } else if (SecretMustRotateKeyProjection.class.equals(type)) {

            secret = secretsRepository.findMustRotateKeyBySecretId(secretId);

        } else if (SecretBaseProjection.class.equals(type)) {

            secret = secretsRepository.findBaseBySecretId(secretId);

        } else {

            throw new IllegalArgumentException("Type must extend AccountBaseProjection");

        }

        if (secret == null) {
            throw new ServiceException(String.format("Secret %s does not exist", secretId))
                    .setErrorCode("my-secrets.secret-does-not-exist");
        }

        if (ownerAccountId != null && !ownerAccountId.equals(secret.getOwnerAccount().getAccountId())) {
            throw new AuthorizationServiceException(String.format(
                    "Secret %s cannot be managed by account %s since it belongs to account %s",
                    secretId, ownerAccountId, secret.getOwnerAccount().getAccountId()));
        }

        return type.cast(secret);

    }

}
