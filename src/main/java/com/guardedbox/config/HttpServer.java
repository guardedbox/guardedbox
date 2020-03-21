package com.guardedbox.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.guardedbox.constants.JdkProperty;
import com.guardedbox.properties.ServerProperties;
import com.guardedbox.properties.SslProperties;

import lombok.RequiredArgsConstructor;

/**
 * HTTP Server.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Component
@RequiredArgsConstructor
public class HttpServer {

    /** ServerProperties. */
    private final ServerProperties serverProperties;

    /** SslProperties. */
    private final SslProperties sslProperties;

    /**
     * Bean: ServletWebServerFactory.
     * Creates a dual port Tomcat, listening both in an http port and an https port. The http port simply redirects to the https one.
     *
     * @return TomcatServletWebServerFactory.
     */
    @Bean
    public ServletWebServerFactory servletWebServerFactory() {

        // Check if there is dual port configuration.
        if (serverProperties.getInternalHttpPort() == null
                || serverProperties.getExternalHttpPort() == null
                || serverProperties.getInternalHttpsPort() == null
                || serverProperties.getExternalHttpsPort() == null
                || serverProperties.getPort().equals(serverProperties.getInternalHttpPort())
                || !serverProperties.getPort().equals(serverProperties.getInternalHttpsPort())) {
            return new TomcatServletWebServerFactory();
        }

        // Set TLS ECDH offered curves.
        if (!StringUtils.isEmpty(sslProperties.getEcdhCurves()))
            System.setProperty(JdkProperty.TLS_ECDH_CURVES.getPropertyName(), sslProperties.getEcdhCurves());

        // Enable TLS OCSP stapling.
        if (sslProperties.getEnableOcspStapling() != null)
            System.setProperty(JdkProperty.TLS_ENABLE_OCSP_STAPLING.getPropertyName(), sslProperties.getEnableOcspStapling().toString());

        // Create the https Tomcat.
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {

            @Override
            protected void postProcessContext(
                    Context context) {

                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);

            }

        };

        // Customize the https connector.
        tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {

            @Override
            public void customize(
                    Connector connector) {

                SSLHostConfig sslHostConfig = connector.findSslHostConfigs()[0];
                sslHostConfig.setHonorCipherOrder(true);

            }

        });

        // Add the http connector with a redirection to the https port.
        Connector httpConnector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        httpConnector.setScheme("http");
        httpConnector.setPort(serverProperties.getInternalHttpPort());
        httpConnector.setSecure(false);
        httpConnector.setRedirectPort(serverProperties.getExternalHttpsPort());
        tomcat.addAdditionalTomcatConnectors(httpConnector);

        return tomcat;

    }

}
