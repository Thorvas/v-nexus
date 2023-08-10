package com.example.demo.CustomExceptions;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Record representing exceptions occuring during validation
 *
 * @param path       Request path where exception occured
 * @param errors     Map of validation errors
 * @param statusCode Status code of response for exception
 * @param date       Date when exception occured
 */
public record ArgumentNotValidError(
        String path,
        Map<String, String> errors,
        int statusCode,
        LocalDateTime date
) {
}
