package com.example.demo.Error;

public class OpinionNotFoundException extends RuntimeException{

    public OpinionNotFoundException(String message) {

        super(message);
    }
}
