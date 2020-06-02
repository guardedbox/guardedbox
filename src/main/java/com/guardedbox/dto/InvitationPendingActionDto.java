package com.guardedbox.dto;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: InvitationPendingAction.
 *
 * @author s3curitybug@gmail.com
 *
 */
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@SuppressWarnings("serial")
public class InvitationPendingActionDto
        implements Serializable {

    /** Invitation Pending Action ID. */
    @JsonIgnore
    private UUID invitationPendingActionId;

    /** Receiver Email. */
    private String receiverEmail;

    /** Secret ID. */
    private UUID secretId;

    /** Group ID. */
    private UUID groupId;

    /** Email Registered. */
    private Boolean emailRegistered;

}
