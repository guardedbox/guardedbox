package com.guardedbox.controller;

import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.dto.AccountWithEncryptionPublicKeyDto;
import com.guardedbox.dto.AccountWithSaltDto;
import com.guardedbox.dto.CreateAccountDto;
import com.guardedbox.dto.RegistrationDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.service.CryptoCaptchaService;
import com.guardedbox.service.transactional.AccountsService;
import com.guardedbox.service.transactional.RegistrationsService;

import lombok.RequiredArgsConstructor;

/**
 * Controller: Accounts.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping("/api/accounts")
@Validated
@RequiredArgsConstructor
public class AccountsController {

    /** AccountsService. */
    private final AccountsService accountsService;

    /** RegistrationsService. */
    private final RegistrationsService registrationsService;

    /** CryptoCaptchaService. */
    private final CryptoCaptchaService cryptoCaptchaService;

    /**
     * @param email An email.
     * @return The salt of the account corresponding to the introduced email.
     */
    @GetMapping("/salt")
    public AccountWithSaltDto getAccountSalt(
            @RequestParam(name = "email", required = true) @NotBlank @Email(regexp = EMAIL_PATTERN) @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH) String email) {

        return accountsService.getAndCheckAccountWithSaltByEmail(email);

    }

    /**
     * @param email An email.
     * @return The encryption public key of the account corresponding to the introduced email.
     */
    @GetMapping("/encryption-public-key")
    public AccountWithEncryptionPublicKeyDto getAccountEncryptionPublicKey(
            @RequestParam(name = "email", required = true) @NotBlank @Email(regexp = EMAIL_PATTERN) @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH) String email) {

        return accountsService.getAndCheckAccountWithEncryptionPublicKeyByEmail(email);

    }

    /**
     * Creates an Account.
     *
     * @param createAccountDto Object with the necessary data to create an Account.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping()
    public SuccessDto createAccount(
            @RequestBody(required = true) @Valid CreateAccountDto createAccountDto) {

        // Verify the crypto-captcha.
        cryptoCaptchaService.verify(createAccountDto);

        // Get the registration, checking if it exists and is not expired.
        RegistrationDto registrationDto = registrationsService.getAndCheckRegistrationByToken(createAccountDto.getRegistrationToken());

        // Create the account.
        createAccountDto.setEmail(registrationDto.getEmail());
        accountsService.createAccount(createAccountDto);

        // Delete the registration.
        registrationsService.deleteRegistration(registrationDto.getRegistrationId());

        // Successful result.
        return new SuccessDto(true);

    }

}
