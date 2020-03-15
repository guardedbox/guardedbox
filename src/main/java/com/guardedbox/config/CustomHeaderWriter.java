package com.guardedbox.config;

import static com.guardedbox.constants.Api.API_BASE_PATH;
import static com.guardedbox.constants.Header.CACHE_CONTROL;
import static com.guardedbox.constants.Header.CONTENT_SECURITY_POLICY;
import static com.guardedbox.constants.Header.CONTENT_TYPE_OPTIONS;
import static com.guardedbox.constants.Header.EXPECT_CT;
import static com.guardedbox.constants.Header.FEATURE_POLICY;
import static com.guardedbox.constants.Header.FRAME_OPTIONS;
import static com.guardedbox.constants.Header.REFERRER_POLICY;
import static com.guardedbox.constants.Header.STRICT_TRANSPORT_SECURITY;
import static com.guardedbox.constants.Header.XSS_PROTECTION;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.guardedbox.properties.HeadersProperties;

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

    /** Cacheable URI Extensions. */
    private static final List<String> CACHEABLE_URI_EXTENSIONS = Arrays.asList(
            "js", "css", "gif", "png", "jpg", "jpeg", "ico", "svg", "woff", "woff2", "eot", "ttf");

    /** Non-Cacheable URI Extensions. */
    private static final List<String> NON_CACHEABLE_URI_EXTENSIONS = Arrays.asList("html");

    /** Indicates if environment is dev, based on property environment. */
    @Value("#{'${environment}' == 'dev'}")
    private final boolean dev;

    /** HeadersProperties. */
    private final HeadersProperties headersProperties;

    @Override
    public void writeHeaders(
            HttpServletRequest request,
            HttpServletResponse response) {

        // HSTS and Expect-CT.
        if (request.isSecure() && !dev) {
            if (!StringUtils.isEmpty(headersProperties.getStrictTransportSecurityHeader()))
                response.setHeader(STRICT_TRANSPORT_SECURITY.getHeaderName(), headersProperties.getStrictTransportSecurityHeader());
            if (!StringUtils.isEmpty(headersProperties.getExpectCtHeader()))
                response.setHeader(EXPECT_CT.getHeaderName(), headersProperties.getExpectCtHeader());
        }

        // Content Security Policy.
        if (!StringUtils.isEmpty(headersProperties.getContentSecurityPolicyHeader()))
            response.setHeader(CONTENT_SECURITY_POLICY.getHeaderName(), headersProperties.getContentSecurityPolicyHeader());

        // Cache Control.
        String uri = request.getRequestURI();
        String uriExtension = uri.lastIndexOf(".") == -1 ? "" : uri.substring(uri.lastIndexOf(".") + 1);

        if (uri.startsWith(API_BASE_PATH) || NON_CACHEABLE_URI_EXTENSIONS.contains(uriExtension)) {
            if (!StringUtils.isEmpty(headersProperties.getCacheControlNonCacheableHeader()))
                response.setHeader(CACHE_CONTROL.getHeaderName(), headersProperties.getCacheControlNonCacheableHeader());
        } else if (CACHEABLE_URI_EXTENSIONS.contains(uriExtension)) {
            if (!StringUtils.isEmpty(headersProperties.getCacheControlCacheableHeader()))
                response.setHeader(CACHE_CONTROL.getHeaderName(), headersProperties.getCacheControlCacheableHeader());
        } else {
            if (!StringUtils.isEmpty(headersProperties.getCacheControlNonCacheableHeader()))
                response.setHeader(CACHE_CONTROL.getHeaderName(), headersProperties.getCacheControlNonCacheableHeader());
        }

        // Frame Options.
        if (!StringUtils.isEmpty(headersProperties.getFrameOptionsHeader()))
            response.setHeader(FRAME_OPTIONS.getHeaderName(), headersProperties.getFrameOptionsHeader());

        // XSS Protection.
        if (!StringUtils.isEmpty(headersProperties.getXssProtectionHeader()))
            response.setHeader(XSS_PROTECTION.getHeaderName(), headersProperties.getXssProtectionHeader());

        // Content Type Options.
        if (!StringUtils.isEmpty(headersProperties.getContentTypeOptionsHeader()))
            response.setHeader(CONTENT_TYPE_OPTIONS.getHeaderName(), headersProperties.getContentTypeOptionsHeader());

        // Referrer Policy.
        if (!StringUtils.isEmpty(headersProperties.getReferrerPolicyHeader()))
            response.setHeader(REFERRER_POLICY.getHeaderName(), headersProperties.getReferrerPolicyHeader());

        // Feature Policy.
        if (!StringUtils.isEmpty(headersProperties.getFeaturePolicyHeader()))
            response.setHeader(FEATURE_POLICY.getHeaderName(), headersProperties.getFeaturePolicyHeader());

    }

}
