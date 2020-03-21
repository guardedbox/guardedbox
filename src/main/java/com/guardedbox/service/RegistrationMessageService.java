package com.guardedbox.service;

import static com.guardedbox.constants.PathParameters.FRONT_REGISTRATION_COMPONENT_PATH;

import org.springframework.stereotype.Service;

import com.guardedbox.properties.EmailsProperties;
import com.guardedbox.properties.LanguageProperties;
import com.guardedbox.properties.ServerProperties;

import lombok.RequiredArgsConstructor;

/**
 * Registration Token Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@RequiredArgsConstructor
public class RegistrationMessageService {

    /** EmailsProperties. */
    private final EmailsProperties emailsProperties;

    /** ServerProperties. */
    private final ServerProperties serverProperties;

    /** LanguageProperties. */
    private final LanguageProperties languageProperties;

    /** EmailService. */
    private final EmailService emailService;

    /**
     * Sends a message to an email indicating that it is already registered.
     *
     * @param email The email.
     */
    public void sendAlreadyRegisteredMessage(
            String email) {

        emailService.sendAsync(
                email,
                emailsProperties.getAlreadyRegisteredSubject().get(languageProperties.getDefaultLanguage()),
                emailsProperties.getAlreadyRegisteredBody().get(languageProperties.getDefaultLanguage()));

    }

    /**
     * Sends a registration message to an email, with a registration token.
     *
     * @param email The email.
     * @param token The registration token.
     */
    public void sendRegistrationMessage(
            String email,
            String token) {

        emailService.sendAsync(
                email,
                emailsProperties.getRegistrationSubject().get(languageProperties.getDefaultLanguage()),
                String.format(emailsProperties.getRegistrationBody().get(languageProperties.getDefaultLanguage()),
                        serverProperties.getExternalUrl() + String.format(FRONT_REGISTRATION_COMPONENT_PATH, token)));

    }

}
