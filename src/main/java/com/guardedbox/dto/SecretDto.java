package com.guardedbox.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class SecretDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -3608188899441682632L;

    /** Secret ID. */
    private UUID secretId;

    /** Name. */
    private String name;

    /** Value. */
    private String value;

}
