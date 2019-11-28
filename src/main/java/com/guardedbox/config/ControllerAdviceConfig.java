package com.guardedbox.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guardedbox.dto.ServiceExceptionDto;
import com.guardedbox.exception.ServiceException;

/**
 * Global Controller Configuration.
 *
 * @author s3curitybug@gmail.com
 *
 */
@ControllerAdvice
public class ControllerAdviceConfig {

    /** Logger. */
    private final Logger logger;

    /** Current Request. */
    private final HttpServletRequest request;

    /** Current Session. */
    private final HttpSession session;

    /**
     * Constructor with Attributes.
     *
     * @param request Current Request.
     * @param session Current Session.
     */
    public ControllerAdviceConfig(
            @Autowired HttpServletRequest request,
            @Autowired HttpSession session) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.request = request;
        this.session = session;
    }

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

        logger.error(String.format(
                "Error during the request %s %s",
                request.getMethod(),
                request.getRequestURI()),
                e);

        ServiceExceptionDto serviceExceptionDto = new ServiceExceptionDto();
        serviceExceptionDto.setErrorCode(e.getErrorCode());
        serviceExceptionDto.setAdditionalData(e.getAdditionalData());

        return new ResponseEntity<>(serviceExceptionDto, HttpStatus.BAD_REQUEST);

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

        logger.error(String.format(
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

        logger.error(String.format(
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

        logger.error(String.format(
                "Error during the request %s %s",
                request.getMethod(),
                request.getRequestURI()),
                e);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

}
