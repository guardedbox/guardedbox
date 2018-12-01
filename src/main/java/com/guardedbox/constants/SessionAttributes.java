package com.guardedbox.constants;

/**
 * Session Attributes.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public final class SessionAttributes {

    /** Session attribute in which the challenge generated in the get-login-challenge request is stored. */
    public static final String LOGIN_CHALLENGE = "login-challenge";

    /** Session attribute in which the email introduced in the get-login-challenge request is stored. */
    public static final String LOGIN_CHALLENGE_EMAIL = "login-challenge-email";

    /** Session attribute in which the expiration time of the login challenge is stored. */
    public static final String LOGIN_CHALLENGE_EXPIRATION = "login-challenge-expiration";

    /** Session attribute in which the code generated in the obtain-login-code request is stored. */
    public static final String LOGIN_CODE = "login-code";

    /** Session attribute in which the email introduced in the obtain-login-code request is stored. */
    public static final String LOGIN_CODE_EMAIL = "login-code-email";

    /** Session attribute in which the expiration time of the login code is stored. */
    public static final String LOGIN_CODE_EXPIRATION = "login-code-expiration";

    /** Session attribute in which the code received in the login request is stored. */
    public static final String LOGIN_REQUEST_CODE = "login-req-code";

    /** Session attribute in which the account ID is stored. */
    public static final String ACCOUNT_ID = "account-id";

    /** Session attribute in which the account email is stored. */
    public static final String ACCOUNT_EMAIL = "account-email";

}
