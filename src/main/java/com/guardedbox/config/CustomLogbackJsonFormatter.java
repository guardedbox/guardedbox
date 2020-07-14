package com.guardedbox.config;

import java.io.IOException;
import java.util.Map;

import ch.qos.logback.contrib.jackson.JacksonJsonFormatter;

/**
 * Custom Logback JSON Formatter.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class CustomLogbackJsonFormatter
        extends JacksonJsonFormatter {

    /**
     * Converts the map of attributes of the log entries to JSON.
     *
     * @param map The map of attributes of the log entries.
     * @return The JSON string representing the introduced map.
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String toJsonString(
            Map map)
            throws IOException {

        map.put("severity", map.get("level"));
        map.remove("level");

        return super.toJsonString(map);

    }

}
