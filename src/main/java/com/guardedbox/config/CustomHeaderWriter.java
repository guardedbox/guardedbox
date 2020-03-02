package com.guardedbox.config;

import static com.guardedbox.constants.Api.API_BASE_PATH;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

/**
 * Custom Header Writer.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Component
@RequiredArgsConstructor
public final class CustomHeaderWriter
        implements HeaderWriter {

    /** Strict Transport Security Header Name. */
    private static final String STRICT_TRANSPORT_SECURITY_HEADER_NAME = "Strict-Transport-Security";

    /** Expect-CT Header Name. */
    private static final String EXPECT_CT_HEADER_NAME = "Expect-CT";

    /** Content Security Policy Header Name. */
    private static final String CONTENT_SECURITY_POLICY_HEADER_NAME = "Content-Security-Policy";

    /** Cache Control Header Name. */
    private static final String CACHE_CONTROL_HEADER_NAME = "Cache-Control";

    /** Cacheable URI Extensions. */
    private static final List<String> CACHEABLE_URI_EXTENSIONS = Arrays.asList(
            "js", "css", "gif", "png", "jpg", "jpeg", "ico", "svg", "woff", "woff2", "eot", "ttf");

    /** Non-Cacheable URI Extensions. */
    private static final List<String> NON_CACHEABLE_URI_EXTENSIONS = Arrays.asList("html");

    /** Frame Options Header Name. */
    private static final String FRAME_OPTIONS_HEADER_NAME = "X-Frame-Options";

    /** XSS Protection Header Name. */
    private static final String XSS_PROTECTION_HEADER_NAME = "X-XSS-Protection";

    /** Content Type Options Header Name. */
    private static final String CONTENT_TYPE_OPTIONS_HEADER_NAME = "X-Content-Type-Options";

    /** Referrer Policy Header Name. */
    private static final String REFERRER_POLICY_HEADER_NAME = "Referrer-Policy";

    /** Feature Policy Header Name. */
    private static final String FEATURE_POLICY_HEADER_NAME = "Feature-Policy";

    /** Indicates if environment is dev, based on property environment. */
    @Value("#{'${environment}' == 'dev'}")
    private final boolean dev;

    /** Property: server.strict-transport-security-header. */
    @Value("${server.strict-transport-security-header:}")
    private final String strictTransportSecurityHeaderValue;

    /** Property: server.expect-ct-header. */
    @Value("${server.expect-ct-header:}")
    private final String expectCtHeaderValue;

    /** Property: server.content-security-policy-header. */
    @Value("${server.content-security-policy-header:}")
    private final String contentSecurityPolicyHeaderValue;

    /** Property: server.cache-control-cacheable-header. */
    @Value("${server.cache-control-cacheable-header:}")
    private final String cacheControlCacheableHeaderValue;

    /** Property: server.cache-control-non-cacheable-header. */
    @Value("${server.cache-control-non-cacheable-header:}")
    private final String cacheControlNonCacheableHeaderValue;

    /** Property: server.frame-options-header. */
    @Value("${server.frame-options-header:}")
    private final String frameOptionsHeaderValue;

    /** Property: server.xss-protection-header. */
    @Value("${server.xss-protection-header:}")
    private final String xssProtectionHeaderValue;

    /** Property: server.content-type-options-header. */
    @Value("${server.content-type-options-header:}")
    private final String contentTypeOptionsHeaderValue;

    /** Property: server.referrer-policy-header. */
    @Value("${server.referrer-policy-header:}")
    private final String referrerPolicyHeaderValue;

    /** Property: server.feature-policy-header. */
    @Value("${server.feature-policy-header:}")
    private final String featurePolicyHeaderValue;

    @Override
    public void writeHeaders(
            HttpServletRequest request,
            HttpServletResponse response) {

        // HSTS and Expect-CT.
        if (request.isSecure() && !dev) {
            if (!StringUtils.isEmpty(strictTransportSecurityHeaderValue))
                response.setHeader(STRICT_TRANSPORT_SECURITY_HEADER_NAME, strictTransportSecurityHeaderValue);
            if (!StringUtils.isEmpty(expectCtHeaderValue))
                response.setHeader(EXPECT_CT_HEADER_NAME, expectCtHeaderValue);
        }

        // Content Security Policy.
        if (!StringUtils.isEmpty(contentSecurityPolicyHeaderValue))
            response.setHeader(CONTENT_SECURITY_POLICY_HEADER_NAME, contentSecurityPolicyHeaderValue);

        // Cache Control.
        String uri = request.getRequestURI();
        String uriExtension = uri.lastIndexOf(".") == -1 ? "" : uri.substring(uri.lastIndexOf(".") + 1);

        if (uri.startsWith(API_BASE_PATH) || NON_CACHEABLE_URI_EXTENSIONS.contains(uriExtension)) {
            if (!StringUtils.isEmpty(cacheControlNonCacheableHeaderValue))
                response.setHeader(CACHE_CONTROL_HEADER_NAME, cacheControlNonCacheableHeaderValue);
        } else if (CACHEABLE_URI_EXTENSIONS.contains(uriExtension)) {
            if (!StringUtils.isEmpty(cacheControlCacheableHeaderValue))
                response.setHeader(CACHE_CONTROL_HEADER_NAME, cacheControlCacheableHeaderValue);
        } else {
            if (!StringUtils.isEmpty(cacheControlNonCacheableHeaderValue))
                response.setHeader(CACHE_CONTROL_HEADER_NAME, cacheControlNonCacheableHeaderValue);
        }

        // Frame Options.
        if (!StringUtils.isEmpty(frameOptionsHeaderValue))
            response.setHeader(FRAME_OPTIONS_HEADER_NAME, frameOptionsHeaderValue);

        // XSS Protection.
        if (!StringUtils.isEmpty(xssProtectionHeaderValue))
            response.setHeader(XSS_PROTECTION_HEADER_NAME, xssProtectionHeaderValue);

        // Content Type Options.
        if (!StringUtils.isEmpty(contentTypeOptionsHeaderValue))
            response.setHeader(CONTENT_TYPE_OPTIONS_HEADER_NAME, contentTypeOptionsHeaderValue);

        // Referrer Policy.
        if (!StringUtils.isEmpty(referrerPolicyHeaderValue))
            response.setHeader(REFERRER_POLICY_HEADER_NAME, referrerPolicyHeaderValue);

        // Feature Policy.
        if (!StringUtils.isEmpty(featurePolicyHeaderValue))
            response.setHeader(FEATURE_POLICY_HEADER_NAME, featurePolicyHeaderValue);

    }

}
