package com.guardedbox.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO: Response to successful or unsuccessful requests.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 5354004949662300001L;

    /** Success. */
    private boolean success;

}
