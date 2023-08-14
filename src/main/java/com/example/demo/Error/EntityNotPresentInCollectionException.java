package com.example.demo.Error;

public class EntityNotPresentInCollectionException extends RuntimeException{

    public EntityNotPresentInCollectionException(String message) {

        super(message);
    }
}
