package com.guardedbox.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Session Attributes Enum.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RequiredArgsConstructor
@Getter
public enum SessionAttribute {

    /** Session attribute in which the challenge generated in the get challenge request is stored. */
    CHALLENGE("challenge"),

    /** Session attribute in which the one time password generated in the obtain-otp request is stored. */
    OTP("otp");

    /** Attribute name. */
    private final String attributeName;

}
