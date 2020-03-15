package com.guardedbox.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * JDK Properties Enum.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RequiredArgsConstructor
@Getter
public enum JdkProperty {

    /** JDK property which sets TLS ECDH curves. */
    TLS_ECDH_CURVES("jdk.tls.namedGroups"),

    /** JDK property which enables TLS OCSP stapling. */
    TLS_ENABLE_OCSP_STAPLING("jdk.tls.server.enableStatusRequestExtension");

    /** Property name. */
    private final String propertyName;

}
