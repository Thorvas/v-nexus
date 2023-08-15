package com.example.demo.Error;

public class WrongStatusException extends RuntimeException{

    public WrongStatusException(String message) {

        super(message);
    }
}
