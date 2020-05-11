package com.guardedbox.properties;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Properties starting by emails.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ConfigurationProperties(prefix = "emails")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class EmailsProperties {

    /** Property Map: emails.registration-subject. */
    private final HashMap<String, String> registrationSubject;

    /** Property Map: emails.registration-body. */
    private final HashMap<String, String> registrationBody;

    /** Property Map: emails.invitation-subject. */
    private final HashMap<String, String> invitationSubject;

    /** Property Map: emails.invitation-body. */
    private final HashMap<String, String> invitationBody;

    /** Property Map: emails.registration-complete-subject. */
    private final HashMap<String, String> registrationCompleteSubject;

    /** Property Map: emails.registration-complete-body. */
    private final HashMap<String, String> registrationCompleteBody;

    /** Property Map: emails.already-registered-subject. */
    private final HashMap<String, String> alreadyRegisteredSubject;

    /** Property Map: emails.already-registered-body. */
    private final HashMap<String, String> alreadyRegisteredBody;

    /** Property Map: emails.otp-subject. */
    private final HashMap<String, String> otpSubject;

    /** Property Map: emails.otp-body. */
    private final HashMap<String, String> otpBody;

    /** Property Map: emails.otp-incorrect-subject. */
    private final HashMap<String, String> otpIncorrectSubject;

    /** Property Map: emails.otp-incorrect-body. */
    private final HashMap<String, String> otpIncorrectBody;

}
