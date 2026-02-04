package com.changamire.payment;

import com.changamire.enums.Status;
import org.springframework.stereotype.Component;

/**
 * Status Converter
 * 
 * This Spring converter component handles conversion of string values
 * to Status enum constants for request parameter binding.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Component
public class StatusConverter extends StringToEnumConverter<Status> {
    public StatusConverter() {
        super(Status.class);
    }
}
