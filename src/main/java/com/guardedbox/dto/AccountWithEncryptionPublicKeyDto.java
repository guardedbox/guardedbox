package com.guardedbox.dto;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Account.
 * Contains the following fields: accountId, email, encryptionPublicKey.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class AccountWithEncryptionPublicKeyDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -6202084704147455366L;

    /** Account ID. */
    @JsonIgnore
    private UUID accountId;

    /** Email. */
    private String email;

    /** Encryption Public Key. */
    private String encryptionPublicKey;

}
