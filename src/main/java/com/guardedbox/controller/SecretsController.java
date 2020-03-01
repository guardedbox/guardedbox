package com.guardedbox.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.dto.CreateSecretDto;
import com.guardedbox.dto.EditSecretDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.service.SessionAccountService;
import com.guardedbox.service.transactional.SecretsService;

import lombok.RequiredArgsConstructor;

/**
 * Controller: Secrets.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping("/api/secrets")
@Validated
@RequiredArgsConstructor
public class SecretsController {

    /** SecretsService. */
    private final SecretsService secretsService;

    /** SessionAccountService. */
    private final SessionAccountService sessionAccount;

    /**
     * @return All the secrets belonging to the current session account.
     */
    @GetMapping()
    public List<SecretDto> getSecrets() {

        return secretsService.getSecretsByOwnerAccountId(sessionAccount.getAccountId());

    }

    /**
     * Creates Secret, belonging to the current session account.
     *
     * @param createSecretDto Object with the necessary data to create a Secret.
     * @return Object with the stored secret data.
     */
    @PostMapping()
    public SecretDto createSecret(
            @RequestBody(required = true) @Valid CreateSecretDto createSecretDto) {

        return secretsService.createSecret(sessionAccount.getAccountId(), createSecretDto);

    }

    /**
     * Edits a secret. It must belong to the current session account.
     *
     * @param secretId ID of the secret to be edited.
     * @param editSecretDto Object with the secret edition data.
     * @return Object with the edited secret data.
     */
    @PostMapping("/{secret-id}")
    public SecretDto editSecret(
            @PathVariable(name = "secret-id", required = true) @NotNull UUID secretId,
            @RequestBody(required = true) @Valid EditSecretDto editSecretDto) {

        return secretsService.editSecret(sessionAccount.getAccountId(), secretId, editSecretDto);

    }

    /**
     * Deletes a secret. It must belong to the current session account.
     *
     * @param secretId ID of the secret to be deleted.
     * @return Object with the deleted secret data.
     */
    @DeleteMapping("/{secret-id}")
    public SecretDto deleteSecret(
            @PathVariable(name = "secret-id", required = true) @NotNull UUID secretId) {

        return secretsService.deleteSecret(sessionAccount.getAccountId(), secretId);

    }

}
