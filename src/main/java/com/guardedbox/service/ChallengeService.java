package com.guardedbox.service;

import java.util.Base64;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.ChallengeDto;
import com.guardedbox.dto.SignedChallengeResponseDto;
import com.guardedbox.properties.SecurityParametersProperties;

import lombok.RequiredArgsConstructor;

/**
 * Challenge Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@RequiredArgsConstructor
public class ChallengeService {

    /** SecurityParametersProperties. */
    private final SecurityParametersProperties securityParameters;

    /** RandomService. */
    private final RandomService randomService;

    /** SignatureVerificationService. */
    private final SignatureVerificationService signatureVerificationService;

    /**
     * @return A challenge object with the challenge in base64 format and its expiration time.
     */
    public ChallengeDto generateChallenge() {

        long currentTime = System.currentTimeMillis();

        return new ChallengeDto()
                .setChallenge(randomService.randomBytesBase64(securityParameters.getChallengeLength()))
                .setExpirationTime(currentTime + securityParameters.getChallengeTtl());

    }

    /**
     * Verifies a signed challenge response.
     *
     * @param signedChallengeResponseDto Signed challenge response object.
     * @param challengeDto Challenge object, as it is returned by the method generateChallenge.
     * @return Boolean indicating if the signed challenge response is valid.
     */
    public boolean verifySignedChallengeResponse(
            SignedChallengeResponseDto signedChallengeResponseDto,
            ChallengeDto challengeDto) {

        // Verify expiration time.
        long currentTime = System.currentTimeMillis();

        if (challengeDto.getExpirationTime() <= currentTime) {
            return false;
        }

        // Verify signature.
        if (!signatureVerificationService.verifySignature(
                Base64.getDecoder().decode(challengeDto.getChallenge()),
                Base64.getDecoder().decode(signedChallengeResponseDto.getSignedChallengeResponse()),
                signedChallengeResponseDto.getEmail())) {
            return false;
        }

        return true;

    }

}
