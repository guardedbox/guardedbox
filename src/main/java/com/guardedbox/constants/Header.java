package com.guardedbox.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Headers Enum.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RequiredArgsConstructor
@Getter
public enum Header {

    /** Strict Transport Security. */
    STRICT_TRANSPORT_SECURITY("Strict-Transport-Security"),

    /** Expect-CT. */
    EXPECT_CT("Expect-CT"),

    /** Content Security Policy. */
    CONTENT_SECURITY_POLICY("Content-Security-Policy"),

    /** Cache Control. */
    CACHE_CONTROL("Cache-Control"),

    /** Frame Options. */
    FRAME_OPTIONS("X-Frame-Options"),

    /** XSS Protection. */
    XSS_PROTECTION("X-XSS-Protection"),

    /** Content Type Options. */
    CONTENT_TYPE_OPTIONS("X-Content-Type-Options"),

    /** Referrer Policy. */
    REFERRER_POLICY("Referrer-Policy"),

    /** Feature Policy. */
    FEATURE_POLICY("Feature-Policy"),

    /** App Language. */
    APP_LANGUAGE("App-Language");

    /** Header name. */
    private final String headerName;

}
