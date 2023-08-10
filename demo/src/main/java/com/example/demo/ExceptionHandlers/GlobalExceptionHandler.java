package com.example.demo.ExceptionHandlers;

import com.example.demo.CustomExceptions.ApiError;
import com.example.demo.CustomExceptions.ArgumentNotValidError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(Exception e, HttpServletRequest request) {

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupportedException(Exception e, HttpServletRequest request) {

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(Exception e, HttpServletRequest request) {

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(Exception e,
                                                         HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(Exception e,
                                                          HttpServletRequest request) {

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(Exception e,
                                                                  HttpServletRequest request) {

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ArgumentNotValidError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                          HttpServletRequest request) {

        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(),error.getDefaultMessage());
        });

        ArgumentNotValidError apiError = new ArgumentNotValidError(
                request.getRequestURI(),
                errorMap,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );


        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
