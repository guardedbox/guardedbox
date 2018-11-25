package com.guardedbox.config.security;

import static com.guardedbox.constants.SecurityParameters.BCRYPT_ROUNDS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.service.CaptchaVerificationService;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Validator;

/**
 * Security Configuration.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Configuration
public class SecurityConfig
        extends WebSecurityConfigurerAdapter {

    /** UserDetailsService. */
    private final UserDetailsService userDetailsService;

    /** ObjectMapper. */
    private final ObjectMapper objectMapper;

    /** Validator. */
    private final Validator validator;

    /** Current Session. */
    private final HttpSession session;

    /** CaptchaVerificationService. */
    private final CaptchaVerificationService captchaVerificationService;

    /**
     * Constructor with Attributes.
     * 
     * @param userDetailsService UserDetailsService.
     * @param objectMapper ObjectMapper.
     * @param validator Validator.
     * @param session Current Session.
     * @param captchaVerificationService CaptchaVerificationService.
     */
    public SecurityConfig(
            @Autowired UserDetailsService userDetailsService,
            @Autowired ObjectMapper objectMapper,
            @Autowired Validator validator,
            @Autowired HttpSession session,
            @Autowired CaptchaVerificationService captchaVerificationService) {
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.session = session;
        this.captchaVerificationService = captchaVerificationService;
    }

    /**
     * Bean: AuthenticationFilter.
     * 
     * @return AuthenticationFilter.
     * @throws Exception Thrown by authenticationManagerBean().
     */
    @Bean
    public AuthenticationFilter authenticationFilter()
            throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(objectMapper, validator, session, captchaVerificationService);
        authenticationFilter.setFilterProcessesUrl("/api/session/login");
        authenticationFilter.setAuthenticationSuccessHandler(this::loginSuccessHandler);
        authenticationFilter.setAuthenticationFailureHandler(this::loginFailureHandler);
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationFilter;
    }

    /**
     * Bean: AuthenticationProvider.
     * 
     * @return DaoAuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

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
     * Bean: CsrfTokenRepository.
     * 
     * @return HttpSessionCsrfTokenRepository.
     */
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new HttpSessionCsrfTokenRepository();
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
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                // Logout.
                .logout().logoutUrl("/api/session/logout").logoutSuccessHandler(this::logoutSuccessHandler)

                // Unauthorized Handling.
                .and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))

                // Allow Web Endpoints.
                .and().authorizeRequests().antMatchers("/").permitAll()
                .and().authorizeRequests().antMatchers("/index.html").permitAll()
                .and().authorizeRequests().antMatchers("/js/bundle.js").permitAll()
                .and().authorizeRequests().antMatchers("/css/bundle.css").permitAll()
                .and().authorizeRequests().regexMatchers("/img/.*\\.(gif|png|jpe?g|svg)").permitAll()
                .and().authorizeRequests().regexMatchers("/font/.*\\.(woff2?|eot|ttf)").permitAll()

                // Allow Session, Password Recovery and Registration Endpoints.
                .and().authorizeRequests().antMatchers("/api/session/info").permitAll()
                .and().authorizeRequests().antMatchers("/api/session/obtain-login-code").permitAll()
                .and().authorizeRequests().antMatchers("/api/session/login").permitAll()
                .and().authorizeRequests().antMatchers("/api/session/logout").permitAll()
                .and().authorizeRequests().antMatchers("/api/registration/obtain-registration-token").permitAll()
                .and().authorizeRequests().antMatchers("/api/registration/get-registration-token-info").permitAll()
                .and().authorizeRequests().antMatchers("/api/registration/register-account").permitAll()

                // Require Authentication for any other Endpoint.
                .and().authorizeRequests().anyRequest().fullyAuthenticated()

                // Authentication Provider.
                .and().authenticationProvider(authenticationProvider())

                // CSRF.
                .csrf().csrfTokenRepository(csrfTokenRepository());

    }

    /**
     * Handles the response to the login request in case of authentication success.
     * 
     * @param request The received login request.
     * @param response The response that will be returned.
     * @param authentication The authentication that was just created.
     * @throws IOException In case an error occurs while writing the response.
     * 
     */
    private void loginSuccessHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        request.changeSessionId();
        csrfTokenRepository().saveToken(csrfTokenRepository().generateToken(request), request, response);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        objectMapper.writeValue(response.getWriter(), new SuccessDto(true));

    }

    /**
     * Handles the response to the login request in case of authentication failure.
     * 
     * @param request The received login request.
     * @param response The response that will be returned.
     * @param e The exception that caused the authentication failure.
     * @throws IOException In case an error occurs while writing the response.
     * 
     */
    private void loginFailureHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException e)
            throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        objectMapper.writeValue(response.getWriter(), new SuccessDto(false));

    }

    /**
     * Handles the response to the logout request.
     * 
     * @param request The received logout request.
     * @param response The response that will be returned.
     * @param authentication The authentication that was just created.
     * @throws IOException In case an error occurs while writing the response.
     * 
     */
    private void logoutSuccessHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        objectMapper.writeValue(response.getWriter(), new SuccessDto(true));

    }

}
