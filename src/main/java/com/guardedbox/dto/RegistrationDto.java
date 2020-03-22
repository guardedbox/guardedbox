package com.guardedbox.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Registration.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class RegistrationDto
        implements Serializable {

    /** Registration ID. */
    @JsonIgnore
    private UUID registrationId;

    /** Email. */
    private String email;

    /** Token. */
    private String token;

    /** Expedition Time. */
    @JsonIgnore
    private Timestamp expeditionTime;

}
