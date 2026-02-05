package com.changamire.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    private final TicketTypeRepository ticketTypeRepository;
    private final com.changamire.user.UserRepository userRepository;

    @Operation(summary = "Purchase tickets", description = "Purchase event tickets with payment processing and QR code generation")
    @PostMapping("/purchase")
    public ResponseEntity<TicketPurchaseResponse> purchaseTicket(
            @Valid @RequestBody TicketPurchaseRequest request
    ) {
        var response = ticketPurchaseService.purchaseTicket(request);
        return ResponseEntity.status(response.success() ? 200 : 400).body(response);
    }

    @Operation(summary = "Get current user's tickets", description = "Returns tickets purchased by the authenticated user")
    @GetMapping("/my")
    public ResponseEntity<List<MyTicketResponse>> getMyTickets(Authentication authentication) {
        String username = authentication.getName();
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        String email = user.getEmail();

        var tickets = ticketTypeRepository.findByCustomerEmailOrderByCreatedDateDesc(email);

        var response = tickets.stream()
                .map(ticket -> {
                    var event = ticket.getEvent();
                    return new MyTicketResponse(
                            ticket.getId(),
                            ticket.getCategory(),
                            ticket.getPrice(),
                            ticket.isRedeemed(),
                            ticket.getQrCodePath(),
                            event != null ? event.getId() : null,
                            event != null ? event.getName() : null,
                            event != null ? event.getDateTime() : null,
                            event != null ? event.getVenue() : null,
                            event != null ? event.getCity() : null,
                            ticket.getCreatedDate()
                    );
                })
                .toList();

        return ResponseEntity.ok(response);
    }
}