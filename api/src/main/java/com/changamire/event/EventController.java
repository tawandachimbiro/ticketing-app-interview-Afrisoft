package com.changamire.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

/**
 * Event Management Controller
 * <p>
 * This controller handles all event-related operations including creation, retrieval,
 * updates, and filtering of events in the ticketing system.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Event management operations")
public class EventController {

    @Autowired
    private EventService eventService;

    @Operation(summary = "Get all events", description = "Retrieve a paginated list of all events")
    @GetMapping
    public ResponseEntity<Page<Event>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(eventService.getAllEvents(page, size));
    }

    @Operation(summary = "Get event by ID", description = "Retrieve a specific event by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return event != null ? ResponseEntity.ok(event) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Filter events", description = "Search and filter events by various criteria including name, city, type, date range, and price range")
    @GetMapping("/filter")
    public ResponseEntity<Page<Event>> getEventsByFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String ispromotion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Event> events = eventService.getEventsByFilters(
                name, city, type, ispromotion,
                startDate, endDate,
                minPrice, maxPrice,
                page, size);

        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Create new event", description = "Create a new event with ticket types and pricing information")
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest request) {
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update event", description = "Update an existing event's details including ticket types and pricing")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventUpdateRequest request) {
        EventResponse response = eventService.updateEvent(id, request);
        return ResponseEntity.ok(response);
    }
}