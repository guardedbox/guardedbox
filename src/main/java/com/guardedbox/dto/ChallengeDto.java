package com.guardedbox.dto;

import java.io.Serializable;

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
public class ChallengeDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -347763538667651558L;

    /** Challenge. */
    private String challenge;

    /** Expiration Time. */
    private Long expirationTime;

}
