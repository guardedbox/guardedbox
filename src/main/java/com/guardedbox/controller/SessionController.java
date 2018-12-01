package com.guardedbox.controller;

import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.SecurityParameters.ENTROPY_EXPANDER_LENGTH;
import static com.guardedbox.constants.SecurityParameters.LOGIN_CHALLENGE_LENGTH;
import static com.guardedbox.constants.SecurityParameters.LOGIN_CHALLENGE_TTL;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.constants.SessionAttributes;
import com.guardedbox.dto.AccountWithEntropyExpanderDto;
import com.guardedbox.dto.AccountWithPublicKeyDto;
import com.guardedbox.dto.LoginChallengeDto;
import com.guardedbox.dto.ObtainLoginCodeDto;
import com.guardedbox.dto.SessionInfoDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.service.EmailService;
import com.guardedbox.service.ExecutionTimeService;
import com.guardedbox.service.RandomService;
import com.guardedbox.service.transactional.AccountsService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired CsrfTokenRepository csrfTokenRepository) {
        this.loginCodeEmailSubject = loginCodeEmailSubject;
        this.loginCodeEmailBody = loginCodeEmailBody;
        this.accountsService = accountsService;
        this.executionTimeService = executionTimeService;
        this.randomService = randomService;
        this.emailService = emailService;
        this.request = request;
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
     * @param email An email.
     * @return The entropy expander of the account corresponding to the introduced email.
     */
    @GetMapping("/get-account-entropy-expander")
    public AccountWithEntropyExpanderDto getAccountEntropyExpander(
            @RequestParam(name = "email", required = true) @NotBlank @Email(regexp = EMAIL_PATTERN) @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH) String email) {

        AccountWithEntropyExpanderDto account = accountsService.getAccountWithEntropyExpander(email);

        if (account == null) {
            account = new AccountWithEntropyExpanderDto();
            account.setEmail(email);
            account.setEntropyExpander(randomService.randomHexString(ENTROPY_EXPANDER_LENGTH));
        }

        return account;

    }

    /**
     * @param email An email.
     * @return A login challenge for the introduced email.
     */
    @GetMapping("/get-login-challenge")
    public LoginChallengeDto getLoginChallenge(
            @RequestParam(name = "email", required = true) @NotBlank @Email(regexp = EMAIL_PATTERN) @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH) String email) {

        long currentTime = System.currentTimeMillis();

        // Generate the challenge.
        String challenge = randomService.randomAlphanumericString(LOGIN_CHALLENGE_LENGTH);

        // Store it in session.
        session.setAttribute(SessionAttributes.LOGIN_CHALLENGE, challenge);
        session.setAttribute(SessionAttributes.LOGIN_CHALLENGE_EMAIL, email);
        session.setAttribute(SessionAttributes.LOGIN_CHALLENGE_EXPIRATION, currentTime + LOGIN_CHALLENGE_TTL);

        // Return it.
        LoginChallengeDto loginChallengeDto = new LoginChallengeDto();
        loginChallengeDto.setChallenge(challenge);

        return loginChallengeDto;

    }

    /**
     * Generates a login code, sends it by email, and stores it in a session attribute.
     * 
     * @param obtainLoginCodeDto Object with the necessary credentials to generate and send a login code.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping("/obtain-login-code")
    public SuccessDto obtainLoginCode(
            @RequestBody(required = true) @Valid ObtainLoginCodeDto obtainLoginCodeDto) {

        long currentTime = System.currentTimeMillis();
        String error = null;

        // Check if the account exists.
        AccountWithPublicKeyDto accountWithPublicKeyDto = accountsService.getAccountWithPublicKey(obtainLoginCodeDto.getEmail());
        if (accountWithPublicKeyDto == null) {
            error = "Email is not registered";
        }

        if (error == null) {

            // Check if the login challenge is correct.
            String loginChallenge = (String) session.getAttribute(SessionAttributes.LOGIN_CHALLENGE);
            String loginChallengeEmail = (String) session.getAttribute(SessionAttributes.LOGIN_CHALLENGE_EMAIL);
            Long loginChallengeExpiration = (Long) session.getAttribute(SessionAttributes.LOGIN_CHALLENGE_EXPIRATION);

            if (loginChallenge == null) {
                error = "No login challenge has been generated";
            } else if (loginChallengeEmail == null || !loginChallengeEmail.equals(obtainLoginCodeDto.getEmail())) {
                error = "Login email does not match login challenge email";
            } else if (loginChallengeExpiration == null || loginChallengeExpiration < currentTime) {
                error = "Login challenge is expired";
            } else if (!passwordEncoder.matches(obtainLoginCodeDto.getChallengeResponse(), accountWithPublicKeyDto.getPublicKey())) {
                error = "Introduced login challenge is incorrect";
            }

        }

        if (error == null) {

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

        // Remove login session attributes in case of error.
        if (error != null) {
            removeLoginSessionAttributes(session);
        }

        // Fix execution time.
        executionTimeService.fix(currentTime, OBTAIN_LOGIN_CODE_EXECUTION_TIME);

        // Successful result (always).
        return new SuccessDto(true);

    }

    /**
     * Removes all login challenge and login code session attributes.
     * 
     * @param session Current Session.
     */
    public static void removeLoginSessionAttributes(
            HttpSession session) {

        // Remove login challenge session attributes.
        session.removeAttribute(SessionAttributes.LOGIN_CHALLENGE);
        session.removeAttribute(SessionAttributes.LOGIN_CHALLENGE_EMAIL);
        session.removeAttribute(SessionAttributes.LOGIN_CHALLENGE_EXPIRATION);

        // Remove login code session attributes.
        session.removeAttribute(SessionAttributes.LOGIN_CODE);
        session.removeAttribute(SessionAttributes.LOGIN_CODE_EMAIL);
        session.removeAttribute(SessionAttributes.LOGIN_CODE_EXPIRATION);
        session.removeAttribute(SessionAttributes.LOGIN_REQUEST_CODE);

    }

}
