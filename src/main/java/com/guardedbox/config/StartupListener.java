package com.guardedbox.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Startup Event Listener.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Component
@RequiredArgsConstructor
public class StartupListener
        implements ServletContextInitializer {

    /**
     * Startup Event.
     *
     * @param servletContext ServletContext.
     */
    @Override
    public void onStartup(
            ServletContext servletContext)
            throws ServletException {

    }

}
