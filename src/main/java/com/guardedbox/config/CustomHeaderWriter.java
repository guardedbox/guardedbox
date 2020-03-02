package com.guardedbox.config;

import static com.guardedbox.constants.Api.API_BASE_PATH;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.header.HeaderWriter;

/**
 * Custom Header Writer.
 *
 * @author s3curitybug@gmail.com
 *
 */
public final class CustomHeaderWriter
        implements HeaderWriter {

    /** HSTS Header Name. */
    private static final String HSTS_HEADER_NAME = "Strict-Transport-Security";

    /** HSTS Header Value. */
    private static final String HSTS_HEADER_VALUE = "max-age=31536000; includeSubDomains; preload";

    /** Cache Control Header Name. */
    private static final String CACHE_CONTROL_HEADER_NAME = "Cache-Control";

    /** Cache Control Header Cacheable Value. */
    private static final String CACHE_CONTROL_HEADER_CACHEABLE_VALUE = "public, max-age=31536000";

    /** Cache Control Header Non-Cacheable Value. */
    private static final String CACHE_CONTROL_HEADER_NON_CACHEABLE_VALUE = "no-cache, no-store, must-revalidate";

    /** Cacheable URI Extensions. */
    private static final List<String> CACHEABLE_URI_EXTENSIONS = Arrays.asList(
            "js", "css", "gif", "png", "jpg", "jpeg", "ico", "svg", "woff", "woff2", "eot", "ttf");

    /** Non-Cacheable URI Extensions. */
    private static final List<String> NON_CACHEABLE_URI_EXTENSIONS = Arrays.asList("html");

    @Override
    public void writeHeaders(
            HttpServletRequest request,
            HttpServletResponse response) {

        // HSTS.
        if (request.isSecure()) {
            response.setHeader(HSTS_HEADER_NAME, HSTS_HEADER_VALUE);
        }

        // Cache.
        String uri = request.getRequestURI();
        String uriExtension = uri.lastIndexOf(".") == -1 ? "" : uri.substring(uri.lastIndexOf(".") + 1);

        if (uri.startsWith(API_BASE_PATH) || NON_CACHEABLE_URI_EXTENSIONS.contains(uriExtension)) {
            response.setHeader(CACHE_CONTROL_HEADER_NAME, CACHE_CONTROL_HEADER_NON_CACHEABLE_VALUE);
        } else if (CACHEABLE_URI_EXTENSIONS.contains(uriExtension)) {
            response.setHeader(CACHE_CONTROL_HEADER_NAME, CACHE_CONTROL_HEADER_CACHEABLE_VALUE);
        } else {
            response.setHeader(CACHE_CONTROL_HEADER_NAME, CACHE_CONTROL_HEADER_NON_CACHEABLE_VALUE);
        }

    }

}
