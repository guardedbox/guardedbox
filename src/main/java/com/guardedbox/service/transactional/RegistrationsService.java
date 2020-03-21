package com.guardedbox.service.transactional;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_64BYTES_LENGTH;

import java.sql.Timestamp;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.CreateRegistrationDto;
import com.guardedbox.dto.RegistrationDto;
import com.guardedbox.entity.RegistrationEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.RegistrationsMapper;
import com.guardedbox.properties.SecurityParametersProperties;
import com.guardedbox.repository.AccountsRepository;
import com.guardedbox.repository.RegistrationsRepository;
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

    /** SecurityParametersProperties. */
    private final SecurityParametersProperties securityParameters;

    /** RegistrationsRepository. */
    private final RegistrationsRepository registrationsRepository;

    /** AccountsRepository. */
    private final AccountsRepository accountsRepository;

    /** RegistrationsMapper. */
    private final RegistrationsMapper registrationsMapper;

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
        RegistrationEntity prevRegistration = registrationsRepository.findByEmail(createRegistrationDto.getEmail());
        if (prevRegistration != null && currentTime < prevRegistration.getExpeditionTime().getTime() + securityParameters.getRegistrationMinTtl()) {
            throw new ServiceException(String.format(
                    "Registration token was not generated for email %s since another one was generated a short time ago",
                    createRegistrationDto.getEmail()))
                            .setErrorCode("login.register-mail-just-sent").addAdditionalData("email", createRegistrationDto.getEmail());
        }

        // Check if the email is already registered.
        if (accountsRepository.existsByEmail(createRegistrationDto.getEmail())) {

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
                token = randomService.randomAlphanumericString(ALPHANUMERIC_64BYTES_LENGTH);
            } while (registrationsRepository.existsByToken(token));

            // Send the registration message.
            registrationMessageService.sendRegistrationMessage(createRegistrationDto.getEmail(), token);

            // Store the registration in the database, overwriting the previous one in case it exists.
            RegistrationEntity registration = (prevRegistration == null ? new RegistrationEntity() : prevRegistration)
                    .setEmail(createRegistrationDto.getEmail())
                    .setToken(token)
                    .setExpeditionTime(new Timestamp(currentTime));
            registrationsRepository.save(registration);

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
            UUID registrationId) {

        registrationsRepository.deleteById(registrationId);

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

        RegistrationEntity registration = registrationsRepository.findByToken(token);

        if (registration == null) {
            throw new ServiceException(String.format("Registration token %s does not exist", token))
                    .setErrorCode("registration.registration-token-not-found");
        }

        if (currentTime > registration.getExpeditionTime().getTime() + securityParameters.getRegistrationTtl()) {
            throw new ServiceException(String.format("Registration token %s is expired", token))
                    .setErrorCode("registration.registration-token-expired");
        }

        return registration;

    }

}
