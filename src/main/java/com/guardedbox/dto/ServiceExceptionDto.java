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
@SuppressWarnings("serial")
public class ServiceExceptionDto
        implements Serializable {

    /** Error Code. */
    private String errorCode;

    /** Additional Data. */
    private Map<String, Object> additionalData;

    /** Success. */
    private Boolean success;

}
