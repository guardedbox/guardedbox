package com.guardedbox.controller;

import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.constants.SessionAttributes;
import com.guardedbox.dto.AccountWithPublicKeyDto;
import com.guardedbox.dto.AccountWithSecretsDto;
import com.guardedbox.dto.RejectSharedSecretDto;
import com.guardedbox.dto.ShareSecretDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.dto.UnshareSecretDto;
import com.guardedbox.service.transactional.AccountsService;
import com.guardedbox.service.transactional.SharedSecretsService;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Controller: Shared Secrets.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping("/api/shared-secrets")
@Validated
public class SharedSecretsController {

    /** SharedSecretsService. */
    private final SharedSecretsService sharedSecretsService;

    /** AccountsService. */
    private final AccountsService accountsService;

    /** Current Session. */
    private final HttpSession session;

    /**
     * Constructor with Attributes.
     * 
     * @param sharedSecretsService SharedSecretsService.
     * @param accountsService AccountsService.
     * @param session Current Session.
     */
    public SharedSecretsController(
            @Autowired SharedSecretsService sharedSecretsService,
            @Autowired AccountsService accountsService,
            @Autowired HttpSession session) {
        this.sharedSecretsService = sharedSecretsService;
        this.accountsService = accountsService;
        this.session = session;
    }

    /**
     * @param email An email.
     * @return The public key of the account corresponding to the introduced email, in case it is registered.
     */
    @GetMapping("/get-account-public-key")
    public AccountWithPublicKeyDto getAccountPublicKey(
            @RequestParam(name = "email", required = true) @NotBlank @Email(regexp = EMAIL_PATTERN) @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH) String email) {
        Long accountId = (Long) session.getAttribute(SessionAttributes.ACCOUNT_ID);
        return accountsService.getAndCheckAccountWithPublicKey(email, accountId);
    }

    /**
     * @param secretId A secret ID representing a secret. It must belong to the current session account.
     * @return All the accounts with which the introduced secret is shared.
     */
    @GetMapping("/get-shared-secret-accounts")
    public List<AccountWithPublicKeyDto> getSharedSecretAccounts(
            @RequestParam(name = "secret-id", required = true) @NotNull @Positive Long secretId) {
        Long accountId = (Long) session.getAttribute(SessionAttributes.ACCOUNT_ID);
        return sharedSecretsService.getSharedSecretAccounts(accountId, secretId);
    }

    /**
     * @return All the secrets shared with the current session account, grouped by owner email.
     */
    @GetMapping("/get-all-secrets-shared-with-me")
    public List<AccountWithSecretsDto> getAllSecretsSharedWithMe() {
        Long accountId = (Long) session.getAttribute(SessionAttributes.ACCOUNT_ID);
        return sharedSecretsService.getSecretsSharedWithAccount(accountId);
    }

    /**
     * Shares a secret.
     * 
     * @param shareSecretDto Object with the secret to be shared data.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping("/share-secret")
    public SuccessDto shareSecret(
            @RequestBody(required = true) @Valid ShareSecretDto shareSecretDto) {
        Long accountId = (Long) session.getAttribute(SessionAttributes.ACCOUNT_ID);
        sharedSecretsService.shareSecret(accountId, shareSecretDto);
        return new SuccessDto(true);
    }

    /**
     * Unshares a secret.
     * 
     * @param unshareSecretDto Object with the secret to be unshared data.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping("/unshare-secret")
    public SuccessDto unshareSecret(
            @RequestBody(required = true) @Valid UnshareSecretDto unshareSecretDto) {
        Long accountId = (Long) session.getAttribute(SessionAttributes.ACCOUNT_ID);
        sharedSecretsService.unshareSecret(accountId, unshareSecretDto);
        return new SuccessDto(true);
    }

    /**
     * Unshares a secret shared to the current session account.
     * 
     * @param rejectSharedSecretDto Object with the secret to be unshared data.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping("/reject-shared-secret")
    public SuccessDto rejectSharedSecret(
            @RequestBody(required = true) @Valid RejectSharedSecretDto rejectSharedSecretDto) {
        Long accountId = (Long) session.getAttribute(SessionAttributes.ACCOUNT_ID);
        sharedSecretsService.rejectSharedSecret(accountId, rejectSharedSecretDto);
        return new SuccessDto(true);
    }

}
