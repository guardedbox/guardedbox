package com.guardedbox.service;

import static com.guardedbox.constants.PathParameters.FRONT_REGISTRATION_COMPONENT_PATH;

import org.springframework.stereotype.Service;

import com.guardedbox.properties.EmailsProperties;
import com.guardedbox.properties.ServerProperties;

import lombok.RequiredArgsConstructor;

/**
 * Messages Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@RequiredArgsConstructor
public class MessagesService {

    /** EmailsProperties. */
    private final EmailsProperties emailsProperties;

    /** ServerProperties. */
    private final ServerProperties serverProperties;

    /** EmailService. */
    private final EmailService emailService;

    /** LanguageService. */
    private final LanguageService languageService;

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
                emailsProperties.getRegistrationSubject().get(languageService.getAppLanguage()),
                String.format(emailsProperties.getRegistrationBody().get(languageService.getAppLanguage()),
                        serverProperties.getExternalUrl() + String.format(FRONT_REGISTRATION_COMPONENT_PATH, token)));

    }

    /**
     * Sends an invitation message to an email, from another email, with a registration token.
     *
     * @param email The email.
     * @param fromEmail The inviter email.
     * @param token The registration token.
     */
    public void sendInvitationMessage(
            String email,
            String fromEmail,
            String token) {

        emailService.sendAsync(
                email,
                emailsProperties.getInvitationSubject().get(languageService.getAppLanguage()),
                String.format(emailsProperties.getInvitationBody().get(languageService.getAppLanguage()),
                        fromEmail,
                        serverProperties.getExternalUrl() + String.format(FRONT_REGISTRATION_COMPONENT_PATH, token)));

    }

    /**
     * Sends a message to an email indicating that the registration is complete.
     *
     * @param email The email.
     */
    public void sendRegistrationCompleteMessage(
            String email) {

        emailService.sendAsync(
                email,
                emailsProperties.getRegistrationCompleteSubject().get(languageService.getAppLanguage()),
                emailsProperties.getRegistrationCompleteBody().get(languageService.getAppLanguage()));

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
                emailsProperties.getAlreadyRegisteredSubject().get(languageService.getAppLanguage()),
                emailsProperties.getAlreadyRegisteredBody().get(languageService.getAppLanguage()));

    }

    /**
     * Sends a one time password message to an email.
     *
     * @param email The email.
     * @param otp The one time password.
     */
    public void sendOtpMessage(
            String email,
            String otp) {

        emailService.sendAsync(
                email,
                emailsProperties.getOtpSubject().get(languageService.getAppLanguage()),
                String.format(emailsProperties.getOtpBody().get(languageService.getAppLanguage()), otp));

    }

    /**
     * Sends a message to an email indicating that the introduced one time password is incorrect.
     *
     * @param email The email.
     */
    public void sendOtpIncorrectMessage(
            String email) {

        emailService.sendAsync(
                email,
                emailsProperties.getOtpIncorrectSubject().get(languageService.getAppLanguage()),
                emailsProperties.getOtpIncorrectBody().get(languageService.getAppLanguage()));

    }

}
