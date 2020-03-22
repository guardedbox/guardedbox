package com.guardedbox.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Challenge.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class ChallengeDto
        implements Serializable {

    /** Challenge. */
    private String challenge;

    /** Expiration Time. */
    @JsonIgnore
    private Long expirationTime;

}
