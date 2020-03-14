package com.guardedbox.service;

import static com.guardedbox.constants.LanguageParameters.DEFAULT_LANG;
import static com.guardedbox.constants.SecurityParameters.OTP_LENGTH;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.OtpDto;
import com.guardedbox.dto.OtpResponseDto;
import com.guardedbox.properties.EmailsProperties;

import lombok.RequiredArgsConstructor;

/**
 * One Time Password Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@RequiredArgsConstructor
public class OtpService {

    /** Property: security-parameters.otp.ttl. */
    @Value("${security-parameters.otp.ttl}")
    private final long otpTtl;

    /** RandomService. */
    private final RandomService randomService;

    /** EmailService. */
    private final EmailService emailService;

    /** EmailsProperties. */
    private final EmailsProperties emailsProperties;

    /** PasswordEncoder. */
    private final PasswordEncoder passwordEncoder;

    /**
     * Generates a one time password for an email and sends it.
     *
     * @param email The email.
     * @return A one time password object, with the password encoded with the PasswordEncoder, its expiration time, and its associated email.
     */
    public OtpDto generateAndSendOtp(
            String email) {

        long currentTime = System.currentTimeMillis();

        String otp = randomService.randomAlphanumericString(OTP_LENGTH);

        emailService.sendAsync(
                email,
                emailsProperties.getOtpSubject().get(DEFAULT_LANG),
                String.format(emailsProperties.getOtpBody().get(DEFAULT_LANG), otp));

        return new OtpDto()
                .setEmail(email)
                .setOtp(passwordEncoder.encode(otp))
                .setExpirationTime(currentTime + otpTtl);

    }

    /**
     * Verifies a one time password response.
     *
     * @param otpResponseDto One time password response object.
     * @param otpDto One time password object, as it is returned by the method generateAndSendOtp.
     * @return Boolean indicating if the one time password response is valid.
     */
    public boolean verifyOtp(
            OtpResponseDto otpResponseDto,
            OtpDto otpDto) {

        // Verify expiration time.
        long currentTime = System.currentTimeMillis();

        if (otpDto.getExpirationTime() <= currentTime) {
            return false;
        }

        // Verify one time password.
        if (!passwordEncoder.matches(otpResponseDto.getOtp(), otpDto.getOtp())) {
            return false;
        }

        return true;

    }

}
