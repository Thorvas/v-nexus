package com.example.demo.Error;

public class VolunteerNotFoundException extends RuntimeException{

    public VolunteerNotFoundException(String message) {

        super(message);
    }
}
