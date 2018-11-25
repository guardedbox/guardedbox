package com.guardedbox.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO: Account.
 * Contains the following fields: accountId, email, password.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class AccountWithPasswordDto {

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

    /** Password. */
    @JsonIgnore
    private String password;

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
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(
            String password) {
        this.password = password;
    }

}
