package com.guardedbox.service.transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.RegistrationTokenDto;
import com.guardedbox.mapper.RegistrationTokensMapper;
import com.guardedbox.repository.RegistrationTokenEntitiesRepository;

import javax.transaction.Transactional;

/**
 * Service: RegistrationToken.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
public class RegistrationTokensService {

    /** RegistrationTokenEntitiesRepository. */
    private final RegistrationTokenEntitiesRepository registrationTokenEntitiesRepository;

    /** RegistrationTokensMapper. */
    private final RegistrationTokensMapper registrationTokensMapper;

    /**
     * Constructor with Attributes.
     * 
     * @param registrationTokenEntitiesRepository RegistrationTokenEntitiesRepository.
     * @param registrationTokenMapper RegistrationTokenMapper.
     */
    public RegistrationTokensService(
            @Autowired RegistrationTokenEntitiesRepository registrationTokenEntitiesRepository,
            @Autowired RegistrationTokensMapper registrationTokenMapper) {
        this.registrationTokenEntitiesRepository = registrationTokenEntitiesRepository;
        this.registrationTokensMapper = registrationTokenMapper;
    }

    /**
     * @param token RegistrationToken.token.
     * @return Boolean indicating if a RegistrationToken corresponding to the introduced token exists.
     */
    public boolean existsToken(
            String token) {
        return registrationTokenEntitiesRepository.existsByToken(token);
    }

    /**
     * @param email RegistrationToken.email.
     * @return The RegistrationTokenDto corresponding to the introduced email.
     */
    public RegistrationTokenDto getRegistrationTokenByEmail(
            String email) {
        return registrationTokensMapper.toDto(registrationTokenEntitiesRepository.findByEmail(email));
    }

    /**
     * @param token RegistrationToken.token.
     * @return The RegistrationTokenDto corresponding to the introduced token.
     */
    public RegistrationTokenDto getRegistrationTokenByToken(
            String token) {
        return registrationTokensMapper.toDto(registrationTokenEntitiesRepository.findByToken(token));
    }

    /**
     * Saves a RegistrationToken.
     * 
     * @param registrationTokenDto RegistrationTokenDto with the RegistrationToken data.
     */
    public void saveRegistrationToken(
            RegistrationTokenDto registrationTokenDto) {
        registrationTokenEntitiesRepository.save(registrationTokensMapper.fromDto(registrationTokenDto));
    }

    /**
     * Deletes a RegistrationToken.
     * 
     * @param registrationTokenDto RegistrationTokenDto with the RegistrationToken to be deleted data.
     */
    public void deleteRegistrationToken(
            RegistrationTokenDto registrationTokenDto) {
        registrationTokenEntitiesRepository.delete(registrationTokenDto.getRegistrationTokenId());
    }

}
