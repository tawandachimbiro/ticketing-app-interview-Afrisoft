package com.changamire.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketPurchaseResponse {
    private boolean success;
    private String message;
    private String transactionId;
    private String ticketDetails;
}