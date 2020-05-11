package com.guardedbox.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Properties starting by security-parameters.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ConfigurationProperties(prefix = "security-parameters")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class SecurityParametersProperties {

    /** Property: security-parameters.registration-ttl. */
    private final Long registrationTtl;

    /** Property: security-parameters.registration-min-ttl. */
    private final Long registrationMinTtl;

    /** Property: security-parameters.invitation-ttl. */
    private final Long invitationTtl;

    /** Property: security-parameters.registration-execution-time. */
    private final Long registrationExecutionTime;

    /** Property: security-parameters.login-salt-execution-time. */
    private final Long loginSaltExecutionTime;

    /** Property: security-parameters.challenge-length. */
    private final Integer challengeLength;

    /** Property: security-parameters.challenge-ttl. */
    private final Long challengeTtl;

    /** Property: security-parameters.challenge-execution-time. */
    private final Long challengeExecutionTime;

    /** Property: security-parameters.otp-length. */
    private final Integer otpLength;

    /** Property: security-parameters.otp-ttl. */
    private final Long otpTtl;

    /** Property: security-parameters.otp-execution-time. */
    private final Long otpExecutionTime;

    /** Property: security-parameters.login-execution-time. */
    private final Long loginExecutionTime;

}
