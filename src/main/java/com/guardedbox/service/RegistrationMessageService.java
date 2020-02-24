package com.guardedbox.service;

import static com.guardedbox.constants.FrontParameters.FRONT_REGISTRATION_COMPONENT_URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Registration Token Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@PropertySource("classpath:email/email_en.properties")
@RequiredArgsConstructor
public class RegistrationMessageService {

    /** Property: internet.url. */
    @Value("${internet.url}")
    private final String internetUrl;

    /** Property: registration.email.subject. */
    @Value("${registration.email.subject}")
    private final String registrationEmailSubject;

    /** Property: registration.email.body. */
    @Value("${registration.email.body}")
    private final String registrationEmailBody;

    /** Property: registration.alreadyRegistered.email.subject. */
    @Value("${registration.already-registered.email.subject}")
    private final String alreadyRegisteredEmailSubject;

    /** Property: registration.alreadyRegistered.email.body. */
    @Value("${registration.already-registered.email.body}")
    private final String alreadyRegisteredEmailBody;

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
                alreadyRegisteredEmailSubject,
                alreadyRegisteredEmailBody);

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
                registrationEmailSubject,
                String.format(registrationEmailBody, internetUrl + String.format(FRONT_REGISTRATION_COMPONENT_URI, token)));

    }

}
