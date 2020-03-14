package com.guardedbox.service;

import static com.guardedbox.constants.FrontParameters.FRONT_REGISTRATION_COMPONENT_URI;
import static com.guardedbox.constants.LanguageParameters.DEFAULT_LANG;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guardedbox.properties.EmailsProperties;

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

    /** EmailService. */
    private final EmailService emailService;

    /** EmailsProperties. */
    private final EmailsProperties emailsProperties;

    /**
     * Sends a message to an email indicating that it is already registered.
     *
     * @param email The email.
     */
    public void sendAlreadyRegisteredMessage(
            String email) {

        emailService.sendAsync(
                email,
                emailsProperties.getAlreadyRegisteredSubject().get(DEFAULT_LANG),
                emailsProperties.getAlreadyRegisteredBody().get(DEFAULT_LANG));

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
                emailsProperties.getAlreadyRegisteredSubject().get(DEFAULT_LANG),
                String.format(emailsProperties.getAlreadyRegisteredBody().get(DEFAULT_LANG),
                        internetUrl + String.format(FRONT_REGISTRATION_COMPONENT_URI, token)));

    }

}
