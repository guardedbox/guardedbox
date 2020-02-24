package com.guardedbox.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Account with Secrets associated to it.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class AccountWithSecretsDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 8481536477701691288L;

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

    /** Encryption Public Key. */
    private String encryptionPublicKey;

    /** Secrets. */
    private List<SecretDto> secrets;

}
