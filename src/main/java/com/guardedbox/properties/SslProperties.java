package com.guardedbox.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Properties starting by server-ssl.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ConfigurationProperties(prefix = "server.ssl")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class SslProperties {

    /** Property: server.ssl.enabled-protocols. */
    private final String enabledProtocols;

    /** Property: server.ssl.ciphers. */
    private final String ciphers;

    /** Property: server.ssl.ecdh-curves. */
    private final String ecdhCurves;

    /** Property: server.ssl.enable-ocsp-stapling. */
    private final Boolean enableOcspStapling;

}
