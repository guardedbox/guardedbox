package com.guardedbox.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Properties starting by server.servlet.session.cookie.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ConfigurationProperties(prefix = "server.servlet.session.cookie")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class CookiesProperties {

    /** Property: server.servlet.session.cookie.name. */
    private final String name;

    /** Property: server.servlet.session.cookie.value-base64. */
    private final Boolean valueBase64;

    /** Property: server.servlet.session.cookie.http-only. */
    private final String httpOnly;

    /** Property: server.servlet.session.cookie.secure. */
    private final String secure;

    /** Property: server.servlet.session.cookie.same-site. */
    private final String sameSite;

}
