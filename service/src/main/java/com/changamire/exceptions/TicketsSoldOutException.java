package com.changamire.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Tickets Sold Out Exception
 * 
 * This exception is thrown when an event has reached capacity and no more
 * tickets are available for purchase. Returns HTTP 400 Bad Request status.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TicketsSoldOutException extends RuntimeException {
    public TicketsSoldOutException(String message) {
        super(message);
    }
}