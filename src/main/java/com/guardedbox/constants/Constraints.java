package com.guardedbox.constants;

/**
 * Constraints.
 *
 * @author s3curitybug@gmail.com
 *
 */
public final class Constraints {

    /** Alphanumeric Pattern. */
    public static final String ALPHANUMERIC_PATTERN = "[a-zA-Z0-9]*";

    /** Base64 Pattern. */
    public static final String BASE64_PATTERN = "[a-zA-Z0-9+\\/=]*";

    /** Email Pattern. */
    public static final String EMAIL_PATTERN = ".+@.+\\..{2,}";

    /** Email Minimum Length. */
    public static final int EMAIL_MIN_LENGTH = 6;

    /** Email Maximum Length. */
    public static final int EMAIL_MAX_LENGTH = 254;

    /** Salt Length (number of base64 characters). */
    public static final int SALT_LENGTH = 44;

    /** Encryption Public Key Length (number of base64 characters). */
    public static final int ENCRYPTION_PUBLIC_KEY_LENGTH = 44;

    /** Signing Public Key Length (number of base64 characters). */
    public static final int SIGNING_PUBLIC_KEY_LENGTH = 44;

    /** Signature Length (number of base64 characters). */
    public static final int SIGNATURE_LENGTH = 88;

    /** Secret Name Maximum Length. */
    public static final int SECRET_NAME_MAX_LENGTH = 100;

    /** Secret Value Maximum Length. */
    public static final int SECRET_VALUE_MAX_LENGTH = 16016;

    /** Group name Maximum Length. */
    public static final int GROUP_NAME_MAX_LENGTH = 100;

    /** Encrypted Key Length (number of base64 characters). */
    public static final int ENCRYPTED_KEY_LENGTH = 60;

}
