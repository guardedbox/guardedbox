package com.guardedbox.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guardedbox.dto.ServiceExceptionDto;
import com.guardedbox.exception.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Global Controller Configuration.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ControllerAdviceConfig {

    /** Current Request. */
    private final HttpServletRequest request;

    /** Current Session. */
    private final HttpSession session;

    /**
     * Exception handler for ServiceException.
     *
     * @param e The ServiceException.
     * @return Bad Request (400) with ServiceExceptionDto body.
     */
    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<ServiceExceptionDto> serviceExceptionHandler(
            ServiceException e) {

        log.error(String.format(
                "Error during the request %s %s",
                request.getMethod(),
                request.getRequestURI()),
                e);

        if (e.getResponseAsSuccess() == null) {

            return new ResponseEntity<>(new ServiceExceptionDto()
                    .setErrorCode(e.getErrorCode())
                    .setAdditionalData(e.getAdditionalData()),
                    HttpStatus.BAD_REQUEST);

        } else {

            return new ResponseEntity<>(new ServiceExceptionDto()
                    .setSuccess(e.getResponseAsSuccess()),
                    HttpStatus.OK);

        }

    }

    /**
     * Exception handler for AuthenticationServiceException.
     *
     * @param e The AuthenticationServiceException.
     * @return Unauthorized (401) with no body.
     */
    @ExceptionHandler
    public ResponseEntity<?> exceptionHandler(
            AuthenticationServiceException e) {

        log.error(String.format(
                "Error during the request %s %s",
                request.getMethod(),
                request.getRequestURI()),
                e);

        session.invalidate();

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    }

    /**
     * Exception handler for AuthorizationServiceException.
     *
     * @param e The AuthorizationServiceException.
     * @return Forbidden (403) with no body.
     */
    @ExceptionHandler
    public ResponseEntity<?> exceptionHandler(
            AuthorizationServiceException e) {

        log.error(String.format(
                "Error during the request %s %s",
                request.getMethod(),
                request.getRequestURI()),
                e);

        session.invalidate();

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);

    }

    /**
     * Exception handler for generic Exception.
     *
     * @param e The Exception.
     * @return Not Found (404) with no body.
     */
    @ExceptionHandler
    public ResponseEntity<?> exceptionHandler(
            Exception e) {

        log.error(String.format(
                "Error during the request %s %s",
                request.getMethod(),
                request.getRequestURI()),
                e);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

}
