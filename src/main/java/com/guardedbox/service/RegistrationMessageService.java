package com.guardedbox.service;

import static com.guardedbox.constants.FrontParameters.FRONT_REGISTRATION_COMPONENT_URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * Registration Token Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@PropertySource("classpath:email/email_en.properties")
public class RegistrationMessageService {

    /** Property: internet.url. */
    private final String internetUrl;

    /** Property: registration.email.subject. */
    private final String registrationEmailSubject;

    /** Property: registration.email.body. */
    private final String registrationEmailBody;

    /** Property: registration.alreadyRegistered.email.subject. */
    private final String alreadyRegisteredEmailSubject;

    /** Property: registration.alreadyRegistered.email.body. */
    private final String alreadyRegisteredEmailBody;

    /** EmailService. */
    private final EmailService emailService;

    /**
     * Constructor with Attributes.
     *
     * @param internetUrl Property: internet.url.
     * @param registrationEmailSubject Property: registration.email.subject.
     * @param registrationEmailBody Property: registration.email.body.
     * @param alreadyRegisteredEmailSubject Property: registration.alreadyRegistered.email.subject.
     * @param alreadyRegisteredEmailBody Property: registration.alreadyRegistered.email.body.
     * @param emailService EmailService.
     */
    public RegistrationMessageService(
            @Value("${internet.url}") String internetUrl,
            @Value("${registration.email.subject}") String registrationEmailSubject,
            @Value("${registration.email.body}") String registrationEmailBody,
            @Value("${registration.already-registered.email.subject}") String alreadyRegisteredEmailSubject,
            @Value("${registration.already-registered.email.body}") String alreadyRegisteredEmailBody,
            @Autowired EmailService emailService) {
        this.internetUrl = internetUrl;
        this.registrationEmailSubject = registrationEmailSubject;
        this.registrationEmailBody = registrationEmailBody;
        this.alreadyRegisteredEmailSubject = alreadyRegisteredEmailSubject;
        this.alreadyRegisteredEmailBody = alreadyRegisteredEmailBody;
        this.emailService = emailService;
    }

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
