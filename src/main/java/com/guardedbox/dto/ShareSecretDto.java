package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.Constraints.SECRET_VALUE_MAX_LENGTH;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Body of the share secret request.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class ShareSecretDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -6157239653303950240L;

    /** Receiver Email. */
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String receiverEmail;

    /** Value. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(max = SECRET_VALUE_MAX_LENGTH)
    private String value;

}
