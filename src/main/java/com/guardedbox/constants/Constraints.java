package com.guardedbox.constants;

/**
 * Constraints.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public final class Constraints {

    /** Email Pattern. */
    public static final String EMAIL_PATTERN = ".+@.+\\..{2,}";

    /** Email Minimum Length. */
    public static final int EMAIL_MIN_LENGTH = 6;

    /** Email Maximum Length. */
    public static final int EMAIL_MAX_LENGTH = 254;

    /** Alphanumeric Pattern. */
    public static final String ALPHANUMERIC_PATTERN = "[a-zA-Z0-9]*";

    /** Base64 Pattern. */
    public static final String BASE64_PATTERN = "[a-zA-Z0-9+\\/=]*";

    /** Hexadecimal Pattern. */
    public static final String HEX_PATTERN = "[0-9a-fA-F]*";

    /** SHA512 Hexadecimal Length. */
    public static final int SHA512_HEX_LENGTH = 128;

    /** Bcrypt Pattern. */
    public static final String BCRYPT_PATTERN = "\\$2.?\\$\\d{1,2}\\$[.\\/a-zA-Z0-9]{53}";

    /** Bcrypt Length. */
    public static final int BCRYPT_LENGTH = 60;

    /** Encrypted Value Pattern. */
    public static final String ENCRYPTED_VALUE_PATTERN = "[a-zA-Z0-9+\\/=?]*";

    /** Signature Max Length. */
    public static final int SIGNATURE_MAX_LENGTH = 512;

    /** Security Question Maximum Length. */
    public static final int SECURITY_QUESTION_MAX_LENGTH = 256;

    /** Security Questions Maximum Length. */
    public static final int SECURITY_QUESTIONS_MAX_LENGTH = 4096;

    /** Public Key Length. */
    public static final int PUBLIC_KEY_LENGTH = 344;

    /** Encrypted Private Key Length. */
    public static final int ENCRYPTED_PRIVATE_KEY_LENGTH = 4593;

    /** Captcha Response Pattern. */
    public static final String CAPTCHA_RESPONSE_PATTERN = "[a-zA-Z0-9\\-_]*";

    /** Captcha Response Maximum Length. */
    public static final int CAPTCHA_RESPONSE_MAX_LENGTH = 1024;

    /** Secret Name Maximum Length. */
    public static final int SECRET_NAME_MAX_LENGTH = 64;

    /** Secret Value Maximum Length. */
    public static final int SECRET_VALUE_MAX_LENGTH = 16384;

}
