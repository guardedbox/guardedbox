package com.guardedbox.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.CreateSecretDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.entity.SecretWithOwnerAccountEncryptionPublicKeyEntity;

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

        if (secretEntity == null)
            return null;

        SecretDto secretDto = new SecretDto();

        secretDto.setSecretId(secretEntity.getSecretId());
        secretDto.setName(secretEntity.getName());
        secretDto.setValue(secretEntity.getValue());

        return secretDto;

    }

    /**
     * Maps a Secret Entity to DTO.
     *
     * @param secretEntity The Secret Entity.
     * @return The Secret DTO.
     */
    public SecretDto toDto(
            SecretWithOwnerAccountEncryptionPublicKeyEntity secretEntity) {

        if (secretEntity == null)
            return null;

        SecretDto secretDto = new SecretDto();

        secretDto.setSecretId(secretEntity.getSecretId());
        secretDto.setName(secretEntity.getName());
        secretDto.setValue(secretEntity.getValue());

        return secretDto;

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

        if (secretDto == null)
            return null;

        SecretEntity secretEntity = new SecretEntity();

        secretEntity.setName(secretDto.getName());
        secretEntity.setValue(secretDto.getValue());

        return secretEntity;

    }

}
