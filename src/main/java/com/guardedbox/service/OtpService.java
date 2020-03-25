package com.guardedbox.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.OtpDto;
import com.guardedbox.dto.OtpResponseDto;
import com.guardedbox.properties.SecurityParametersProperties;

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

    /** SecurityParametersProperties. */
    private final SecurityParametersProperties securityParameters;

    /** RandomService. */
    private final RandomService randomService;

    /** MessagesService. */
    private final MessagesService messagesService;

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

        String otp = randomService.randomAlphanumericString(securityParameters.getOtpLength());

        messagesService.sendOtpMessage(email, otp);

        return new OtpDto()
                .setEmail(email)
                .setOtp(passwordEncoder.encode(otp))
                .setExpirationTime(currentTime + securityParameters.getOtpTtl());

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
