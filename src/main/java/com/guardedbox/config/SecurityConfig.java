package com.guardedbox.config;

import static com.guardedbox.constants.PathParameters.API_BASE_PATH;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import com.guardedbox.properties.CryptographyProperties;

import lombok.RequiredArgsConstructor;

/**
 * Security Configuration.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig
        extends WebSecurityConfigurerAdapter {

    /** Indicates if environment is dev, based on property environment. */
    @Value("#{'${environment}' == 'dev'}")
    private final boolean dev;

    /** CryptographyProperties. */
    private final CryptographyProperties cryptographyProperties;

    /** CustomHeaderWriter. */
    private final CustomHeaderWriter customHeaderWriter;

    /**
     * Bean: HttpSessionIdResolver.
     *
     * @return HeaderHttpSessionIdResolver.
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }

    /**
     * Bean: PasswordEncoder.
     *
     * @return BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(cryptographyProperties.getBcryptRounds());
    }

    /**
     * Configures Security.
     */
    @Override
    protected void configure(
            HttpSecurity httpSecurity)
            throws Exception {

        if (dev) {

            // Dev configuration.
            httpSecurity

                    // Allow Web Hot-Update Endpoints.
                    .authorizeRequests()
                    .regexMatchers(HttpMethod.GET, "/[a-zA-Z0-9]+\\.hot-update.json").permitAll()
                    .regexMatchers(HttpMethod.GET, "/main\\.[a-zA-Z0-9]+\\.hot-update.js").permitAll();

        }

        // General configuration.
        httpSecurity

                // Login.
                .formLogin().disable()

                // Logout.
                .logout().disable()

                // Unauthorized Handling.
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))

                // Allow Web Endpoints.
                .and().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.GET, "/index.html").permitAll()
                .regexMatchers(HttpMethod.GET, "/js/bundle(\\.[a-zA-Z0-9]+)?\\.js").permitAll()
                .regexMatchers(HttpMethod.GET, "/css/bundle(\\.[a-zA-Z0-9]+)?\\.css").permitAll()
                .regexMatchers(HttpMethod.GET, "/favicon\\.(gif|png|jpe?g|ico|svg)").permitAll()
                .regexMatchers(HttpMethod.GET, "/img/.*\\.(gif|png|jpe?g|ico|svg)").permitAll()
                .regexMatchers(HttpMethod.GET, "/font/.*\\.(woff2?|eot|ttf)").permitAll()

                // Allow API Public Endpoints.
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "registrations").permitAll()
                .antMatchers(HttpMethod.GET, API_BASE_PATH + "registrations").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "accounts").permitAll()
                .antMatchers(HttpMethod.GET, API_BASE_PATH + "accounts/login-salt").permitAll()
                .antMatchers(HttpMethod.GET, API_BASE_PATH + "session").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "session/challenge").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "session/otp").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "session/login").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "session/logout").permitAll()

                // Require Authentication for any other Endpoint.
                .anyRequest().fullyAuthenticated()

                // Headers.
                .and().headers()
                .cacheControl().disable()
                .frameOptions().disable()
                .xssProtection().disable()
                .contentTypeOptions().disable()
                .httpStrictTransportSecurity().disable()
                .addHeaderWriter(customHeaderWriter)

                // CSRF.
                .and().csrf().disable(); // CSRF token is not used. Cookie samesite attribute is used to prevent CSRF attacks instead.

    }

}
