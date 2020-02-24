package com.guardedbox.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Response to the session info request.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class SessionInfoDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 7948660755765688188L;

    /** Indicates if the current session is authenticated. */
    private boolean authenticated;

    /** Indicates the current session email, in case it is authenticated, or null otherwise. */
    private String email;

}
