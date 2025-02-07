package com.changamire.event;

import com.changamire.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;




@Data
@AllArgsConstructor
public class TicketPurchaseResponse {
    private boolean success;
    private String message;
    private String transactionId;
    private String ticketDetails;
    private String code;
    private PaymentMethod paymentMethod;
    private String hostedUrl;
    private String checkoutId;
}

