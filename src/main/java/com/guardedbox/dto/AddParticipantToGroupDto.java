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

/**
 * DTO: Body of the add participant to group request.
 *
 * @author s3curitybug@gmail.com
 *
 */
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

    /**
     * @return The groupId.
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * @param groupId The groupId to set.
     */
    public void setGroupId(
            Long groupId) {
        this.groupId = groupId;
    }

    /**
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(
            String email) {
        this.email = email;
    }

    /**
     * @return The encryptedGroupKey.
     */
    public String getEncryptedGroupKey() {
        return encryptedGroupKey;
    }

    /**
     * @param encryptedGroupKey The encryptedGroupKey to set.
     */
    public void setEncryptedGroupKey(
            String encryptedGroupKey) {
        this.encryptedGroupKey = encryptedGroupKey;
    }

}
