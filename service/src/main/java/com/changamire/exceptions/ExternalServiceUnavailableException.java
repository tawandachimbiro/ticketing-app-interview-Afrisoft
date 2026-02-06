package com.changamire.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * External Service Unavailable Exception
 * 
 * This exception is thrown when external payment gateway or other third-party
 * services are unavailable or unreachable. Returns HTTP 400 Bad Request status.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExternalServiceUnavailableException extends RuntimeException{
    public ExternalServiceUnavailableException(String message){
        super(message);
    }
}



