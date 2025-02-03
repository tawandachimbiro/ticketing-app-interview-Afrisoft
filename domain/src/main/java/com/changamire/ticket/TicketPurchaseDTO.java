package com.changamire.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketPurchaseDTO {
    private String customerEmail;
    private String ticketId; // From BusSchedule
    private int quantity;
    private String paymentMethod;
}