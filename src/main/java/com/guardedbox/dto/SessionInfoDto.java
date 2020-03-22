package com.guardedbox.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Response to the session info request.
 *
 * @author s3curitybug@gmail.com
 *
 */
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@SuppressWarnings("serial")
public class SessionInfoDto
        implements Serializable {

    /** Indicates if the current session is authenticated. */
    private Boolean authenticated;

    /** Indicates the current session email, in case it is authenticated, or null otherwise. */
    private String email;

    /** Success. */
    private Boolean success;

}
