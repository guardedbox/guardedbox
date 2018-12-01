package com.guardedbox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.constants.SessionAttributes;
import com.guardedbox.dto.DeleteSecretDto;
import com.guardedbox.dto.EditSecretDto;
import com.guardedbox.dto.NewSecretDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.service.transactional.SecretsService;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Controller: My Secrets.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping("/api/my-secrets")
@Validated
public class MySecretsController {

    /** SecretsService. */
    private final SecretsService secretsService;

    /** Current Session. */
    private final HttpSession session;

    /**
     * Constructor with Attributes.
     * 
     * @param secretsService SecretsService.
     * @param session Current Session.
     */
    public MySecretsController(
            @Autowired SecretsService secretsService,
            @Autowired HttpSession session) {
        this.secretsService = secretsService;
        this.session = session;
    }

    /**
     * @return All the secrets belonging to the current session account.
     */
    @GetMapping("/get-all")
    public List<SecretDto> getAllMySecrets() {
        Long accountId = (Long) session.getAttribute(SessionAttributes.ACCOUNT_ID);
        return secretsService.getAllAccountSecrets(accountId);
    }

    /**
     * Stores a new secret, belonging to the current session account.
     * 
     * @param newSecretDto Object with the new secret data.
     * @return Object with the stored secret data.
     */
    @PostMapping("/new-secret")
    public SecretDto newSecret(
            @RequestBody(required = true) @Valid NewSecretDto newSecretDto) {
        Long accountId = (Long) session.getAttribute(SessionAttributes.ACCOUNT_ID);
        return secretsService.newSecret(accountId, newSecretDto);
    }

    /**
     * Edits a secret. It must belong to the current session account.
     * 
     * @param editSecretDto Object with the secret new data.
     * @return Object with the edited secret data.
     */
    @PostMapping("/edit-secret")
    public SecretDto editSecret(
            @RequestBody(required = true) @Valid EditSecretDto editSecretDto) {
        Long accountId = (Long) session.getAttribute(SessionAttributes.ACCOUNT_ID);
        return secretsService.editSecret(accountId, editSecretDto);
    }

    /**
     * Deletes a secret. It must belong to the current session account.
     * 
     * @param deleteSecretDto Object with the secret to be deleted data.
     * @return Object with the deleted secret data.
     */
    @PostMapping("/delete-secret")
    public SecretDto deleteSecret(
            @RequestBody(required = true) @Valid DeleteSecretDto deleteSecretDto) {
        Long accountId = (Long) session.getAttribute(SessionAttributes.ACCOUNT_ID);
        return secretsService.deleteSecret(accountId, deleteSecretDto);
    }

}
