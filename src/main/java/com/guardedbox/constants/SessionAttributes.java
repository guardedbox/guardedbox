package com.guardedbox.constants;

/**
 * Session Attributes.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public final class SessionAttributes {

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
