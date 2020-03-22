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
@SuppressWarnings("serial")
public class SuccessDto
        implements Serializable {

    /** Success. */
    private boolean success;

}
