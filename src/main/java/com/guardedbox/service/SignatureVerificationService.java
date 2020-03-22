package com.guardedbox.service;

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

import javax.annotation.PostConstruct;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import com.guardedbox.properties.CryptographyProperties;
import com.guardedbox.service.transactional.AccountsService;

import lombok.RequiredArgsConstructor;

/**
 * Signature Verification Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@RequiredArgsConstructor
public class SignatureVerificationService {

    /** CryptographyProperties. */
    private final CryptographyProperties cryptographyProperties;

    /** AccountsService. */
    private final AccountsService accountsService;

    /** Signature Algorithm Identifier. */
    private AlgorithmIdentifier signatureAlgorithmId;

    /**
     * This method gets called after the bean is created.
     */
    @PostConstruct
    private void postConstruct() {

        // Set signatureAlgorithmId.
        try {

            signatureAlgorithmId = new AlgorithmIdentifier((ASN1ObjectIdentifier) EdECObjectIdentifiers.class
                    .getDeclaredField("id_" + cryptographyProperties.getSignatureAlgorithm()).get(null));

        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(String.format(
                    "Error creating the AlgorithmIdentifier corresponding to the signature algorithm %s",
                    cryptographyProperties.getSignatureAlgorithm()));
        }

    }

    /**
     * Verifies a signature.
     *
     * @param originalMessage The original message.
     * @param signedMessage The signature of the original message.
     * @param email The email corresponding to the Account whose private key was used to sign the message.
     * @param login Boolean indicating if the login public key of the account should be used to verify the signature or the signing one instead.
     * @return Boolean indicating if the signature is verified.
     */
    public boolean verifySignature(
            byte[] originalMessage,
            byte[] signedMessage,
            String email,
            boolean login) {

        String signingPublicKey = null;

        if (login) {
            signingPublicKey = accountsService.getAndCheckAccountLoginPublicKeyByEmail(email).getLoginPublicKey();
        } else {
            signingPublicKey = accountsService.getAndCheckAccountPublicKeysByEmail(email).getSigningPublicKey();
        }

        return verifySignature(originalMessage, signedMessage, Base64.getDecoder().decode(signingPublicKey));

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

            KeyFactory keyFactory = KeyFactory.getInstance(cryptographyProperties.getSignatureAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
            KeySpec keySpec = new X509EncodedKeySpec(new SubjectPublicKeyInfo(signatureAlgorithmId, signingPublicKey).getEncoded());
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance(cryptographyProperties.getSignatureAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
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
