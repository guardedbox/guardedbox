package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTED_KEY_LENGTH;
import static com.guardedbox.constants.Constraints.GROUP_NAME_MAX_LENGTH;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DTO: Body of the create secret request.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class CreateGroupDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -1781168530115693520L;

    /** Name. */
    @NotBlank
    @Size(max = GROUP_NAME_MAX_LENGTH)
    private String name;

    /** Encrypted Key. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(max = ENCRYPTED_KEY_LENGTH)
    private String encryptedKey;

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
     * @return The encryptedKey.
     */
    public String getEncryptedKey() {
        return encryptedKey;
    }

    /**
     * @param encryptedKey The encryptedKey to set.
     */
    public void setEncryptedKey(
            String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

}
