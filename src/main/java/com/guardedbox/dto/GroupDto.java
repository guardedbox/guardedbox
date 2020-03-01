package com.guardedbox.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class GroupDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -3630417901231355279L;

    /** Group ID. */
    private UUID groupId;

    /** Owner Account. */
    private AccountWithEncryptionPublicKeyDto ownerAccount;

    /** Name. */
    private String name;

    /** Encrypted Group Key. */
    private String encryptedGroupKey;

}
