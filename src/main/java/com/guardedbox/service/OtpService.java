package com.guardedbox.service;

import static com.guardedbox.constants.SecurityParameters.OTP_LENGTH;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.OtpDto;
import com.guardedbox.dto.OtpResponseDto;

import lombok.RequiredArgsConstructor;

/**
 * One Time Password Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@PropertySource("classpath:email/email_en.properties")
@RequiredArgsConstructor
public class OtpService {

    /** Property: security-parameters.otp.ttl. */
    @Value("${security-parameters.otp.ttl}")
    private final long otpTtl;

    /** Property: otp.email.subject. */
    @Value("${otp.email.subject}")
    private final String otpEmailSubject;

    /** Property: otp.email.body. */
    @Value("${otp.email.body}")
    private final String otpEmailBody;

    /** RandomService. */
    private final RandomService randomService;

    /** EmailService. */
    private final EmailService emailService;

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
                otpEmailSubject,
                String.format(otpEmailBody, otp));

        OtpDto otpDto = new OtpDto();
        otpDto.setEmail(email);
        otpDto.setOtp(passwordEncoder.encode(otp));
        otpDto.setExpirationTime(currentTime + otpTtl);

        return otpDto;

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
