package com.changamire.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Email Sending Exception
 * <p>
 * This exception is thrown when email notification delivery fails,
 * such as ticket confirmation emails or receipts.
 * Returns HTTP 400 Bad Request status to the client.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String message) {
        super(message);
    }
}
