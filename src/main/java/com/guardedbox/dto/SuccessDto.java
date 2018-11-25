package com.guardedbox.dto;

/**
 * DTO: Response to successful or unsuccessful requests.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class SuccessDto {

    /** Success. */
    private boolean success;

    /**
     * @param success The success.
     */
    public SuccessDto(
            boolean success) {
        this.success = success;
    }

    /**
     * @return The success.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success The success to set.
     */
    public void setSuccess(
            boolean success) {
        this.success = success;
    }

}
