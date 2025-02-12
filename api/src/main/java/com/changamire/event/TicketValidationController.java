package com.changamire.event;

import com.changamire.ticket.TicketType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketTypeRepository ticketTypeRepository;
    private final ObjectMapper objectMapper; // Spring Boot auto-configures this


    @Operation(summary = "Validate ticket", description = "Check if a ticket is valid and not redeemed")
  //  @ApiResponse(responseCode = "200", description = "Ticket validation result")
    @PostMapping("/validate")
    public ResponseEntity<?> validateTicket(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "QR code data as JSON string",
            content = @Content(schema = @Schema(example = "{\"ticketId\":154,\"eventId\":1,\"type\":\"STANDARD\"}"))
    ) @RequestBody String qrData) {
        try {
            JsonNode json = objectMapper.readTree(qrData);
            Long ticketId = json.get("ticketId").asLong();

            TicketType ticket = ticketTypeRepository.findById(ticketId)
                    .orElseThrow(() -> new RuntimeException("Ticket not found"));

            return ResponseEntity.ok(Map.of(
                    "valid", !ticket.isRedeemed(),
                    "ticketId", ticket.getId(),
                    "eventId", ticket.getEvent().getId(),
                    "type", ticket.getCategory(),
                    "redeemed", ticket.isRedeemed()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid QR Code"));
        }
    }

    @PostMapping("/redeem")
    @Operation(summary = "redeem ticket", description = "Check if a ticket is used or claimed")
    public ResponseEntity<?> redeemTicket(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "QR code data as JSON string",
            content = @Content(schema = @Schema(example = "{\"ticketId\":154,\"eventId\":1,\"type\":\"STANDARD\"}"))
    ) @RequestBody String qrData) {
        try {
            JSONObject json = new JSONObject(qrData);
            Long ticketId = json.getLong("ticketId");

            TicketType ticket = ticketTypeRepository.findById(ticketId)
                    .orElseThrow(() -> new RuntimeException("Ticket not found"));

            if(ticket.isRedeemed()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Ticket already redeemed"));
            }

            ticket.setRedeemed(true);
            ticketTypeRepository.save(ticket);

            return ResponseEntity.ok(Map.of(
                    "message", "Ticket redeemed successfully",
                    "ticketId", ticket.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid QR Code: " + e.getMessage()));
        }
    }


//    @PostMapping("/redeem")
//    public ResponseEntity<?> redeemTicket(@RequestBody String qrData) {
//        try {
//            JSONObject json = new JSONObject(qrData);
//            Long ticketId = json.getLong("ticketId");
//
//            TicketType ticket = ticketTypeRepository.findById(ticketId)
//                .orElseThrow(() -> new RuntimeException("Ticket not found"));
//
//            if(ticket.isRedeemed()) {
//                return ResponseEntity.badRequest()
//                    .body(Map.of("error", "Ticket already redeemed"));
//            }
//
//            ticket.setRedeemed(true);
//            ticketTypeRepository.save(ticket);
//
//            return ResponseEntity.ok(Map.of(
//                "message", "Ticket redeemed successfully",
//                "ticketId", ticket.getId()
//            ));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(Map.of("error", "Invalid QR Code"));
//        }
//    }


}