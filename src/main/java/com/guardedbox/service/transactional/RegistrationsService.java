package com.guardedbox.service.transactional;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_64BYTES_LENGTH;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.CreateRegistrationDto;
import com.guardedbox.dto.RegistrationDto;
import com.guardedbox.entity.RegistrationEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.RegistrationsMapper;
import com.guardedbox.properties.SecurityParametersProperties;
import com.guardedbox.repository.AccountsRepository;
import com.guardedbox.repository.RegistrationsRepository;
import com.guardedbox.service.MessagesService;
import com.guardedbox.service.RandomService;

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

    /** MessagesService. */
    private final MessagesService messagesService;

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
     * @param fromAccount AccountDto representing the inviter.
     * @return RegistrationDto with the created Registration data.
     */
    public RegistrationDto createRegistration(
            CreateRegistrationDto createRegistrationDto,
            AccountDto fromAccount) {

        long currentTime = System.currentTimeMillis();
        String email = createRegistrationDto.getEmail();
        String fromEmail = fromAccount == null ? null : fromAccount.getEmail();
        boolean emailAlreadyRegistered = accountsRepository.existsByEmail(email);
        boolean isInvitation = fromAccount != null;
        RegistrationEntity registration = null;

        // Check if the email is already registered.
        if (emailAlreadyRegistered) {

            // Check if this is an invitation.
            if (isInvitation) {

                // Throw a ServiceException indicating that the email is already registered.
                throw new ServiceException(String.format("Registration token was not generated for email %s since it is already registered", email))
                        .setErrorCode("accounts.email-already-registered").addAdditionalData("email", email);

            } else {

                // Create an invalid registration token.
                registration = createRegistrationToken(currentTime, email, fromEmail, false);

                // Send a message indicating that the email is already registered.
                messagesService.sendAlreadyRegisteredMessage(email);

            }

        } else {

            // Create a registration token.
            registration = createRegistrationToken(currentTime, email, fromEmail, true);

            // Send the invitation or registration message.
            if (isInvitation) {
                messagesService.sendInvitationMessage(email, fromEmail, registration.getToken());
            } else {
                messagesService.sendRegistrationMessage(email, registration.getToken());
            }

        }

        // Return the registration.
        return registrationsMapper.toDto(registration);

    }

    /**
     * Creates and stores in the database a registration token, checking first if a previous one was generated a short time ago.
     *
     * @param currentTime The current time.
     * @param email The email associated to the registration token.
     * @param fromEmail The inviter email in case this is an invitation, or null otherwise.
     * @param valid Boolean indicating if the registration token is valid or fake (to avoid enumeration).
     * @return The created RegistrationEntity.
     */
    private RegistrationEntity createRegistrationToken(
            long currentTime,
            String email,
            String fromEmail,
            boolean valid) {

        // Check if a registration was created for the email a short time ago.
        List<RegistrationEntity> prevRegistrations = registrationsRepository.findByEmail(email);
        for (RegistrationEntity prevRegistration : prevRegistrations) {
            if (currentTime < prevRegistration.getCreationTime().getTime() + securityParameters.getRegistrationMinTtl()) {
                throw new ServiceException(String.format(
                        "Registration token was not generated for email %s since another one was generated a short time ago", email))
                                .setErrorCode("login.register-mail-just-sent").addAdditionalData("email", email);
            }
        }

        // Generate the registration token.
        String token = null;
        do {
            token = randomService.randomAlphanumericString(ALPHANUMERIC_64BYTES_LENGTH);
        } while (registrationsRepository.existsByToken(token));

        // Store it in the database.
        return registrationsRepository.save(new RegistrationEntity()
                .setEmail(email)
                .setFromEmail(fromEmail)
                .setToken(token)
                .setCreationTime(new Timestamp(currentTime))
                .setValid(valid)
                .setConsumed(false)
                .setAccount(null));

    }

    /**
     * Finds a Registration by token and checks that it exists, is valid and is not expired.
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

        if (!registration.getValid()) {
            throw new ServiceException(String.format("Registration token %s is not valid", token))
                    .setErrorCode("registration.registration-token-invalid");
        }

        if (currentTime > registration.getCreationTime().getTime()
                + (StringUtils.isEmpty(registration.getFromEmail())
                        ? securityParameters.getRegistrationTtl()
                        : securityParameters.getInvitationTtl())) {
            throw new ServiceException(String.format("Registration token %s is expired", token))
                    .setErrorCode("registration.registration-token-expired");
        }

        return registration;

    }

}
