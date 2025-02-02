package com.changamire.payment;

import com.changamire.enums.Status;
import org.springframework.stereotype.Component;

@Component
public class StatusConverter extends StringToEnumConverter<Status> {
    public StatusConverter() {
        super(Status.class);
    }
}
