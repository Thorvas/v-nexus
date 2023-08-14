package com.example.demo.Error;

public class ProjectNotFoundException extends RuntimeException{

    public ProjectNotFoundException(String message) {

        super(message);
    }

}
