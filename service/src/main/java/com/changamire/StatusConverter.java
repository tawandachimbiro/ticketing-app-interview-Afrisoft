package com.changamire;

import org.springframework.stereotype.Component;

@Component
public class StatusConverter extends StringToEnumConverter<Status> {
    public StatusConverter() {
        super(Status.class);
    }
}
