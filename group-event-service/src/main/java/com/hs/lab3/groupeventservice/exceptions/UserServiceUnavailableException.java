package com.hs.lab3.groupeventservice.exceptions;

public class UserServiceUnavailableException extends RuntimeException{
    public UserServiceUnavailableException(String s) {
        super(s);
    }
}
