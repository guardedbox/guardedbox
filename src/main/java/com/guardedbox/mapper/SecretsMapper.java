package com.guardedbox.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.CreateSecretDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.entity.projection.SecretBaseProjection;

/**
 * Mapper: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class SecretsMapper {

    /**
     * Maps a Secret Entity to DTO.
     *
     * @param secretEntity The Secret Entity.
     * @return The Secret DTO.
     */
    public SecretDto toDto(
            SecretEntity secretEntity) {

        return secretEntity == null ? null : new SecretDto()
                .setSecretId(secretEntity.getSecretId())
                .setValue(secretEntity.getValue())
                .setEncryptedKey(secretEntity.getEncryptedKey())
                .setNumberOfSharings(secretEntity.getSharedSecrets() == null ? null : secretEntity.getSharedSecrets().size())
                .setWasShared(secretEntity.getWasShared());

    }

    /**
     * Maps a Secret Projection to DTO.
     *
     * @param secretProjection The Secret Projection.
     * @return The Secret DTO.
     */
    public SecretDto toDto(
            SecretBaseProjection secretProjection) {

        if (secretProjection == null)
            return null;

        SecretDto secretDto = new SecretDto()
                .setSecretId(secretProjection.getSecretId())
                .setValue(secretProjection.getValue());

        if (secretProjection instanceof SecretBaseProjection) {

            return secretDto;

        } else {

            throw new IllegalArgumentException("Type must extend SecretBaseProjection");

        }

    }

    /**
     * Maps a List of Secret Entities to List of Secret DTOs.
     *
     * @param secretEntities The List of Secret Entities.
     * @return The List of Secret DTOs.
     */
    public List<SecretDto> toDto(
            List<SecretEntity> secretEntities) {

        if (secretEntities == null)
            return null;

        List<SecretDto> secretDtos = new ArrayList<>(secretEntities.size());
        for (SecretEntity secretEntity : secretEntities)
            secretDtos.add(toDto(secretEntity));

        return secretDtos;

    }

    /**
     * Maps a Secret DTO to Entity.
     *
     * @param secretDto The Secret DTO.
     * @return The Secret Entity.
     */
    public SecretEntity fromDto(
            CreateSecretDto secretDto) {

        return secretDto == null ? null : new SecretEntity()
                .setValue(secretDto.getValue())
                .setEncryptedKey(secretDto.getEncryptedKey());

    }

}
