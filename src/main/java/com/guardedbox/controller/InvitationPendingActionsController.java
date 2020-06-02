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

import com.guardedbox.dto.CreateInvitationPendingActionDto;
import com.guardedbox.dto.InvitationPendingActionDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.service.SessionAccountService;
import com.guardedbox.service.transactional.InvitationPendingActionsService;

import lombok.RequiredArgsConstructor;

/**
 * Controller: InvitationPendingAction.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping(API_BASE_PATH + "invitation-pending-action")
@Validated
@RequiredArgsConstructor
public class InvitationPendingActionsController {

    /** InvitationPendingActionService. */
    private final InvitationPendingActionsService invitationPendingActionsService;

    /** SessionAccountService. */
    private final SessionAccountService sessionAccount;

    /**
     * @param secretId An ID representing a secret.
     * @return All the invitation pending actions corresponding to the introduced secret ID.
     */
    @GetMapping("/secret/{secret-id}")
    public List<InvitationPendingActionDto> getInvitationPendingActionsBySecretId(
            @PathVariable(name = "secret-id", required = true) @NotNull UUID secretId) {

        return invitationPendingActionsService.getInvitationPendingActionsBySecretId(sessionAccount.getAccountId(), secretId);

    }

    /**
     * @param groupId An ID representing a group.
     * @return All the invitation pending actions corresponding to the introduced group ID.
     */
    @GetMapping("/group/{group-id}")
    public List<InvitationPendingActionDto> getInvitationPendingActionsByGroupId(
            @PathVariable(name = "group-id", required = true) @NotNull UUID groupId) {

        return invitationPendingActionsService.getInvitationPendingActionsByGroupId(sessionAccount.getAccountId(), groupId);

    }

    /**
     * Creates an InvitationPendingAction.
     *
     * @param createInvitationPendingActionDto Object with the necessary data to create an InvitationPendingAction.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping()
    public SuccessDto createInvitationPendingAction(
            @RequestBody(required = true) @Valid CreateInvitationPendingActionDto createInvitationPendingActionDto) {

        invitationPendingActionsService.createInvitationPendingAction(createInvitationPendingActionDto, sessionAccount.getAccount());

        return new SuccessDto(true);

    }

    /**
     * Deletes an invitation pending action associated to a receiver email and a secret ID.
     *
     * @param secretId The secret ID.
     * @param receiverEmail The receiver email.
     * @return Object indicating if the execution was successful.
     */
    @DeleteMapping("/secret/{secret-id}")
    public SuccessDto deleteInvitationPendingActionBySecretId(
            @PathVariable(name = "secret-id", required = true) @NotNull UUID secretId,
            @RequestParam(name = "receiver-email", required = true) @NotBlank @Email(regexp = EMAIL_PATTERN) @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH) String receiverEmail) {

        invitationPendingActionsService.deleteInvitationPendingActionBySecretId(sessionAccount.getAccountId(), secretId, receiverEmail);

        return new SuccessDto(true);

    }

    /**
     * Deletes an invitation pending action associated to a receiver email and a group ID.
     *
     * @param groupId The group ID.
     * @param receiverEmail The receiver email.
     * @return Object indicating if the execution was successful.
     */
    @DeleteMapping("/group/{group-id}")
    public SuccessDto deleteInvitationPendingActionByGroupId(
            @PathVariable(name = "group-id", required = true) @NotNull UUID groupId,
            @RequestParam(name = "receiver-email", required = true) @NotBlank @Email(regexp = EMAIL_PATTERN) @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH) String receiverEmail) {

        invitationPendingActionsService.deleteInvitationPendingActionByGroupId(sessionAccount.getAccountId(), groupId, receiverEmail);

        return new SuccessDto(true);

    }

}
