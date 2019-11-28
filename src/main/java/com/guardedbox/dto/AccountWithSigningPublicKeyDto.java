package com.guardedbox.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO: Account.
 * Contains the following fields: accountId, email, signingPublicKey.
 *
 * @author s3curitybug@gmail.com
 *
 */
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

    /**
     * @return The accountId.
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * @param accountId The accountId to set.
     */
    public void setAccountId(
            Long accountId) {
        this.accountId = accountId;
    }

    /**
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(
            String email) {
        this.email = email;
    }

    /**
     * @return The signingPublicKey.
     */
    public String getSigningPublicKey() {
        return signingPublicKey;
    }

    /**
     * @param signingPublicKey The signingPublicKey to set.
     */
    public void setSigningPublicKey(
            String signingPublicKey) {
        this.signingPublicKey = signingPublicKey;
    }

}
