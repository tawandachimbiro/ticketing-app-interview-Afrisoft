package com.changamire.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ticket Purchase Controller
 * <p>
 * This controller handles ticket purchase operations including processing
 * ticket orders, payment, and generating QR codes for purchased tickets.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Ticket purchase and management operations")
public class TicketController {
    private final TicketPurchaseService ticketPurchaseService;

    @Operation(summary = "Purchase tickets", description = "Purchase event tickets with payment processing and QR code generation")
    @PostMapping("/purchase")
    public ResponseEntity<TicketPurchaseResponse> purchaseTicket(
            @Valid @RequestBody TicketPurchaseRequest request
    ) {
        var response = ticketPurchaseService.purchaseTicket(request);
        return ResponseEntity.status(response.success() ? 200 : 400).body(response);
    }
}