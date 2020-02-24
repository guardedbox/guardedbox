package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.MINING_NONCE_LENGTH;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO: Mined Challenge Response.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MinedChallengeResponseDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 5990244027641898565L;

    /** Mined Challenge Response. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = MINING_NONCE_LENGTH, max = MINING_NONCE_LENGTH)
    private String minedChallengeResponse;

}
