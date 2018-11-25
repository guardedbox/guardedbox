package com.guardedbox.controller;

import static com.guardedbox.constants.Constraints.CAPTCHA_RESPONSE_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.CAPTCHA_RESPONSE_PATTERN;
import static com.guardedbox.constants.SecurityParameters.CAPTCHA_RESPONSE_HEADER;
import static com.guardedbox.constants.SecurityParameters.LOGIN_CODE_LENGTH;
import static com.guardedbox.constants.SecurityParameters.LOGIN_CODE_TTL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.constants.SessionAttributes;
import com.guardedbox.dto.AccountWithPasswordDto;
import com.guardedbox.dto.ObtainLoginCodeDto;
import com.guardedbox.dto.SessionInfoDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.service.CaptchaVerificationService;
import com.guardedbox.service.EmailService;
import com.guardedbox.service.ExecutionTimeService;
import com.guardedbox.service.RandomService;
import com.guardedbox.service.transactional.AccountsService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Controller: Session Handling.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping("/api/session")
@Validated
@PropertySource("classpath:email/email_en.properties")
public class SessionController {

    /** Execution time of the obtain login code request (ms). */
    private static final long OBTAIN_LOGIN_CODE_EXECUTION_TIME = 3000L;

    /** Property: login-code.email.subject. */
    private final String loginCodeEmailSubject;

    /** Property: login-code.email.body. */
    private final String loginCodeEmailBody;

    /** AccountsService. */
    private final AccountsService accountsService;

    /** ExecutionTimeService. */
    private final ExecutionTimeService executionTimeService;

    /** RandomService. */
    private final RandomService randomService;

    /** EmailService. */
    private final EmailService emailService;

    /** Current Request. */
    private final HttpServletRequest request;

    /** Current Session. */
    private final HttpSession session;

    /** CaptchaVerificationService. */
    private final CaptchaVerificationService captchaVerificationService;

    /** PasswordEncoder. */
    private final PasswordEncoder passwordEncoder;

    /** CsrfTokenRepository. */
    private final CsrfTokenRepository csrfTokenRepository;

    /**
     * Constructor with Attributes.
     * 
     * @param loginCodeEmailSubject Property: login-code.email.subject.
     * @param loginCodeEmailBody Property: login-code.email.body.
     * @param accountsService AccountsService.
     * @param executionTimeService ExecutionTimeService.
     * @param randomService RandomService.
     * @param emailService EmailService.
     * @param request Current Request.
     * @param captchaVerificationService CaptchaVerificationService.
     * @param session Current Session.
     * @param passwordEncoder PasswordEncoder.
     * @param csrfTokenRepository CsrfTokenRepository.
     */
    public SessionController(
            @Value("${login-code.email.subject}") String loginCodeEmailSubject,
            @Value("${login-code.email.body}") String loginCodeEmailBody,
            @Autowired AccountsService accountsService,
            @Autowired ExecutionTimeService executionTimeService,
            @Autowired RandomService randomService,
            @Autowired EmailService emailService,
            @Autowired HttpServletRequest request,
            @Autowired HttpSession session,
            @Autowired CaptchaVerificationService captchaVerificationService,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired CsrfTokenRepository csrfTokenRepository) {
        this.loginCodeEmailSubject = loginCodeEmailSubject;
        this.loginCodeEmailBody = loginCodeEmailBody;
        this.accountsService = accountsService;
        this.executionTimeService = executionTimeService;
        this.randomService = randomService;
        this.emailService = emailService;
        this.request = request;
        this.captchaVerificationService = captchaVerificationService;
        this.session = session;
        this.passwordEncoder = passwordEncoder;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    /**
     * @return The current session information.
     */
    @GetMapping("/info")
    public SessionInfoDto sessionInfo() {

        SessionInfoDto sessionInfoDto = new SessionInfoDto();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Authentication.
        boolean authenticated = !(authentication instanceof AnonymousAuthenticationToken);
        if (authenticated) {
            sessionInfoDto.setAuthenticated(true);
            sessionInfoDto.setEmail(authentication.getName());
        }

        // Roles.
        List<String> roles = new ArrayList<>(authentication.getAuthorities().size());
        for (GrantedAuthority authority : authentication.getAuthorities())
            roles.add(authority.getAuthority());
        sessionInfoDto.setRoles(roles);

        // CSRF.
        sessionInfoDto.setCsrfToken(csrfTokenRepository.loadToken(request));

        return sessionInfoDto;

    }

    /**
     * Generates a login code, sends it by email, and stores it in a session attribute.
     * 
     * @param obtainLoginCodeDto Object with the necessary credentials to generate and send a login code.
     * @param captchaResponse Header with the response to the captcha challenge.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping("/obtain-login-code")
    public SuccessDto obtainLoginCode(
            @RequestBody(required = true) @Valid ObtainLoginCodeDto obtainLoginCodeDto,
            @RequestHeader(name = CAPTCHA_RESPONSE_HEADER, required = true) @NotBlank @Pattern(regexp = CAPTCHA_RESPONSE_PATTERN) @Size(max = CAPTCHA_RESPONSE_MAX_LENGTH) String captchaResponse) {

        long currentTime = System.currentTimeMillis();

        // Check if the captcha response is valid.
        if (!captchaVerificationService.verifyCaptchaResponse(captchaResponse)) {
            throw new ServiceException("Captcha response is invalid");
        }

        // Check if the credentials are correct.
        AccountWithPasswordDto accountWithPasswordDto = accountsService.getAccountWithPassword(obtainLoginCodeDto.getEmail());
        if (accountWithPasswordDto != null && passwordEncoder.matches(obtainLoginCodeDto.getPassword(), accountWithPasswordDto.getPassword())) {

            // Generate the login code.
            String loginCode = randomService.randomAlphanumericString(LOGIN_CODE_LENGTH);

            // Send it by email.
            emailService.send(
                    obtainLoginCodeDto.getEmail(),
                    loginCodeEmailSubject,
                    String.format(loginCodeEmailBody, loginCode));

            // Store it in session.
            session.setAttribute(SessionAttributes.LOGIN_CODE, loginCode);
            session.setAttribute(SessionAttributes.LOGIN_CODE_EMAIL, obtainLoginCodeDto.getEmail());
            session.setAttribute(SessionAttributes.LOGIN_CODE_EXPIRATION, currentTime + LOGIN_CODE_TTL);

        }

        // Fix execution time.
        executionTimeService.fix(currentTime, OBTAIN_LOGIN_CODE_EXECUTION_TIME);

        // Successful result (always).
        return new SuccessDto(true);

    }

}
