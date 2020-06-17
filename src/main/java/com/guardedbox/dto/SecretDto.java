package com.guardedbox.dto;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@JsonInclude(Include.NON_NULL)
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

    /** Must Rotate Key. */
    private Boolean mustRotateKey;

    /** Number of Sharings. */
    private Integer numberOfSharings;

    /** Number of Ex Members. */
    private Integer numberOfExMembers;

}
