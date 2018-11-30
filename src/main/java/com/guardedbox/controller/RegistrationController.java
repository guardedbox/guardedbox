package com.guardedbox.controller;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.Constraints.CAPTCHA_RESPONSE_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.CAPTCHA_RESPONSE_PATTERN;
import static com.guardedbox.constants.SecurityParameters.CAPTCHA_RESPONSE_HEADER;
import static com.guardedbox.constants.SecurityParameters.ENTROPY_EXPANDER_LENGTH;
import static com.guardedbox.constants.SecurityParameters.REGISTRATION_TOKEN_LENGTH;
import static com.guardedbox.constants.SecurityParameters.REGISTRATION_TOKEN_MIN_TTL;
import static com.guardedbox.constants.SecurityParameters.REGISTRATION_TOKEN_TTL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.dto.NewAccountDto;
import com.guardedbox.dto.ObtainRegistrationTokenDto;
import com.guardedbox.dto.RegistrationTokenDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.service.CaptchaVerificationService;
import com.guardedbox.service.EmailService;
import com.guardedbox.service.ExecutionTimeService;
import com.guardedbox.service.RandomService;
import com.guardedbox.service.transactional.AccountsService;
import com.guardedbox.service.transactional.RegistrationTokensService;

import java.sql.Timestamp;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Controller: Registration.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping("/api/registration")
@Validated
@PropertySource("classpath:email/email_en.properties")
public class RegistrationController {

    /** Execution time of the obtain registration token request (ms). */
    private static final long OBTAIN_REGISTRATION_TOKEN_EXECUTION_TIME = 3000L;

    /** URI of the front-end registration component, including the registration token parameter. */
    private static final String FRONT_REGISTRATION_COMPONENT_URI = "#/registration?token=%s";

    /** Property: internet.url. */
    private final String internetUrl;

    /** Property: registration.email.subject. */
    private final String registrationEmailSubject;

    /** Property: registration.email.body. */
    private final String registrationEmailBody;

    /** Property: registration.alreadyRegistered.email.subject. */
    private final String alreadyRegisteredEmailSubject;

    /** Property: registration.alreadyRegistered.email.body. */
    private final String alreadyRegisteredEmailBody;

    /** AccountsService. */
    private final AccountsService accountsService;

    /** RegistrationTokensService. */
    private final RegistrationTokensService registrationTokensService;

    /** ExecutionTimeService. */
    private final ExecutionTimeService executionTimeService;

    /** RandomService. */
    private final RandomService randomService;

    /** EmailService. */
    private final EmailService emailService;

    /** CaptchaVerificationService. */
    private final CaptchaVerificationService captchaVerificationService;

    /** PasswordEncoder. */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor with Attributes.
     * 
     * @param internetUrl Property: internet.url.
     * @param registrationEmailSubject Property: registration.email.subject.
     * @param registrationEmailBody Property: registration.email.body.
     * @param alreadyRegisteredEmailSubject Property: registration.alreadyRegistered.email.subject.
     * @param alreadyRegisteredEmailBody Property: registration.alreadyRegistered.email.body.
     * @param accountsService AccountsService.
     * @param registrationTokensService RegistrationTokensService.
     * @param executionTimeService ExecutionTimeService.
     * @param randomService Random Service.
     * @param emailService Email Service.
     * @param captchaVerificationService CaptchaVerificationService.
     * @param passwordEncoder PasswordEncoder.
     */
    public RegistrationController(
            @Value("${internet.url}") String internetUrl,
            @Value("${registration.email.subject}") String registrationEmailSubject,
            @Value("${registration.email.body}") String registrationEmailBody,
            @Value("${registration.already-registered.email.subject}") String alreadyRegisteredEmailSubject,
            @Value("${registration.already-registered.email.body}") String alreadyRegisteredEmailBody,
            @Autowired AccountsService accountsService,
            @Autowired RegistrationTokensService registrationTokensService,
            @Autowired ExecutionTimeService executionTimeService,
            @Autowired RandomService randomService,
            @Autowired EmailService emailService,
            @Autowired CaptchaVerificationService captchaVerificationService,
            @Autowired PasswordEncoder passwordEncoder) {
        this.internetUrl = internetUrl;
        this.registrationEmailSubject = registrationEmailSubject;
        this.registrationEmailBody = registrationEmailBody;
        this.alreadyRegisteredEmailSubject = alreadyRegisteredEmailSubject;
        this.alreadyRegisteredEmailBody = alreadyRegisteredEmailBody;
        this.accountsService = accountsService;
        this.registrationTokensService = registrationTokensService;
        this.executionTimeService = executionTimeService;
        this.randomService = randomService;
        this.emailService = emailService;
        this.captchaVerificationService = captchaVerificationService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generates a registration token, sends it by email, and stores it in the database.
     * 
     * @param obtainRegistrationTokenDto Object with the necessary data to generate and send a registration token.
     * @param captchaResponse Header with the response to the captcha challenge.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping("/obtain-registration-token")
    public SuccessDto obtainRegistrationToken(
            @RequestBody(required = true) @Valid ObtainRegistrationTokenDto obtainRegistrationTokenDto,
            @RequestHeader(name = CAPTCHA_RESPONSE_HEADER, required = true) @NotBlank @Pattern(regexp = CAPTCHA_RESPONSE_PATTERN) @Size(max = CAPTCHA_RESPONSE_MAX_LENGTH) String captchaResponse) {

        long currentTime = System.currentTimeMillis();

        // Check if the captcha response is valid.
        if (!captchaVerificationService.verifyCaptchaResponse(captchaResponse)) {
            throw new ServiceException("Captcha response is invalid");
        }

        // Check if a token was generated for the email a short time ago.
        RegistrationTokenDto prevRegistrationTokenDto = registrationTokensService.getRegistrationTokenByEmail(obtainRegistrationTokenDto.getEmail());
        if (prevRegistrationTokenDto != null && currentTime < prevRegistrationTokenDto.getExpeditionTime().getTime() + REGISTRATION_TOKEN_MIN_TTL) {
            throw new ServiceException(String.format(
                    "Registration token was not generated for email %s since another one was generated a short time ago",
                    obtainRegistrationTokenDto.getEmail()))
                            .setErrorCode("login.register-mail-just-sent");
        }

        // Check if the email is already registered.
        if (accountsService.isEmailRegistered(obtainRegistrationTokenDto.getEmail())) {

            emailService.send(
                    obtainRegistrationTokenDto.getEmail(),
                    alreadyRegisteredEmailSubject,
                    alreadyRegisteredEmailBody);

        } else {

            // Generate the registration token.
            String token = null;
            do {
                token = randomService.randomAlphanumericString(REGISTRATION_TOKEN_LENGTH);
            } while (registrationTokensService.existsToken(token));

            // Send it by email.
            emailService.send(
                    obtainRegistrationTokenDto.getEmail(),
                    registrationEmailSubject,
                    String.format(registrationEmailBody, internetUrl + String.format(FRONT_REGISTRATION_COMPONENT_URI, token)));

            // Store it in the database overwriting the previous one, in case it exists.
            RegistrationTokenDto registrationTokenDto = null;
            if (prevRegistrationTokenDto == null) {
                registrationTokenDto = new RegistrationTokenDto();
                registrationTokenDto.setEmail(obtainRegistrationTokenDto.getEmail());
                registrationTokenDto.setEntropyExpander(randomService.randomHexString(ENTROPY_EXPANDER_LENGTH));
            } else {
                registrationTokenDto = prevRegistrationTokenDto;
            }
            registrationTokenDto.setToken(token);
            registrationTokenDto.setExpeditionTime(new Timestamp(currentTime));
            registrationTokensService.saveRegistrationToken(registrationTokenDto);

        }

        // Fix execution time.
        executionTimeService.fix(currentTime, OBTAIN_REGISTRATION_TOKEN_EXECUTION_TIME);

        // Successful result.
        return new SuccessDto(true);

    }

    /**
     * @param token A registration token.
     * @param captchaResponse Header with the response to the captcha challenge.
     * @return The information of the introduced registration token.
     */
    @GetMapping("/get-registration-token-info")
    public RegistrationTokenDto getRegistrationTokenInfo(
            @RequestParam(name = "token", required = true) @NotBlank @Pattern(regexp = ALPHANUMERIC_PATTERN) @Size(min = REGISTRATION_TOKEN_LENGTH, max = REGISTRATION_TOKEN_LENGTH) String token,
            @RequestHeader(name = CAPTCHA_RESPONSE_HEADER, required = true) @NotBlank @Pattern(regexp = CAPTCHA_RESPONSE_PATTERN) @Size(max = CAPTCHA_RESPONSE_MAX_LENGTH) String captchaResponse) {

        long currentTime = System.currentTimeMillis();

        // Check if the captcha response is valid.
        if (!captchaVerificationService.verifyCaptchaResponse(captchaResponse)) {
            throw new ServiceException("Captcha response is invalid");
        }

        // Check if the registration token exists.
        RegistrationTokenDto registrationTokenDto = registrationTokensService.getRegistrationTokenByToken(token);
        if (registrationTokenDto == null) {
            throw new ServiceException(String.format("Registration token info was not recovered since token %s does not exist", token))
                    .setErrorCode("registration.registration-token-not-found");
        }

        // Check if the registration token is expired.
        if (currentTime > registrationTokenDto.getExpeditionTime().getTime() + REGISTRATION_TOKEN_TTL) {
            throw new ServiceException(String.format("Registration token info was not recovered since token %s is expired", token))
                    .setErrorCode("registration.registration-token-expired");
        }

        return registrationTokenDto;

    }

    /**
     * Registers a new account.
     * 
     * @param newAccountDto Object with the new account data.
     * @param captchaResponse Header with the response to the captcha challenge.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping("/register-account")
    public SuccessDto registerAccount(
            @RequestBody(required = true) @Valid NewAccountDto newAccountDto,
            @RequestHeader(name = CAPTCHA_RESPONSE_HEADER, required = true) @NotBlank @Pattern(regexp = CAPTCHA_RESPONSE_PATTERN) @Size(max = CAPTCHA_RESPONSE_MAX_LENGTH) String captchaResponse) {

        long currentTime = System.currentTimeMillis();

        // Check if the captcha response is valid.
        if (!captchaVerificationService.verifyCaptchaResponse(captchaResponse)) {
            throw new ServiceException("Captcha response is invalid");
        }

        // Check if the registration token exists.
        RegistrationTokenDto registrationTokenDto = registrationTokensService.getRegistrationTokenByToken(newAccountDto.getRegistrationToken());
        if (registrationTokenDto == null) {
            throw new ServiceException(String.format(
                    "Account was not registered since token %s does not exist",
                    newAccountDto.getRegistrationToken()))
                            .setErrorCode("registration.registration-token-not-found");
        }

        // Check if the registration token is expired.
        if (currentTime > registrationTokenDto.getExpeditionTime().getTime() + REGISTRATION_TOKEN_TTL) {
            throw new ServiceException(String.format(
                    "Account was not registered since token %s is expired",
                    newAccountDto.getRegistrationToken()))
                            .setErrorCode("registration.registration-token-expired");
        }

        // Create the new account.
        newAccountDto.setEmail(registrationTokenDto.getEmail());
        newAccountDto.setPassword(passwordEncoder.encode(newAccountDto.getPassword()));
        newAccountDto.setSecurityAnswers(passwordEncoder.encode(newAccountDto.getSecurityAnswers()));
        accountsService.newAccount(newAccountDto);

        // Delete the registration token.
        registrationTokensService.deleteRegistrationToken(registrationTokenDto);

        // Successful result.
        return new SuccessDto(true);

    }

}
