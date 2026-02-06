package com.changamire.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status Enum
 * <p>
 * This enum represents payment transaction statuses including SUCCESS,
 * FAILED, and PENDING. Includes JSON serialization support for API responses.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public enum Status {
    SUCCESS("success"),
    FAILED("failed"),
    PENDING("pending");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static Status fromValue(String value) {
        for (Status status : Status.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}