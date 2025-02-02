package com.changamire.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class ExceptionHandlerController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(RecordNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Error recordNotFound(RecordNotFoundException e) {
        LOGGER.info("Record Not Found error: {}", e.getMessage());
        return Error.of(400, e.getMessage());
    }
}