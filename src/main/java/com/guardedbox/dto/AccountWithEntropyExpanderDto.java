package com.guardedbox.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO: Account.
 * Contains the following fields: accoundId, email, entropyExpander.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class AccountWithEntropyExpanderDto {

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

    /** Entropy Expander. */
    private String entropyExpander;

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
     * @return The entropyExpander.
     */
    public String getEntropyExpander() {
        return entropyExpander;
    }

    /**
     * @param entropyExpander The entropyExpander to set.
     */
    public void setEntropyExpander(
            String entropyExpander) {
        this.entropyExpander = entropyExpander;
    }

}
