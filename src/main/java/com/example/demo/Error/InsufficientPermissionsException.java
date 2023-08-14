package com.example.demo.Error;

public class InsufficientPermissionsException extends RuntimeException {

    public InsufficientPermissionsException(String message) {

        super(message);
    }
}
