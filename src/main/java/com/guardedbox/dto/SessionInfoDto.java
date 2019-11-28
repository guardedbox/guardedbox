package com.guardedbox.dto;

import java.io.Serializable;

/**
 * DTO: Response to the session info request.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class SessionInfoDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 7948660755765688188L;

    /** Indicates if the current session is authenticated. */
    private boolean authenticated;

    /** Indicates the current session email, in case it is authenticated, or null otherwise. */
    private String email;

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

}
