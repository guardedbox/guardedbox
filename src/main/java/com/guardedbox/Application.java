package com.guardedbox;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application main class.
 *
 * @author s3curitybug@gmail.com
 *
 */
@SpringBootApplication
public class Application {

    /**
     * Main method. Invokes the Spring-Boot run method.
     *
     * @param args Arguments. They are passed to the Spring-Boot run method.
     */
    public static void main(
            final String[] args) {

        // Add BouncyCastle Security Provider.
        Security.addProvider(new BouncyCastleProvider());

        // Run Spring-Boot.
        SpringApplication.run(Application.class, args);

    }

}
