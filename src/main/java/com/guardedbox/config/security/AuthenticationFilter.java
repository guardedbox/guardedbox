package com.guardedbox.config.security;

import static com.guardedbox.constants.Constraints.CAPTCHA_RESPONSE_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.CAPTCHA_RESPONSE_PATTERN;
import static com.guardedbox.constants.SecurityParameters.CAPTCHA_RESPONSE_HEADER;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardedbox.constants.SessionAttributes;
import com.guardedbox.dto.LoginDto;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.service.CaptchaVerificationService;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Validator;

/**
 * Authentication Filter.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class AuthenticationFilter
        extends UsernamePasswordAuthenticationFilter {

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
     * @param objectMapper ObjectMapper.
     * @param validator Validator.
     * @param session Current Session.
     * @param captchaVerificationService CaptchaVerificationService.
     */
    public AuthenticationFilter(
            ObjectMapper objectMapper,
            Validator validator,
            HttpSession session,
            CaptchaVerificationService captchaVerificationService) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.session = session;
        this.captchaVerificationService = captchaVerificationService;
    }

    /**
     * Login Request Handler.
     */
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) {

        // Check that request method is post.
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        // Check if the captcha response is valid.
        String captchaResponse = request.getHeader(CAPTCHA_RESPONSE_HEADER);
        if (StringUtils.isEmpty(captchaResponse)
                || captchaResponse.length() > CAPTCHA_RESPONSE_MAX_LENGTH
                || !captchaResponse.matches(CAPTCHA_RESPONSE_PATTERN)) {
            throw new AuthenticationServiceException("Captcha response is invalid");
        }
        if (!captchaVerificationService.verifyCaptchaResponse(captchaResponse)) {
            throw new ServiceException("Captcha response is invalid");
        }

        // Parse request body.
        if (!request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE))
            throw new AuthenticationServiceException("Login request content-type is not valid");
        LoginDto loginDto = null;
        try (InputStream requestInputStream = request.getInputStream()) {
            loginDto = objectMapper.readValue(requestInputStream, LoginDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Error reading the login request body");
        }
        if (!validator.validate(loginDto).isEmpty())
            throw new AuthenticationServiceException("Login request body is not valid");

        // Build the authentication token based on the introduced email and password.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword());
        setDetails(request, authenticationToken);

        // Store the introduced login code in a session attribute.
        session.setAttribute(SessionAttributes.LOGIN_REQUEST_CODE, loginDto.getCode());

        // Build the authentication from the authentication token.
        Authentication authentication = this.getAuthenticationManager().authenticate(authenticationToken);

        // Return the authentication.
        return authentication;

    }

}
