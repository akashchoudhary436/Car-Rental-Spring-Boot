package com.example.car.exception;


// for invalid user name and password
public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException(String message) {
        super(message);
    }
}
