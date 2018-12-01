package com.guardedbox.service.transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.DeleteSecretDto;
import com.guardedbox.dto.EditSecretDto;
import com.guardedbox.dto.EditSecretSharingDto;
import com.guardedbox.dto.NewSecretDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.entity.SharedSecretEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.SecretsMapper;
import com.guardedbox.repository.SecretEntitiesRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

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

    /** SecretsMapper. */
    private final SecretsMapper secretsMapper;

    /**
     * Constructor with Attributes.
     * 
     * @param secretEntitiesRepository SecretEntitiesRepository.
     * @param secretsMapper SecretsMapper.
     */
    public SecretsService(
            @Autowired SecretEntitiesRepository secretEntitiesRepository,
            @Autowired SecretsMapper secretsMapper) {
        this.secretEntitiesRepository = secretEntitiesRepository;
        this.secretsMapper = secretsMapper;
    }

    /**
     * @param accountId Account.accountId.
     * @return The List of SecretDtos corresponding to the introduced accountId.
     */
    public List<SecretDto> getAllAccountSecrets(
            Long accountId) {
        return secretsMapper.toDto(secretEntitiesRepository.findByAccountAccountIdOrderByNameAsc(accountId));
    }

    /**
     * Creates a Secret.
     * 
     * @param accountId Account.accountId.
     * @param newSecretDto NewSecretDto with the new Secret data.
     * @return SecretDto with the new Secret data.
     */
    public SecretDto newSecret(
            Long accountId,
            NewSecretDto newSecretDto) {
        SecretEntity secret = secretsMapper.fromDto(newSecretDto);
        secret.setAccount(new AccountEntity(accountId));
        return secretsMapper.toDto(secretEntitiesRepository.save(secret));
    }

    /**
     * Edits a Secret.
     * 
     * @param accountId Account.accountId.
     * @param editSecretDto EditSecretDto with the Secret new data.
     * @return SecretDto with the edited Secret data.
     */
    public SecretDto editSecret(
            Long accountId,
            EditSecretDto editSecretDto) {

        SecretEntity secret = findAndCheckSecret(editSecretDto.getSecretId(), accountId);

        secret.setName(editSecretDto.getName());
        secret.setValue(editSecretDto.getValue());

        if (editSecretDto.getSharings().size() != secret.getSharedSecrets().size()) {
            throw new ServiceException(String.format(
                    "Edit secret sharings do not match secret %s current sharings", editSecretDto.getSecretId()));
        }
        Map<String, EditSecretSharingDto> editSecretSharings = new HashMap<>();
        for (EditSecretSharingDto editSecretSharing : editSecretDto.getSharings()) {
            editSecretSharings.put(editSecretSharing.getEmail(), editSecretSharing);
        }
        for (SharedSecretEntity sharedSecret : secret.getSharedSecrets()) {
            EditSecretSharingDto editSecretSharing = editSecretSharings.get(sharedSecret.getAccount().getEmail());
            if (editSecretSharing == null) {
                throw new ServiceException(String.format(
                        "Edit secret sharings do not match secret %s current sharings", editSecretDto.getSecretId()));
            }
            sharedSecret.setValue(editSecretSharing.getValue());
        }

        return secretsMapper.toDto(secretEntitiesRepository.save(secret));

    }

    /**
     * Deletes a Secret.
     * 
     * @param accountId Account.accountId.
     * @param deleteSecretDto DeleteSecretDto with the Secret to be deleted data.
     * @return SecretDto with the deleted Secret data.
     */
    public SecretDto deleteSecret(
            Long accountId,
            DeleteSecretDto deleteSecretDto) {
        SecretEntity secret = findAndCheckSecret(deleteSecretDto.getSecretId(), accountId);
        secretEntitiesRepository.delete(secret);
        return secretsMapper.toDto(secret);
    }

    /**
     * Finds a Secret by secretId and checks if it exists and belongs to an accoundId.
     * 
     * @param secretId The secretId.
     * @param accountId The accoundId.
     * @return The Secret.
     */
    protected SecretEntity findAndCheckSecret(
            Long secretId,
            Long accountId) {

        SecretEntity secret = secretEntitiesRepository.findOne(secretId);

        if (secret == null) {
            throw new ServiceException(String.format("Secret %s does not exist", secretId))
                    .setErrorCode("my-secrets.secret-does-not-exist");
        }

        if (accountId != null && !accountId.equals(secret.getAccount().getAccountId())) {
            throw new AuthorizationServiceException(String.format(
                    "Secret %s cannot be managed by account %s since it belongs to account %s",
                    secretId, accountId, secret.getAccount().getAccountId()));
        }

        return secret;

    }

}
