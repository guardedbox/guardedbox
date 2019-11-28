package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.SECRET_NAME_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.SECRET_VALUE_MAX_LENGTH;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DTO: Body of the edit secret request.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class EditSecretDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -3327969144838571389L;

    /** Name. */
    @NotBlank
    @Size(max = SECRET_NAME_MAX_LENGTH)
    private String name;

    /** Value. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(max = SECRET_VALUE_MAX_LENGTH)
    private String value;

    /** Sharings. */
    @NotNull
    private List<@NotNull @Valid EditSecretSharingDto> sharings;

    /**
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(
            String name) {
        this.name = name;
    }

    /**
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(
            String value) {
        this.value = value;
    }

    /**
     * @return The sharings.
     */
    public List<EditSecretSharingDto> getSharings() {
        return sharings;
    }

    /**
     * @param sharings The sharings to set.
     */
    public void setSharings(
            List<EditSecretSharingDto> sharings) {
        this.sharings = sharings;
    }

}
