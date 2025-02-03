package com.changamire.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketPurchaseService ticketPurchaseService;

    @PostMapping("/purchase")
    public ResponseEntity<TicketPurchaseResponse> purchaseTicket(
            @Valid @RequestBody TicketPurchaseRequest request
    ) {
        TicketPurchaseResponse response = ticketPurchaseService.purchaseTicket(request);
        return ResponseEntity.status(response.isSuccess() ? 200 : 400).body(response);
    }
}