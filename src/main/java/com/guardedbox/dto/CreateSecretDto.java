package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.BASE64_44BYTES_LENGTH;
import static com.guardedbox.constants.Constraints.BASE64_JSON_PATTERN;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.SECRET_VALUE_MAX_LENGTH;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Body of the create secret request.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class CreateSecretDto
        implements Serializable {

    /** Value. */
    @NotBlank
    @Pattern(regexp = BASE64_JSON_PATTERN)
    @Size(max = SECRET_VALUE_MAX_LENGTH)
    private String value;

    /** Encrypted Key. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_44BYTES_LENGTH, max = BASE64_44BYTES_LENGTH)
    private String encryptedKey;

}
