package com.changamire.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Record Not Found Exception
 * 
 * This exception is thrown when a requested database record is not found.
 * Returns HTTP 400 Bad Request status.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RecordNotFoundException extends RuntimeException{
    public RecordNotFoundException(String message){
        super(message);
    }
}
