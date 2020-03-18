package com.guardedbox.dto;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class GroupDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -5684218439247544348L;

    /** Group ID. */
    private UUID groupId;

    /** Owner Account. */
    private AccountDto ownerAccount;

    /** Name. */
    private String name;

    /** Encrypted Group Key. */
    private String encryptedGroupKey;

}
