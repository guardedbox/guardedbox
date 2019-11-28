package com.guardedbox.controller;

import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.dto.AccountWithEncryptionPublicKeyDto;
import com.guardedbox.dto.AccountWithSecretsDto;
import com.guardedbox.dto.ShareSecretDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.service.SessionAccountService;
import com.guardedbox.service.transactional.SharedSecretsService;

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

    /** SessionAccountService. */
    private final SessionAccountService sessionAccount;

    /**
     * Constructor with Attributes.
     *
     * @param sharedSecretsService SharedSecretsService.
     * @param sessionAccount SessionAccountService.
     */
    public SharedSecretsController(
            @Autowired SharedSecretsService sharedSecretsService,
            @Autowired SessionAccountService sessionAccount) {
        this.sharedSecretsService = sharedSecretsService;
        this.sessionAccount = sessionAccount;
    }

    /**
     * @return All the secrets shared with the current session account, grouped by owner email.
     */
    @GetMapping("/received")
    public List<AccountWithSecretsDto> getSharedSecrets() {

        return sharedSecretsService.getSecretsSharedWithAccount(sessionAccount.getAccountId());

    }

    /**
     * @param secretId A secret ID representing a secret. It must belong to the current session account.
     * @return All the accounts with which the introduced secret is shared.
     */
    @GetMapping("/sent/{secret-id}/receiver-accounts")
    public List<AccountWithEncryptionPublicKeyDto> getSharedSecretReceiverAccounts(
            @PathVariable(name = "secret-id", required = true) @NotNull @Positive Long secretId) {

        return sharedSecretsService.getSharedSecretReceiverAccounts(sessionAccount.getAccountId(), secretId);

    }

    /**
     * Shares a secret.
     *
     * @param secretId The secret ID of the secret to be shared.
     * @param shareSecretDto Object with the necessary data to share the secret.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping("/sent/{secret-id}")
    public SuccessDto shareSecret(
            @PathVariable(name = "secret-id", required = true) @NotNull @Positive Long secretId,
            @RequestBody(required = true) @Valid ShareSecretDto shareSecretDto) {

        sharedSecretsService.shareSecret(sessionAccount.getAccountId(), secretId, shareSecretDto);
        return new SuccessDto(true);

    }

    /**
     * Unshares a secret.
     *
     * @param secretId The secret ID of the secret to be unshared.
     * @param receiverEmail The email from which the secret will be unshared.
     * @return Object indicating if the execution was successful.
     */
    @DeleteMapping("/sent/{secret-id}")
    public SuccessDto unshareSecret(
            @PathVariable(name = "secret-id", required = true) @NotNull @Positive Long secretId,
            @RequestParam(name = "receiver-email") @NotBlank @Email(regexp = EMAIL_PATTERN) @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH) String receiverEmail) {

        sharedSecretsService.unshareSecret(sessionAccount.getAccountId(), secretId, receiverEmail);
        return new SuccessDto(true);

    }

    /**
     * Unshares a secret shared to the current session account.
     *
     * @param secretId The secret ID of the secret to be unshared.
     * @return Object indicating if the execution was successful.
     */
    @DeleteMapping("/received/{secret-id}")
    public SuccessDto rejectSharedSecret(
            @PathVariable(name = "secret-id", required = true) @NotNull @Positive Long secretId) {

        sharedSecretsService.rejectSharedSecret(secretId, sessionAccount.getAccountId());
        return new SuccessDto(true);

    }

}
