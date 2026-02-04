package com.changamire.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Payment Processing Exception
 * 
 * This exception is thrown when payment processing fails through the
 * external payment gateway. Returns HTTP 400 Bad Request status.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PaymentProcessingException extends RuntimeException{
    public PaymentProcessingException(String message){
        super(message);
    }
}

