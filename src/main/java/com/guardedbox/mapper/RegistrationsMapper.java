package com.guardedbox.mapper;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.RegistrationDto;
import com.guardedbox.entity.RegistrationEntity;

/**
 * Mapper: Registration.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class RegistrationsMapper {

    /**
     * Maps a Registration Entity to DTO.
     *
     * @param registrationEntity The Registration Entity.
     * @return The Registration DTO.
     */
    public RegistrationDto toDto(
            RegistrationEntity registrationEntity) {

        return registrationEntity == null ? null : new RegistrationDto()
                .setRegistrationId(registrationEntity.getRegistrationId())
                .setEmail(registrationEntity.getEmail())
                .setToken(registrationEntity.getToken())
                .setExpeditionTime(registrationEntity.getExpeditionTime());

    }

    /**
     * Maps a Registration DTO to Entity.
     *
     * @param registrationDto The Registration DTO.
     * @return The Registration Entity.
     */
    public RegistrationEntity fromDto(
            RegistrationDto registrationDto) {

        return registrationDto == null ? null : new RegistrationEntity()
                .setRegistrationId(registrationDto.getRegistrationId())
                .setEmail(registrationDto.getEmail())
                .setToken(registrationDto.getToken())
                .setExpeditionTime(registrationDto.getExpeditionTime());

    }

}
