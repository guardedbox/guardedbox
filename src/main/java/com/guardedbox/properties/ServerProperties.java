package com.guardedbox.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Properties starting by server.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ConfigurationProperties(prefix = "server")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class ServerProperties {

    /** Property: server.internal-http-port. */
    private final Integer internalHttpPort;

    /** Property: server.internal-https-port. */
    private final Integer internalHttpsPort;

    /** Property: server.external-http-port. */
    private final Integer externalHttpPort;

    /** Property: server.external-https-port. */
    private final Integer externalHttpsPort;

    /** Property: server.port. */
    private final Integer port;

}
