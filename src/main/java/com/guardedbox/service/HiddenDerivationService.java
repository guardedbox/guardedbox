package com.guardedbox.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.guardedbox.exception.ServiceException;
import com.guardedbox.properties.KeysProperties;

import lombok.RequiredArgsConstructor;

/**
 * Hidden Derivation Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@RequiredArgsConstructor
public class HiddenDerivationService {

    /** KeysProperties. */
    private final KeysProperties keysProperties;

    /** Hidden derivation keys. */
    private HashMap<Integer, byte[]> derivationKeys;

    /**
     * This method gets called after the bean is created.
     */
    @PostConstruct
    private void postConstruct() {

        derivationKeys = new HashMap<>(keysProperties.getHiddenDerivation().size());

        for (Entry<Integer, String> entry : keysProperties.getHiddenDerivation().entrySet()) {

            Integer keyLength = entry.getKey();
            String keyStr = entry.getValue();
            if (StringUtils.isEmpty(keyStr)) {
                throw new RuntimeException(String.format("Hidden derivation key of length %s is empty", keyLength));
            }

            byte[] keyRaw = Base64.getDecoder().decode(keyStr);
            if (keyRaw.length != keyLength) {
                throw new RuntimeException(String.format("Hidden derivation key of length %s contains %s bytes", keyLength, keyRaw.length));
            }

            derivationKeys.put(keyLength, keyRaw);

        }

    }

    /**
     * Derives an array of bytes from a source and a secret key.
     *
     * @param source The source.
     * @param length The length of the derived array of bytes.
     * @return The derived array of bytes encoded to base64.
     */
    public String deriveBase64(
            String source,
            int length) {

        return Base64.getEncoder().encodeToString(derive(source, length));

    }

    /**
     * Derives an array of bytes from a source and a secret key.
     *
     * @param source The source.
     * @param length The length of the derived array of bytes.
     * @return The derived array of bytes.
     */
    public byte[] derive(
            String source,
            int length) {

        byte[] key = derivationKeys.get(length);
        if (key == null) {
            throw new ServiceException(String.format("No key of length %s was defined", length));
        }

        try {

            String algorithm = "HmacSHA" + length * 8;
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            mac.init(keySpec);
            return mac.doFinal(source.getBytes(StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ServiceException("Error during the derivation process", e);
        }

    }

}
