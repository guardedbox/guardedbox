package com.guardedbox.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class SecretDto
        implements Serializable {

    /** Secret ID. */
    private UUID secretId;

    /** Value. */
    private String value;

    /** Encrypted Key. */
    private String encryptedKey;

}
