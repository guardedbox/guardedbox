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

        if (registrationEntity == null)
            return null;

        RegistrationDto registrationDto = new RegistrationDto();

        registrationDto.setRegistrationId(registrationEntity.getRegistrationId());
        registrationDto.setEmail(registrationEntity.getEmail());
        registrationDto.setToken(registrationEntity.getToken());
        registrationDto.setExpeditionTime(registrationEntity.getExpeditionTime());

        return registrationDto;

    }

    /**
     * Maps a Registration DTO to Entity.
     *
     * @param registrationDto The Registration DTO.
     * @return The Registration Entity.
     */
    public RegistrationEntity fromDto(
            RegistrationDto registrationDto) {

        if (registrationDto == null)
            return null;

        RegistrationEntity registrationEntity = new RegistrationEntity();

        registrationEntity.setRegistrationId(registrationDto.getRegistrationId());
        registrationEntity.setEmail(registrationDto.getEmail());
        registrationEntity.setToken(registrationDto.getToken());
        registrationEntity.setExpeditionTime(registrationDto.getExpeditionTime());

        return registrationEntity;

    }

}
