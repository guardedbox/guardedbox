package com.guardedbox.controller;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_64BYTES_LENGTH;
import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.PathParameters.API_BASE_PATH;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guardedbox.dto.CreateRegistrationDto;
import com.guardedbox.dto.RegistrationDto;
import com.guardedbox.dto.SuccessDto;
import com.guardedbox.properties.SecurityParametersProperties;
import com.guardedbox.service.ExecutionTimeService;
import com.guardedbox.service.transactional.RegistrationsService;

import lombok.RequiredArgsConstructor;

/**
 * Controller: Registrations.
 *
 * @author s3curitybug@gmail.com
 *
 */
@RestController
@RequestMapping(API_BASE_PATH + "registrations")
@Validated
@RequiredArgsConstructor
public class RegistrationsController {

    /** SecurityParametersProperties. */
    private final SecurityParametersProperties securityParameters;

    /** RegistrationsService. */
    private final RegistrationsService registrationsService;

    /** ExecutionTimeService. */
    private final ExecutionTimeService executionTimeService;

    /**
     * @param token A registration token.
     * @return The Registration corresponding to the introduced token.
     */
    @GetMapping()
    public RegistrationDto getRegistration(
            @RequestParam(name = "token", required = true) @NotBlank @Pattern(regexp = ALPHANUMERIC_PATTERN) @Size(min = ALPHANUMERIC_64BYTES_LENGTH, max = ALPHANUMERIC_64BYTES_LENGTH) String token) {

        // Return the Registration.
        return registrationsService.getAndCheckRegistrationByToken(token);

    }

    /**
     * Creates a Registration, and sends its token.
     *
     * @param createRegistrationDto Object with the necessary data to create a Registration and send its token.
     * @return Object indicating if the execution was successful.
     */
    @PostMapping()
    public SuccessDto createRegistration(
            @RequestBody(required = true) @Valid CreateRegistrationDto createRegistrationDto) {

        long startTime = System.currentTimeMillis();

        // Create Registration.
        registrationsService.createRegistration(createRegistrationDto);

        // Fix execution time.
        executionTimeService.fix(startTime, securityParameters.getRegistrationExecutionTime());

        // Successful result.
        return new SuccessDto(true);

    }

}
