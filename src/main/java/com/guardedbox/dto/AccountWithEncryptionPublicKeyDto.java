package com.guardedbox.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO: Account.
 * Contains the following fields: accountId, email, encryptionPublicKey.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class AccountWithEncryptionPublicKeyDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -6202084704147455366L;

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

    /** Encryption Public Key. */
    private String encryptionPublicKey;

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
     * @return The encryptionPublicKey.
     */
    public String getEncryptionPublicKey() {
        return encryptionPublicKey;
    }

    /**
     * @param encryptionPublicKey The encryptionPublicKey to set.
     */
    public void setEncryptionPublicKey(
            String encryptionPublicKey) {
        this.encryptionPublicKey = encryptionPublicKey;
    }

}
