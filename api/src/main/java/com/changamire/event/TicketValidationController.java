package com.changamire.event;

import com.changamire.ticket.TicketType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Ticket Validation Controller
 * <p>
 * This controller handles ticket validation and redemption operations.
 * It verifies QR codes, checks ticket validity, and manages ticket redemption
 * to prevent duplicate entries.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Slf4j
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket Validation", description = "Ticket validation and redemption operations")
public class TicketValidationController {

    private final TicketTypeRepository ticketTypeRepository;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Validate ticket", description = "Check if a ticket is valid and not redeemed")
    @PostMapping("/validate")
    public ResponseEntity<?> validateTicket(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "QR code data as JSON string",
            content = @Content(schema = @Schema(example = "{\"ticketId\":154,\"eventId\":1,\"type\":\"STANDARD\"}")))
            @RequestBody String qrData) {
        log.info("Request to validate ticket with QR data: {}", qrData);
        try {
            JsonNode json = objectMapper.readTree(qrData);
            Long ticketId = json.get("ticketId").asLong();
            TicketType ticket = ticketTypeRepository.findById(String.valueOf(ticketId))
                    .orElseThrow(() -> new RuntimeException("Ticket not found"));
            return ResponseEntity.ok(Map.of("valid", !ticket.isRedeemed(),
                                            "ticketId", ticket.getId(),
                                            "eventId", ticket.getEvent().getId(),
                                            "type", ticket.getCategory(),
                                            "redeemed", ticket.isRedeemed()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid QR Code"));
        }
    }

    @Operation(summary = "Redeem ticket", description = "Check if a ticket is used or claimed")
    @PostMapping("/redeem")
    public ResponseEntity<?> redeemTicket(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "QR code data as JSON string",
            content = @Content(schema = @Schema(example = "{\"ticketId\":154,\"eventId\":1,\"type\":\"STANDARD\"}")))
            @RequestBody String qrData) {
        log.info("Request to redeem ticket with QR data: {}", qrData);
        try {
            JSONObject json = new JSONObject(qrData);
            Long ticketId = json.getLong("ticketId");
            TicketType ticket = ticketTypeRepository.findById(String.valueOf(ticketId))
                    .orElseThrow(() -> new RuntimeException("Ticket not found"));
            if (ticket.isRedeemed()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Ticket already redeemed"));
            }
            ticket.setRedeemed(true);
            ticketTypeRepository.save(ticket);
            return ResponseEntity.ok(Map.of("message", "Ticket redeemed successfully",
                                            "ticketId", ticket.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid QR Code: " + e.getMessage()));
        }
    }
}