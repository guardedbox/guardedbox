package com.guardedbox.service;

import static com.guardedbox.constants.SecurityParameters.CHALLENGE_LENGTH;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.ChallengeDto;
import com.guardedbox.dto.MinedChallengeResponseDto;
import com.guardedbox.dto.SignedChallengeResponseDto;

/**
 * Challenge Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class ChallengeService {

    /** Property: security-parameters.challenge.ttl. */
    private final long challengeTtl;

    /** RandomService. */
    private final RandomService randomService;

    /** SignatureVerificationService. */
    private final SignatureVerificationService signatureVerificationService;

    /** MiningVerificationService. */
    private final MiningVerificationService miningVerificationService;

    /**
     * Constructor with Attributes.
     *
     * @param challengeTtl Property: security-parameters.challenge.ttl.
     * @param randomService RandomService.
     * @param signatureVerificationService SignatureVerificationService.
     * @param miningVerificationService MiningVerificationService.
     */
    public ChallengeService(
            @Value("${security-parameters.challenge.ttl}") long challengeTtl,
            @Autowired RandomService randomService,
            @Autowired SignatureVerificationService signatureVerificationService,
            @Autowired MiningVerificationService miningVerificationService) {
        this.challengeTtl = challengeTtl;
        this.randomService = randomService;
        this.signatureVerificationService = signatureVerificationService;
        this.miningVerificationService = miningVerificationService;
    }

    /**
     * @return A challenge object with the challenge in base64 format and its expiration time.
     */
    public ChallengeDto generateChallenge() {

        long currentTime = System.currentTimeMillis();

        ChallengeDto challengeDto = new ChallengeDto();
        challengeDto.setChallenge(randomService.randomBytesBase64(CHALLENGE_LENGTH));
        challengeDto.setExpirationTime(currentTime + challengeTtl);

        return challengeDto;

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

    /**
     * Verifies a mined challenge response.
     *
     * @param minedChallengeResponseDto Mined challenge response object.
     * @param challengeDto Challenge object, as it is returned by the method generateChallenge.
     * @return Boolean indicating if the mined challenge response is valid.
     */
    public boolean verifyMinedChallengeResponse(
            MinedChallengeResponseDto minedChallengeResponseDto,
            ChallengeDto challengeDto) {

        // Verify expiration time.
        long currentTime = System.currentTimeMillis();

        if (challengeDto.getExpirationTime() <= currentTime) {
            return false;
        }

        // Verify mining.
        if (!miningVerificationService.verifyMining(
                Base64.getDecoder().decode(challengeDto.getChallenge()),
                Base64.getDecoder().decode(minedChallengeResponseDto.getMinedChallengeResponse()))) {
            return false;
        }

        return true;

    }

}
