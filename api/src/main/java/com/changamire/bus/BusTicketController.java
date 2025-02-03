package com.changamire.bus;

import com.changamire.ticket.BusTicketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bus-tickets")
public class BusTicketController {

    @Autowired
    private BusTicketService busTicketService;

    @GetMapping
    public ResponseEntity<List<BusTicketDTO>> getAllBusTickets() {
        List<BusTicketDTO> tickets = busTicketService.getAllBusTickets();
        return ResponseEntity.ok(tickets);
    }

    @PostMapping
    public ResponseEntity<BusTicketDTO> createBusTicket(@RequestBody BusTicketDTO dto) {
        BusTicketDTO createdTicket = busTicketService.createBusTicket(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }
}