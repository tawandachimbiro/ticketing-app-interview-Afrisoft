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


    @ExceptionHandler(PaymentProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Error paymentProcessing(PaymentProcessingException e) {
        LOGGER.info("Payment processing error: {}", e.getMessage());
        return Error.of(400, e.getMessage());
    }


    @ExceptionHandler(TicketsSoldOutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Error ticketsSoldOut(TicketsSoldOutException e) {
        LOGGER.info("Tickets are sold out error: {}", e.getMessage());
        return Error.of(400, e.getMessage());
    }

    //EmailSendingException

    @ExceptionHandler(EmailSendingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Error emailSending(EmailSendingException e) {
        LOGGER.info("Email sending error: {}", e.getMessage());
        return Error.of(400, e.getMessage());
    }

    //EventNotFoundException

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Error EventNotFoundException(EventNotFoundException e) {
        LOGGER.info("Event Not Found Exception", e.getMessage());
        return Error.of(400, e.getMessage());
    }



    @ExceptionHandler(ExternalServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Error ExternalServiceUnavailableException(ExternalServiceUnavailableException e) {
        LOGGER.info("External Service Unavailable Error", e.getMessage());
        return Error.of(400, e.getMessage());
    }
}