package com.guardedbox.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Response to requests that have thrown a ServiceException.
 *
 * @author s3curitybug@gmail.com
 *
 */
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

}
