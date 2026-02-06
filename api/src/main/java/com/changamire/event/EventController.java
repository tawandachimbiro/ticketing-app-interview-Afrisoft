package com.changamire.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@Slf4j
@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Event management operations")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Get all events", description = "Retrieve a paginated list of all events")
    @GetMapping
    public ResponseEntity<Page<Event>> getAllEvents(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        log.info("Request to get all events with page {} and size {}", page, size);
        return ResponseEntity.ok(eventService.getAllEvents(page, size));
    }

    @Operation(summary = "Get event by ID", description = "Retrieve a specific event by its unique identifier")
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        log.info("Request to get event with id {}", id);
        Event event = eventService.getEventById(id);
        return event != null ? ResponseEntity.ok(event) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Filter events", description = "Search and filter events by various criteria including name, city, type, date range, and price range")
    @GetMapping("/filter")
    public ResponseEntity<Page<Event>> getEventsByFilters(@ModelAttribute EventFilterRequest filterRequest) {
        log.info("Request to filter events: {}", filterRequest.toString());
        return ResponseEntity.ok(eventService.getEventsByFilters(filterRequest));
    }

    @Operation(summary = "Create new event", description = "Create a new event with ticket types and pricing information")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest request) {
        log.info("Request to create event: {}", request.toString());
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update event", description = "Update an existing event's details including ticket types and pricing")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id,
                                                      @Valid @RequestBody EventUpdateRequest request) {
        log.info("Request to update event with id {}: {}", id, request.toString());
        EventResponse response = eventService.updateEvent(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete event(Soft delete)", description = "Soft delete an event (admin only). Event is hidden but kept for audit.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        log.info("Request to delete event with id {}", id);
        eventService.softDeleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}