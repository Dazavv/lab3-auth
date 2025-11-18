package com.hs.lab3.groupeventservice.exceptions;

public class NoAvailableSlotsException extends RuntimeException {
    public NoAvailableSlotsException(String message) {
        super(message);
    }
}
