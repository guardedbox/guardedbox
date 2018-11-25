package com.guardedbox.service;

import org.springframework.stereotype.Service;

/**
 * Execution Time Service.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class ExecutionTimeService {

    /**
     * Fixes the execution time by making the current thread to sleep the difference between the elapsed time since the
     * start time and the required execution time.
     * 
     * @param startTime The start time.
     * @param requiredExecutionTime The required execution time.
     */
    public void fix(
            long startTime,
            long requiredExecutionTime) {

        long elapsedTime = System.currentTimeMillis() - startTime;

        if (elapsedTime < requiredExecutionTime) {
            try {
                Thread.sleep(requiredExecutionTime - elapsedTime);
            } catch (InterruptedException e) {
            }
        }

    }

}
