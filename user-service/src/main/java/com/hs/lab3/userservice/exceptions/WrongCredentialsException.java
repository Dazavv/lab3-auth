package com.hs.lab3.userservice.exceptions;

public class WrongCredentialsException extends RuntimeException {

    public WrongCredentialsException(String msg) {
        super(msg);
    }
}