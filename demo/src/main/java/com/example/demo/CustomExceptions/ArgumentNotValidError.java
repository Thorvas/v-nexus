package com.example.demo.CustomExceptions;

import java.time.LocalDateTime;
import java.util.Map;

public record ArgumentNotValidError(
        String path,
        Map<String, String> errors,
        int statusCode,
        LocalDateTime date
) {
}
