package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.BASE64_JSON_PATTERN;
import static com.guardedbox.constants.Constraints.SECRET_VALUE_MAX_LENGTH;

import java.io.Serializable;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Subattribute of the body of the edit group request.
 *
 * @author s3curitybug@gmail.com
 *
 */
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@SuppressWarnings("serial")
public class EditGroupEditSecretDto
        implements Serializable {

    /** Secret ID. */
    @NotNull
    private UUID secretId;

    /** Value. */
    @NotBlank
    @Pattern(regexp = BASE64_JSON_PATTERN)
    @Size(max = SECRET_VALUE_MAX_LENGTH)
    private String value;

}
