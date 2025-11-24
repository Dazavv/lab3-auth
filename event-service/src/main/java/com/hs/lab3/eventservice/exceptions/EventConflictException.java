package com.hs.lab3.eventservice.exceptions;

public class EventConflictException extends RuntimeException {
    public EventConflictException(String message) {
        super(message);
    }
}
