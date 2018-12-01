package com.guardedbox.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * DTO: Account with Secrets associated to it.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class AccountWithSecretsDto {

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

    /** Secrets. */
    private List<SecretDto> secrets;

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
     * @return The secrets.
     */
    public List<SecretDto> getSecrets() {
        return secrets;
    }

    /**
     * @param secrets The secrets to set.
     */
    public void setSecrets(
            List<SecretDto> secrets) {
        this.secrets = secrets;
    }

}
