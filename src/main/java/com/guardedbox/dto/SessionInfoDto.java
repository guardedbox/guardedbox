package com.guardedbox.dto;

import org.springframework.security.web.csrf.CsrfToken;

import java.util.List;

/**
 * DTO: Response to the session info request.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class SessionInfoDto {

    /** Indicates if the current session is authenticated. */
    private boolean authenticated;

    /** Indicates the current session email, in case it is authenticated, or null otherwise. */
    private String email;

    /** Indicates the current session roles, in case it is authenticated, or null otherwise. */
    private List<String> roles;

    /** Indicates the current session csrf token. */
    private CsrfToken csrfToken;

    /**
     * @return The authenticated.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * @param authenticated The authenticated to set.
     */
    public void setAuthenticated(
            boolean authenticated) {
        this.authenticated = authenticated;
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
     * @return The roles.
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * @param roles The roles to set.
     */
    public void setRoles(
            List<String> roles) {
        this.roles = roles;
    }

    /**
     * @return The csrfToken.
     */
    public CsrfToken getCsrfToken() {
        return csrfToken;
    }

    /**
     * @param csrfToken The csrfToken to set.
     */
    public void setCsrfToken(
            CsrfToken csrfToken) {
        this.csrfToken = csrfToken;
    }

}
