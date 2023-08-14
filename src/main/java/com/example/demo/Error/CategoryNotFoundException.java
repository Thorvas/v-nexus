package com.example.demo.Error;

public class CategoryNotFoundException extends RuntimeException{

    public CategoryNotFoundException(String message) {

        super(message);
    }
}
