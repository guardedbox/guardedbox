package com.guardedbox.controller;

import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.PathParameters.API_BASE_PATH;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.ExMemberDto;
import com.guardedbox.dto.ShareSecretDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.service.SessionAccountService;
import com.guardedbox.service.transactional.SharedSecretsService;

import lombok.RequiredArgsConstructor;

/**
 * Controller: Shared Secrets.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping(API_BASE_PATH + "shared-secrets")
@Validated
@RequiredArgsConstructor
public class SharedSecretsController {

    /** SharedSecretsService. */
    private final SharedSecretsService sharedSecretsService;

    /** SessionAccountService. */
    private final SessionAccountService sessionAccount;

    /**
     * @return All the secrets shared with the current session account, grouped by owner email.
     */
    @GetMapping("/received")
    public List<AccountDto> getSharedSecrets() {

        return sharedSecretsService.getSecretsSharedWithAccount(sessionAccount.getAccountId());

    }

    /**
     * @param secretId A secret ID representing a secret. It must belong to the current session account.
     * @return All the accounts with which the introduced secret is shared.
     */
    @GetMapping("/sent/{secret-id}/receiver-accounts")
    public List<AccountDto> getSharedSecretReceiverAccounts(
            @PathVariable(name = "secret-id", required = true) @NotNull UUID secretId) {

        return sharedSecretsService.getSharedSecretReceiverAccounts(sessionAccount.getAccountId(), secretId);

    }

    /**
     * @param secretId A secret ID representing a secret. It must belong to the current session account.
     * @return All the ex members with which the introduced secret was shared.
     */
    @GetMapping("/sent/{secret-id}/ex-members")
    public List<ExMemberDto> getSharedSecretExMembers(
            @PathVariable(name = "secret-id", required = true) @NotNull UUID secretId) {

        return sharedSecretsService.getSharedSecretExMembers(sessionAccount.getAccountId(), secretId);

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
            @PathVariable(name = "secret-id", required = true) @NotNull UUID secretId,
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
            @PathVariable(name = "secret-id", required = true) @NotNull UUID secretId,
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
            @PathVariable(name = "secret-id", required = true) @NotNull UUID secretId) {

        sharedSecretsService.rejectSharedSecret(secretId, sessionAccount.getAccountId());
        return new SuccessDto(true);

    }

    /**
     * Forgets a shared secret ex member.
     *
     * @param secretId The secret ID of the shared secret.
     * @param email The email of the ex member.
     * @return Object indicating if the execution was successful.
     */
    @DeleteMapping("/sent/{secret-id}/ex-member")
    public SuccessDto forgetSharedSecretExMember(
            @PathVariable(name = "secret-id", required = true) @NotNull UUID secretId,
            @RequestParam(name = "email", required = true) @NotBlank @Email(regexp = EMAIL_PATTERN) @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH) String email) {

        sharedSecretsService.forgetSharedSecretExMember(sessionAccount.getAccountId(), secretId, email);
        return new SuccessDto(true);

    }

}
