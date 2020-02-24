package com.guardedbox.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class AccountDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -5507706737709209311L;

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

}
