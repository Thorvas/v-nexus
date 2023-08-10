package com.example.demo.CustomExceptions;

import java.time.LocalDateTime;

/**
 * Record representing thrown exceptions handled within exception handler
 *
 * @param path       Request path where exception occured
 * @param message    Message of thrown exception
 * @param statusCode Status code of response for exception
 * @param date       Date when exception occured
 */
public record ApiError(
        String path,
        String message,
        int statusCode,
        LocalDateTime date
) {
}
