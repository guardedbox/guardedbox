package com.guardedbox.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Ex Member Causes Enum.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RequiredArgsConstructor
@Getter
public enum ExMemberCause {

    /** Secret Unshared By Owner. */
    SECRET_UNSHARED_BY_OWNER("shared-secrets.secret-unshared-by-owner"),

    /** Secret Rejected By Receiver. */
    SECRET_REJECTED_BY_RECEIVER("shared-secrets.secret-rejected-by-receiver"),

    /** Group Participant Removed By Owner. */
    GROUP_PARTICIPANT_REMOVED_BY_OWNER("groups.participant-removed-by-owner"),

    /** Group Participant Left. */
    GROUP_PARTICIPANT_LEFT("groups.participant-left"),

    /** Account Was Deleted. */
    ACCOUNT_WAS_DELETED("accounts.account-was-deleted");

    /** Cause Name. */
    private final String causeName;

}
