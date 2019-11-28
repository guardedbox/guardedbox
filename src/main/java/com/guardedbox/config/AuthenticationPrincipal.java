package com.guardedbox.config;

import java.io.Serializable;

import com.guardedbox.dto.AccountDto;

/**
 * Object used as authentication principal for sessions.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class AuthenticationPrincipal
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -5762749620314203068L;

    /** The Account associated to this Authentication Principal. */
    private AccountDto account;

    /**
     * Constructor with Account.
     *
     * @param account The Account associated to this Authentication Principal.
     */
    public AuthenticationPrincipal(
            AccountDto account) {
        this.account = account;
    }

    /**
     * @return A string representation of this Authentication Principal, represented by its Account email.
     */
    @Override
    public String toString() {
        return account == null ? null : account.getEmail();
    }

    /**
     * @return The account.
     */
    public AccountDto getAccount() {
        return account;
    }

}
