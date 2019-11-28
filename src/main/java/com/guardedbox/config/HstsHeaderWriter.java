package com.guardedbox.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.header.HeaderWriter;

/**
 * HSTS Header Writer.
 *
 * @author s3curitybug@gmail.com
 *
 */
public final class HstsHeaderWriter
        implements HeaderWriter {

    /** HSTS Header Name. */
    private static final String HEADER_NAME = "Strict-Transport-Security";

    /** HSTS Header Value. */
    private static final String HEADER_VALUE = "max-age=31536000; includeSubDomains; preload";

    @Override
    public void writeHeaders(
            HttpServletRequest request,
            HttpServletResponse response) {

        if (request.isSecure()) {
            response.setHeader(HEADER_NAME, HEADER_VALUE);
        }

    }

}
