package com.guardedbox.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Global Configuration for Error Handling.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Controller
public class ErrorHandlingConfig
        implements ErrorController {

    /** Used to retrieve request error attributes. */
    private final ErrorAttributes errorAttributes;

    /** Current Request. */
    private final HttpServletRequest request;

    /** Current Session. */
    private final HttpSession session;

    /**
     * Constructor with Attributes.
     * 
     * @param errorAttributes Used to retrieve request error attributes.
     * @param request Current Request.
     * @param session Current Session.
     */
    public ErrorHandlingConfig(
            @Autowired ErrorAttributes errorAttributes,
            @Autowired HttpServletRequest request,
            @Autowired HttpSession session) {
        this.errorAttributes = errorAttributes;
        this.request = request;
        this.session = session;
    }

    /**
     * Error handler.
     * 
     * @return Unauthorized (401), Forbidden (403) or Not Found (404) with no body.
     * 
     */
    @RequestMapping(value = "/error", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS, RequestMethod.PATCH, RequestMethod.TRACE})
    public ResponseEntity<?> error() {

        Map<String, Object> requestErrorAttributes = errorAttributes.getErrorAttributes(new ServletRequestAttributes(request), true);
        HttpStatus errorStatus = HttpStatus.valueOf((Integer) requestErrorAttributes.get("status"));

        if (HttpStatus.UNAUTHORIZED.equals(errorStatus) || HttpStatus.FORBIDDEN.equals(errorStatus)) {
            session.invalidate();
            return new ResponseEntity<>(errorStatus);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    /**
     * @return The path to the error handler.
     */
    @Override
    public String getErrorPath() {
        return "/error";
    }

}
