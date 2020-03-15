package com.guardedbox.config;

import static com.guardedbox.constants.Api.API_BASE_PATH;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import com.guardedbox.properties.CookiesProperties;
import com.guardedbox.properties.SecurityParametersProperties;

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

    /** SecurityParametersProperties. */
    private final SecurityParametersProperties securityParameters;

    /** CookiesProperties. */
    private final CookiesProperties cookiesProperties;

    /** CustomHeaderWriter. */
    private final CustomHeaderWriter customHeaderWriter;

    /**
     * Bean: PasswordEncoder.
     *
     * @return BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(securityParameters.getBcryptRounds());
    }

    /**
     * Bean: CookieSerializer.
     *
     * @return DefaultCookieSerializer.
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setUseBase64Encoding(cookiesProperties.getValueBase64());
        cookieSerializer.setSameSite(cookiesProperties.getSameSite());
        return cookieSerializer;
    }

    /**
     * Configures Security.
     */
    @Override
    protected void configure(
            HttpSecurity httpSecurity)
            throws Exception {

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
                .antMatchers(HttpMethod.GET, API_BASE_PATH + "session").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "session/challenge").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "session/otp").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "session/login").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "session/logout").permitAll()
                .antMatchers(HttpMethod.GET, API_BASE_PATH + "registrations").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "registrations").permitAll()
                .antMatchers(HttpMethod.POST, API_BASE_PATH + "accounts").permitAll()
                .antMatchers(HttpMethod.GET, API_BASE_PATH + "accounts/salt").permitAll()
                .antMatchers(HttpMethod.GET, API_BASE_PATH + "accounts/encryption-public-key").permitAll()

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
