package com.guardedbox.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Account.
 * Contains the following fields: accountId, email, salt.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class AccountWithSaltDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 8311168168735591089L;

    /** Account ID. */
    @JsonIgnore
    private Long accountId;

    /** Email. */
    private String email;

    /** Salt. */
    private String salt;

}
