package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTED_KEY_LENGTH;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Body of the add participant to group request.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class AddParticipantToGroupDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -5859332858555187140L;

    /** Group Id. */
    @JsonIgnore
    private Long groupId;

    /** Email. */
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String email;

    /** Encrypted Group Key. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(max = ENCRYPTED_KEY_LENGTH)
    private String encryptedGroupKey;

}
