package com.guardedbox.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.guardedbox.config.AuthenticationPrincipal;
import com.guardedbox.dto.AccountDto;

/**
 * Current Session Account Service.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class SessionAccountService {

    /**
     * @return The current session account.
     */
    public AccountDto getAccount() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal == null || !(principal instanceof AuthenticationPrincipal)) {
            return null;
        }

        return ((AuthenticationPrincipal) principal).getAccount();

    }

    /**
     * @return The current session account ID.
     */
    public Long getAccountId() {

        AccountDto sessionAccount = getAccount();

        if (sessionAccount == null) {
            return null;
        }

        return sessionAccount.getAccountId();

    }

    /**
     * @return The current session account email.
     */
    public String getEmail() {

        AccountDto sessionAccount = getAccount();

        if (sessionAccount == null) {
            return null;
        }

        return sessionAccount.getEmail();

    }

}
