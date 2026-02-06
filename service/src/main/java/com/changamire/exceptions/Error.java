package com.changamire.exceptions;

import lombok.Data;

/**
 * Error Response Model
 * <p>
 * This class represents a standardized error response containing HTTP status code
 * and error message for API error handling.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
public class Error {

    private final int status;
    private final String error;

    private Error(int status, String error) {
        this.status = status;
        this.error = error;
    }

    public static Error of(int status, String error) {
        return new Error(status, error);
    }

    @Override
    public String toString() {
        return "Error{" +
                "status=" + status +
                ", error='" + error + '\'' +
                '}';
    }
}
