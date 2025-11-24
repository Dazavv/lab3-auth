package com.hs.lab3.userservice.exceptions;

public class JWTNotValidException extends RuntimeException {

    public JWTNotValidException(String msg) {
        super(msg);
    }
}