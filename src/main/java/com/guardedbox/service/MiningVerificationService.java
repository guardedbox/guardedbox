package com.guardedbox.service;

import static com.guardedbox.constants.SecurityParameters.MINING_ALGORITHM;
import static com.guardedbox.constants.SecurityParameters.MINING_PROOF_THRESHOLD;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

/**
 * Mining Verification Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class MiningVerificationService {

    /** Perform an & operation with this number to convert a signed byte to unsigned. */
    private static final int SIGNED_TO_UNSIGNED_BYTE = 255;

    /**
     * Verifies a mining.
     *
     * @param originalMessage The original message.
     * @param nonce A nonce such that the first bytes of hmac(message, nonce) are lower than a predefined threshold.
     * @return Boolean indicating if the nonce fulfills the mining condition.
     */
    public boolean verifyMining(
            byte[] originalMessage,
            byte[] nonce) {

        try {

            SecretKeySpec keySpec = new SecretKeySpec(nonce, MINING_ALGORITHM);
            Mac mac = Mac.getInstance(MINING_ALGORITHM);
            mac.init(keySpec);
            byte[] proof = mac.doFinal(originalMessage);

            boolean valid = true;
            for (int i = 0; i < MINING_PROOF_THRESHOLD.length; i++) {
                valid &= (proof[i] & SIGNED_TO_UNSIGNED_BYTE) <= MINING_PROOF_THRESHOLD[i];
            }

            return valid;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }

    }

}
