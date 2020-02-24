package com.guardedbox.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Account.
 * Contains the following fields: accountId, email, signingPublicKey.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class AccountWithSigningPublicKeyDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 374106389578555109L;

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

    /** Signing Public Key. */
    private String signingPublicKey;

}
