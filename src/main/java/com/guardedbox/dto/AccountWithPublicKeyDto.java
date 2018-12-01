package com.guardedbox.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO: Account.
 * Contains the following fields: accoundId, email, publicKey.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class AccountWithPublicKeyDto {

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

    /** Public Key. */
    private String publicKey;

    /**
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

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
     * @param email The email to set.
     */
    public void setEmail(
            String email) {
        this.email = email;
    }

    /**
     * @return The publicKey.
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * @param publicKey The publicKey to set.
     */
    public void setPublicKey(
            String publicKey) {
        this.publicKey = publicKey;
    }

}
