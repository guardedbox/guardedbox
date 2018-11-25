package com.guardedbox.constants;

/**
 * Security Parameters.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public final class SecurityParameters {

    /** Bcrypt rounds. */
    public static final int BCRYPT_ROUNDS = 12;

    /** Login code length. */
    public static final int LOGIN_CODE_LENGTH = 10;

    /** Login code time to live (ms). */
    public static final long LOGIN_CODE_TTL = 10 * 60 * 1000L;

    /** Registration token length. */
    public static final int REGISTRATION_TOKEN_LENGTH = 64;

    /** Registration token time to live (ms). */
    public static final long REGISTRATION_TOKEN_TTL = 2 * 60 * 60 * 1000L;

    /** Registration token minimum time to live (ms). Tokens will not be overridden during this time since its expedition. */
    public static final long REGISTRATION_TOKEN_MIN_TTL = 5 * 60 * 1000L;

    /** Number of security questions. */
    public static final int N_SECURITY_QUESTIONS = 3;

    /** Name of the captcha response header. */
    public static final String CAPTCHA_RESPONSE_HEADER = "Captcha-Response";

}
