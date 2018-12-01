package com.guardedbox.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.guardedbox.constants.SessionAttributes;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.servlet.http.HttpSession;

/**
 * PasswordEncoder to check login challenge responses.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class LoginChallengePasswordEncoder
        implements PasswordEncoder {

    /** Current Session. */
    private final HttpSession session;

    /**
     * Constructor with Attributes.
     * 
     * @param session Current Session.
     */
    public LoginChallengePasswordEncoder(
            @Autowired HttpSession session) {
        this.session = session;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.crypto.password.PasswordEncoder#encode(java.lang.CharSequence)
     */
    @Override
    public String encode(
            CharSequence rawPassword) {
        return rawPassword.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.crypto.password.PasswordEncoder#matches(java.lang.CharSequence, java.lang.String)
     */
    @Override
    public boolean matches(
            CharSequence challengeResponse,
            String publicKey) {

        String challenge = (String) session.getAttribute(SessionAttributes.LOGIN_CHALLENGE);
        if (challenge == null)
            return false;

        try {

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            BigInteger modulus = new BigInteger(1, publicKeyBytes);
            BigInteger exponent = BigInteger.valueOf(3);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(pubKey);
            signature.update(challenge.getBytes());
            byte[] challengeResponseBytes = hexStringToByteArray(challengeResponse.toString());
            boolean result = signature.verify(challengeResponseBytes);

            return result;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            return false;
        }

    }

    /**
     * Converts a hexadecimal string to byte array.
     * 
     * @param s The hexadecimal string.
     * @return The byte array.
     */
    private byte[] hexStringToByteArray(
            String s) {
        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) ((Character.digit(s.charAt(i * 2), 16) << 4) + Character.digit(s.charAt(i * 2 + 1), 16));
        }
        return data;
    }

}
