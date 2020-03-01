package com.guardedbox.config;

import java.io.Serializable;

import com.guardedbox.dto.AccountDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Object used as authentication principal for sessions.
 *
 * @author s3curitybug@gmail.com
 *
 */
@AllArgsConstructor
public class AuthenticationPrincipal
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -5762749620314203068L;

    /** The Account associated to this Authentication Principal. */
    @Getter
    private AccountDto account;

    /**
     * @return A string representation of this Authentication Principal, represented by its Account email.
     */
    @Override
    public String toString() {
        return account == null ? null : account.getEmail();
    }

}
