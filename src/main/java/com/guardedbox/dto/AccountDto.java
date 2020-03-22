package com.guardedbox.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@SuppressWarnings("serial")
public class AccountDto
        implements Serializable {

    /** Account ID. */
    @JsonIgnore
    private UUID accountId;

    /** Email. */
    private String email;

    /** Login Salt. */
    private String loginSalt;

    /** Login Public Key. */
    private String loginPublicKey;

    /** Encryption Salt. */
    private String encryptionSalt;

    /** Encryption Public Key. */
    private String encryptionPublicKey;

    /** Signing Salt. */
    private String signingSalt;

    /** Signing Public Key. */
    private String signingPublicKey;

    /** Secrets. */
    private List<SecretDto> secrets;

}
