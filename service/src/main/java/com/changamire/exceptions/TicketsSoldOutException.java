package com.changamire.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TicketsSoldOutException extends RuntimeException {
    public TicketsSoldOutException(String message) {
        super(message);
    }
}