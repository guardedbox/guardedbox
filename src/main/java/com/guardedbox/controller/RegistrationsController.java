package com.guardedbox.controller;

import static com.guardedbox.constants.Api.API_BASE_PATH;
import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.SecurityParameters.REGISTRATION_TOKEN_LENGTH;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Value;
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

    /** Property: security-parameters.registration.execution-time. */
    @Value("${security-parameters.registration.execution-time}")
    private final long createRegistrationExecutionTime;

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
            @RequestParam(name = "token", required = true) @NotBlank @Pattern(regexp = ALPHANUMERIC_PATTERN) @Size(min = REGISTRATION_TOKEN_LENGTH, max = REGISTRATION_TOKEN_LENGTH) String token) {

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
        executionTimeService.fix(startTime, createRegistrationExecutionTime);

        // Successful result.
        return new SuccessDto(true);

    }

}
