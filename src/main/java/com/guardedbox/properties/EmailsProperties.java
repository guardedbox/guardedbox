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

    /** Property Map: emails.otp-subject. */
    private final HashMap<String, String> otpSubject;

    /** Property Map: emails.otp-body. */
    private final HashMap<String, String> otpBody;

    /** Property Map: emails.registration-subject. */
    private final HashMap<String, String> registrationSubject;

    /** Property Map: emails.registration-body. */
    private final HashMap<String, String> registrationBody;

    /** Property Map: emails.already-registered-subject. */
    private final HashMap<String, String> alreadyRegisteredSubject;

    /** Property Map: emails.already-registered-body. */
    private final HashMap<String, String> alreadyRegisteredBody;

}
