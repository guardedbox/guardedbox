package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Body of the create registration request.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class CreateRegistrationDto
        extends MinedChallengeResponseDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -5104883037954008785L;

    /** Email. */
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String email;

}
