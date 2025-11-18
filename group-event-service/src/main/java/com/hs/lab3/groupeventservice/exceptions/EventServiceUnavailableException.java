package com.hs.lab3.groupeventservice.exceptions;

public class EventServiceUnavailableException extends RuntimeException{
    public EventServiceUnavailableException(String s) {
        super(s);
    }
}
