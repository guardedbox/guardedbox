package com.guardedbox.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO: Account.
 * Contains the following fields: accountId, email, salt.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class AccountWithSaltDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 8311168168735591089L;

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

    /** Salt. */
    private String salt;

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
     * @return The salt.
     */
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt The salt to set.
     */
    public void setSalt(
            String salt) {
        this.salt = salt;
    }

}
