package com.guardedbox.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Properties starting by server related to headers.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ConfigurationProperties(prefix = "server")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class HeadersProperties {

    /** Property: server.strict-transport-security-header. */
    private final String strictTransportSecurityHeader;

    /** Property: server.expect-ct-header. */
    private final String expectCtHeader;

    /** Property: server.content-security-policy-header. */
    private final String contentSecurityPolicyHeader;

    /** Property: server.cache-control-cacheable-header. */
    private final String cacheControlCacheableHeader;

    /** Property: server.cache-control-non-cacheable-header. */
    private final String cacheControlNonCacheableHeader;

    /** Property: server.frame-options-header. */
    private final String frameOptionsHeader;

    /** Property: server.xss-protection-header. */
    private final String xssProtectionHeader;

    /** Property: server.content-type-options-header. */
    private final String contentTypeOptionsHeader;

    /** Property: server.referrer-policy-header. */
    private final String referrerPolicyHeader;

    /** Property: server.feature-policy-header. */
    private final String featurePolicyHeader;

}
