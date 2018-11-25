package com.guardedbox.mapper;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.RegistrationTokenDto;
import com.guardedbox.entity.RegistrationTokenEntity;

/**
 * Mapper: RegistrationToken.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class RegistrationTokensMapper {

    /**
     * Maps a RegistrationToken Entity to DTO.
     * 
     * @param registrationTokenEntity The RegistrationToken Entity.
     * @return The RegistrationToken DTO.
     */
    public RegistrationTokenDto toDto(
            RegistrationTokenEntity registrationTokenEntity) {

        if (registrationTokenEntity == null)
            return null;

        RegistrationTokenDto registrationTokenDto = new RegistrationTokenDto();

        registrationTokenDto.setRegistrationTokenId(registrationTokenEntity.getRegistrationTokenId());
        registrationTokenDto.setEmail(registrationTokenEntity.getEmail());
        registrationTokenDto.setToken(registrationTokenEntity.getToken());
        registrationTokenDto.setExpeditionTime(registrationTokenEntity.getExpeditionTime());

        return registrationTokenDto;

    }

    /**
     * Maps a RegistrationToken DTO to Entity.
     * 
     * @param registrationTokenDto The RegistrationToken DTO.
     * @return The RegistrationToken Entity.
     */
    public RegistrationTokenEntity fromDto(
            RegistrationTokenDto registrationTokenDto) {

        if (registrationTokenDto == null)
            return null;

        RegistrationTokenEntity registrationTokenEntity = new RegistrationTokenEntity();

        registrationTokenEntity.setRegistrationTokenId(registrationTokenDto.getRegistrationTokenId());
        registrationTokenEntity.setEmail(registrationTokenDto.getEmail());
        registrationTokenEntity.setToken(registrationTokenDto.getToken());
        registrationTokenEntity.setExpeditionTime(registrationTokenDto.getExpeditionTime());

        return registrationTokenEntity;

    }

}
