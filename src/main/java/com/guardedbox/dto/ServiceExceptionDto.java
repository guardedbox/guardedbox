package com.guardedbox.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Response to requests that have thrown a ServiceException.
 *
 * @author s3curitybug@gmail.com
 *
 */
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class ServiceExceptionDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -9120666933873524976L;

    /** Error Code. */
    private String errorCode;

    /** Additional Data. */
    private Map<String, Object> additionalData;

    /** Success. */
    private Boolean success;

}
