package com.guardedbox.constants;

import lombok.experimental.UtilityClass;

/**
 * Path Parameters.
 *
 * @author s3curitybug@gmail.com
 *
 */
@UtilityClass
public class PathParameters {

    /** API Base Path. */
    public static final String API_BASE_PATH = "/api/";

    /** Path of the front-end registration component, including the registration token parameter. */
    public static final String FRONT_REGISTRATION_COMPONENT_PATH = "/#/registration?token=%s";

}
