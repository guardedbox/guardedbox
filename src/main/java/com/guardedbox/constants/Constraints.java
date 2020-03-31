package com.guardedbox.constants;

import lombok.experimental.UtilityClass;

/**
 * Constraints.
 *
 * @author s3curitybug@gmail.com
 *
 */
@UtilityClass
public class Constraints {

    /** Alphanumeric Pattern. */
    public static final String ALPHANUMERIC_PATTERN = "[a-zA-Z0-9]*";

    /** Alphanumeric 64 Bytes Length. */
    public static final int ALPHANUMERIC_64BYTES_LENGTH = 86;

    /** Base64 Pattern. */
    public static final String BASE64_PATTERN = "[a-zA-Z0-9+\\/=]*";

    /** Base64 32 Bytes Length. */
    public static final int BASE64_32BYTES_LENGTH = 44;

    /** Base64 44 Bytes Length. */
    public static final int BASE64_44BYTES_LENGTH = 60;

    /** Base64 64 Bytes Length. */
    public static final int BASE64_64BYTES_LENGTH = 88;

    /** Base64 JSON Pattern. */
    public static final String BASE64_JSON_PATTERN = "[{}\\[\\]\"':,a-zA-Z0-9+\\/=]*";

    /** Email Pattern. */
    public static final String EMAIL_PATTERN = ".+@.+\\..{2,}";

    /** Email Minimum Length. */
    public static final int EMAIL_MIN_LENGTH = 6;

    /** Email Maximum Length. */
    public static final int EMAIL_MAX_LENGTH = 254;

    /** Secret Name Maximum Length. */
    public static final int SECRET_NAME_MAX_LENGTH = 100; // TODO Delete

    /** Secret Value Maximum Length. */
    public static final int SECRET_VALUE_MAX_LENGTH = 164978;

    /** Group name Maximum Length. */
    public static final int GROUP_NAME_MAX_LENGTH = 100;

}
