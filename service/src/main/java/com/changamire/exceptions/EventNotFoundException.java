package com.changamire.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Event Not Found Exception
 * 
 * This exception is thrown when a requested event does not exist in the system.
 * Returns HTTP 400 Bad Request status to the client.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EventNotFoundException extends RuntimeException{
    public EventNotFoundException(String message){
        super(message);
    }
}
