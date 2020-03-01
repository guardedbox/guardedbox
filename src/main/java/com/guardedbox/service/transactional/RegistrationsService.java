package com.guardedbox.service.transactional;

import static com.guardedbox.constants.SecurityParameters.REGISTRATION_TOKEN_LENGTH;

import java.sql.Timestamp;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.CreateRegistrationDto;
import com.guardedbox.dto.RegistrationDto;
import com.guardedbox.entity.RegistrationEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.RegistrationsMapper;
import com.guardedbox.repository.RegistrationEntitiesRepository;
import com.guardedbox.service.RandomService;
import com.guardedbox.service.RegistrationMessageService;

import lombok.RequiredArgsConstructor;

/**
 * Service: Registration.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationsService {

    /** Property: security-parameters.registration.ttl. */
    @Value("${security-parameters.registration.ttl}")
    private final long registrationTtl;

    /** Property: security-parameters.registration.min-ttl. */
    @Value("${security-parameters.registration.min-ttl}")
    private final long registrationMinTtl;

    /** RegistrationEntitiesRepository. */
    private final RegistrationEntitiesRepository registrationEntitiesRepository;

    /** RegistrationsMapper. */
    private final RegistrationsMapper registrationsMapper;

    /** AccountsService. */
    private final AccountsService accountsService;

    /** RegistrationMessageService. */
    private final RegistrationMessageService registrationMessageService;

    /** RandomService. */
    private final RandomService randomService;

    /**
     * @param token Registration.token.
     * @return The RegistrationDto corresponding to the introduced token. Checks if it exists and is not expired.
     */
    public RegistrationDto getAndCheckRegistrationByToken(
            String token) {

        return registrationsMapper.toDto(findAndCheckRegistrationByToken(token));

    }

    /**
     * Creates a Registration.
     *
     * @param createRegistrationDto CreateRegistrationDto with the new Registration data.
     * @return RegistrationDto with the created Registration data.
     */
    public RegistrationDto createRegistration(
            CreateRegistrationDto createRegistrationDto) {

        long currentTime = System.currentTimeMillis();

        // Check if a registration was created for the email a short time ago.
        RegistrationEntity prevRegistration = registrationEntitiesRepository.findByEmail(createRegistrationDto.getEmail());
        if (prevRegistration != null && currentTime < prevRegistration.getExpeditionTime().getTime() + registrationMinTtl) {
            throw new ServiceException(String.format(
                    "Registration token was not generated for email %s since another one was generated a short time ago",
                    createRegistrationDto.getEmail()))
                            .setErrorCode("login.register-mail-just-sent").addAdditionalData("email", createRegistrationDto.getEmail());
        }

        // Check if the email is already registered.
        if (accountsService.existsAccountByEmail(createRegistrationDto.getEmail())) {

            // Send a message indicating that the email is already registered.
            registrationMessageService.sendAlreadyRegisteredMessage(createRegistrationDto.getEmail());

            throw new ServiceException(String.format(
                    "Registration token was not generated for email %s since it is already registered",
                    createRegistrationDto.getEmail()))
                            .setErrorCode("accounts.email-already-registered").addAdditionalData("email", createRegistrationDto.getEmail())
                            .setResponseAsSuccess(true);

        } else {

            // Generate the registration token.
            String token = null;
            do {
                token = randomService.randomAlphanumericString(REGISTRATION_TOKEN_LENGTH);
            } while (registrationEntitiesRepository.existsByToken(token));

            // Send the registration message.
            registrationMessageService.sendRegistrationMessage(createRegistrationDto.getEmail(), token);

            // Store the registration in the database, overwriting the previous one in case it exists.
            RegistrationEntity registration = (prevRegistration == null ? new RegistrationEntity() : prevRegistration)
                    .setEmail(createRegistrationDto.getEmail())
                    .setToken(token)
                    .setExpeditionTime(new Timestamp(currentTime));
            registrationEntitiesRepository.save(registration);

            // Return the registration.
            return registrationsMapper.toDto(registration);

        }

    }

    /**
     * Deletes a Registration.
     *
     * @param registrationId ID of the Registration to be deleted.
     */
    public void deleteRegistration(
            Long registrationId) {

        registrationEntitiesRepository.deleteById(registrationId);

    }

    /**
     * Finds a Registration by token and checks that it exists and is not expired.
     *
     * @param token Registration.token.
     * @return The RegistrationEntity corresponding to the introduced token.
     */
    protected RegistrationEntity findAndCheckRegistrationByToken(
            String token) {

        long currentTime = System.currentTimeMillis();

        RegistrationEntity registration = registrationEntitiesRepository.findByToken(token);

        if (registration == null) {
            throw new ServiceException(String.format("Registration token %s does not exist", token))
                    .setErrorCode("registration.registration-token-not-found");
        }

        if (currentTime > registration.getExpeditionTime().getTime() + registrationTtl) {
            throw new ServiceException(String.format("Registration token %s is expired", token))
                    .setErrorCode("registration.registration-token-expired");
        }

        return registration;

    }

}
