package com.guardedbox.config;

import static com.guardedbox.constants.SecurityParameters.BCRYPT_ROUNDS;

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
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

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

    /** Property: server.servlet.session.cookie.value-base64. */
    @Value("${server.servlet.session.cookie.value-base64:true}")
    private final boolean cookieValueBase64;

    /** Property: server.servlet.session.cookie.same-site. */
    @Value("${server.servlet.session.cookie.same-site:Lax}")
    private final String cookieSameSite;

    /**
     * Bean: PasswordEncoder.
     *
     * @return BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_ROUNDS);
    }

    /**
     * Bean: CookieSerializer.
     *
     * @return DefaultCookieSerializer.
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setUseBase64Encoding(cookieValueBase64);
        cookieSerializer.setSameSite(cookieSameSite);
        return cookieSerializer;
    }

    /**
     * Configures Security.
     */
    @Override
    protected void configure(
            HttpSecurity http)
            throws Exception {

        http

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

                // Allow Public Endpoints.
                .antMatchers(HttpMethod.GET, "/api/session").permitAll()
                .antMatchers(HttpMethod.POST, "/api/session/challenge").permitAll()
                .antMatchers(HttpMethod.POST, "/api/session/otp").permitAll()
                .antMatchers(HttpMethod.POST, "/api/session/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/session/logout").permitAll()
                .antMatchers(HttpMethod.GET, "/api/registrations").permitAll()
                .antMatchers(HttpMethod.POST, "/api/registrations").permitAll()
                .antMatchers(HttpMethod.POST, "/api/accounts").permitAll()
                .antMatchers(HttpMethod.GET, "/api/accounts/salt").permitAll()
                .antMatchers(HttpMethod.GET, "/api/accounts/encryption-public-key").permitAll()

                // Require Authentication for any other Endpoint.
                .anyRequest().fullyAuthenticated()

                // Headers.
                .and().headers()
                .httpStrictTransportSecurity().disable()
                .addHeaderWriter(new HstsHeaderWriter())

                // CSRF.
                .and().csrf().disable(); // CSRF token is not used. Cookie samesite attribute is used to prevent CSRF attacks instead.

    }

}
