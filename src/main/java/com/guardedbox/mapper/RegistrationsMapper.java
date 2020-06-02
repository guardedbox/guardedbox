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
                .setToken(registrationEntity.getToken());

    }

}
