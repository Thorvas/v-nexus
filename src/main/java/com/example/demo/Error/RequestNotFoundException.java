package com.example.demo.Error;

public class RequestNotFoundException extends RuntimeException{

    public RequestNotFoundException(String message) {

        super(message);
    }
}
