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

import com.guardedbox.dto.AccountDto;
import com.guardedbox.properties.SecurityParametersProperties;
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

    /** SecurityParametersProperties. */
    private final SecurityParametersProperties securityParameters;

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
                    .getDeclaredField("id_" + securityParameters.getSignatureAlgorithm()).get(null));

        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(String.format(
                    "Error creating the AlgorithmIdentifier corresponding to the signature algorithm %s",
                    securityParameters.getSignatureAlgorithm()));
        }

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

        AccountDto account = accountsService.getAndCheckAccountPublicKeysByEmail(email);
        return verifySignature(originalMessage, signedMessage, Base64.getDecoder().decode(account.getSigningPublicKey()));

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

            KeyFactory keyFactory = KeyFactory.getInstance(securityParameters.getSignatureAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
            KeySpec keySpec = new X509EncodedKeySpec(new SubjectPublicKeyInfo(signatureAlgorithmId, signingPublicKey).getEncoded());
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance(securityParameters.getSignatureAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
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
