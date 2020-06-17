package com.guardedbox.dto;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: ExMember.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class ExMemberDto
        implements Serializable {

    /** Ex Member ID. */
    @JsonIgnore
    private UUID exMemberId;

    /** Email. */
    private String email;

    /** Cause. */
    private String cause;

}
