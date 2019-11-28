package com.guardedbox.service;

import static com.guardedbox.constants.SecurityParameters.SIGNATURE_ALGORITHM;
import static com.guardedbox.constants.SecurityParameters.SIGNATURE_ALGORITHM_ID;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountWithSigningPublicKeyDto;
import com.guardedbox.service.transactional.AccountsService;

/**
 * Signature Verification Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class SignatureVerificationService {

    /** AccountsService. */
    private final AccountsService accountsService;

    /**
     * Constructor with Attributes.
     *
     * @param accountsService AccountsService.
     */
    public SignatureVerificationService(
            @Autowired AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    /**
     * Verifies a signature.
     *
     * @param originalMessage The original message.
     * @param signedMessage The signature of the original message.
     * @param email The email corresponding to the Account whose private key was used to sign the message.
     * @return Boolean indicating if the signature is verified.
     */
    public boolean verifySignature(
            byte[] originalMessage,
            byte[] signedMessage,
            String email) {

        AccountWithSigningPublicKeyDto accountWithSigningPublicKeyDto = accountsService.getAndCheckAccountWithSigningPublicKeyByEmail(email);
        return verifySignature(originalMessage, signedMessage, Base64.getDecoder().decode(accountWithSigningPublicKeyDto.getSigningPublicKey()));

    }

    /**
     * Verifies a signature.
     *
     * @param originalMessage The original message.
     * @param signedMessage The signature of the original message.
     * @param signingPublicKey The public key corresponding to the private key used to sign the message.
     * @return Boolean indicating if the signature is verified.
     */
    public boolean verifySignature(
            byte[] originalMessage,
            byte[] signedMessage,
            byte[] signingPublicKey) {

        try {

            KeyFactory keyFactory = KeyFactory.getInstance(SIGNATURE_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
            KeySpec keySpec = new X509EncodedKeySpec(new SubjectPublicKeyInfo(SIGNATURE_ALGORITHM_ID, signingPublicKey).getEncoded());
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
            signature.initVerify(pubKey);
            signature.update(originalMessage);

            return signature.verify(signedMessage);

        } catch (NoSuchAlgorithmException | NoSuchProviderException
                | IOException | InvalidKeySpecException | InvalidKeyException
                | SignatureException e) {
            return false;
        }

    }

}
