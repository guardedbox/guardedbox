package com.guardedbox.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Properties starting by cryptography.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ConfigurationProperties(prefix = "cryptography")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class CryptographyProperties {

    /** Property: cryptography.cryptography-length. */
    private final Integer cryptographyLength;

    /** Property: cryptography.signature-algorithm. */
    private final String signatureAlgorithm;

    /** Property: cryptography.bcrypt-rounds. */
    private final Integer bcryptRounds;

}
