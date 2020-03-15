package com.guardedbox.service;

import static com.guardedbox.constants.FrontParameters.FRONT_REGISTRATION_COMPONENT_URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guardedbox.properties.EmailsProperties;
import com.guardedbox.properties.LanguageProperties;

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

    /** Property: internet.url. */
    @Value("${internet.url}")
    private final String internetUrl;

    /** EmailsProperties. */
    private final EmailsProperties emailsProperties;

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
                emailsProperties.getAlreadyRegisteredSubject().get(languageProperties.getDefaultLanguage()),
                String.format(emailsProperties.getAlreadyRegisteredBody().get(languageProperties.getDefaultLanguage()),
                        internetUrl + String.format(FRONT_REGISTRATION_COMPONENT_URI, token)));

    }

}
