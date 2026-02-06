package com.changamire.event;

import com.changamire.exceptions.EventNotFoundException;
import com.changamire.ticket.TicketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Event Service
 * 
 * This service handles business logic for event management including creating,
 * updating, retrieving, and filtering events. It provides comprehensive event
 * search capabilities with multiple filter criteria.
 * 
 * Caching Strategy:
 * - Read operations are cached with appropriate TTLs to reduce database load
 * - Write operations (create/update/delete) evict all event-related caches to ensure data consistency
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    /**
     * Get all events with pagination
     * Cached to improve performance for frequently accessed event listings
     * Cache key includes page and size to cache each page separately
     * 
     * @param page page number (0-indexed)
     * @param size page size
     * @return paginated list of events
     */
    @Cacheable(value = "events-all", key = "#page + '-' + #size")
    public Page<Event> getAllEvents(int page, int size) {
        log.info("Request to get all events with page {} and size {}", page, size);
        var pageable = PageRequest.of(page, size);
        var eventsPage = eventRepository.findByDeletedFalse(pageable);

        // Filter out purchased tickets from all events
        eventsPage.forEach(event -> {
            var ticketTemplates = event.getTicketTypes().stream()
                    .filter(tt -> tt.getQrCodePath() == null)
                    .toList();
            event.setTicketTypes(ticketTemplates);
        });
        
        return eventsPage;
    }

    /**
     * Get event by ID
     * Cached individually as event details are frequently accessed
     * Cache key is the event ID
     * 
     * @param id event ID
     * @return event details
     * @throws EventNotFoundException if event not found
     */
    @Cacheable(value = "event-by-id", key = "#id")
    public Event getEventById(Long id) {
        log.info("Request to get event by id {}", id);
        var event = eventRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
        
        // Filter out purchased tickets
        var ticketTemplates = event.getTicketTypes().stream()
                .filter(tt -> tt.getQrCodePath() == null)
                .toList();
        
        event.setTicketTypes(ticketTemplates);
        return event;
    }

    /**
     * Get events by filters with pagination
     * Cached with composite key based on all filter parameters
     * Allows fast retrieval of frequently used filter combinations
     * 
     * @param filterRequest event filter request containing all filter criteria
     * @return filtered and paginated list of events
     */
    @Cacheable(value = "events-filtered", 
               key = "#filterRequest.name + '-' + #filterRequest.city + '-' + #filterRequest.type + '-' + #filterRequest.ispromotion + '-' + #filterRequest.startDate + '-' + #filterRequest.endDate + '-' + #filterRequest.minPrice + '-' + #filterRequest.maxPrice + '-' + #filterRequest.page + '-' + #filterRequest.size")
    public Page<Event> getEventsByFilters(EventFilterRequest filterRequest) {
        log.info("Request to get events by filters: {}", filterRequest);
        var pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize());
        
        var spec = Specification.where(EventSpecifications.notDeleted())
            .and(EventSpecifications.hasName(filterRequest.getName()))
            .and(EventSpecifications.hasCity(filterRequest.getCity()))
            .and(EventSpecifications.hasType(filterRequest.getType()))
            .and(EventSpecifications.hasPromotion(filterRequest.getIspromotion()))
            .and(EventSpecifications.betweenDates(filterRequest.getStartDate(), filterRequest.getEndDate()))
            .and(EventSpecifications.hasTicketPriceBetween(filterRequest.getMinPrice(), filterRequest.getMaxPrice()));

        var eventsPage = eventRepository.findAll(spec, pageable);
        
        // Filter out purchased tickets from all events
        eventsPage.forEach(event -> {
            var ticketTemplates = event.getTicketTypes().stream()
                    .filter(tt -> tt.getQrCodePath() == null)
                    .toList();
            event.setTicketTypes(ticketTemplates);
        });
        
        return eventsPage;
    }

    /**
     * Create new event
     * Evicts all event-related caches after creating to ensure fresh data is fetched
     * 
     * @param request event creation request
     * @return event response with created event details
     */
    @Caching(evict = {
        @CacheEvict(value = "events-all", allEntries = true),
        @CacheEvict(value = "events-filtered", allEntries = true)
    })
    public EventResponse createEvent(EventCreateRequest request) {
        log.info("Request to create event: {}", request);
        var event = Event.builder()
                .name(request.name())
                .dateTime(request.dateTime())
                .venue(request.venue())
                .address(request.address())
                .city(request.city())
                .type(request.type())
                .ispromotion(request.ispromotion())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .capacity(request.capacity())
                .description(request.description())
                .banner_url(request.banner_url())
                .image_url(request.image_url())
                .ticketTypes(new ArrayList<>())
                .build();

        if (request.ticketTypes() != null && !request.ticketTypes().isEmpty()) {
            for (TicketType ticketType : request.ticketTypes()) {
                ticketType.setEvent(event);
                event.getTicketTypes().add(ticketType);
            }
        }

        var savedEvent = eventRepository.save(event);
        
        // Filter out purchased tickets
        var ticketTemplates = savedEvent.getTicketTypes().stream()
                .filter(tt -> tt.getQrCodePath() == null)
                .toList();

        return EventResponse.builder()
                .id(savedEvent.getId())
                .name(savedEvent.getName())
                .dateTime(savedEvent.getDateTime())
                .venue(savedEvent.getVenue())
                .address(savedEvent.getAddress())
                .city(savedEvent.getCity())
                .type(savedEvent.getType())
                .ispromotion(savedEvent.getIspromotion())
                .latitude(savedEvent.getLatitude())
                .longitude(savedEvent.getLongitude())
                .capacity(savedEvent.getCapacity())
                .description(savedEvent.getDescription())
                .banner_url(savedEvent.getBanner_url())
                .image_url(savedEvent.getImage_url())
                .ticketTypes(ticketTemplates)
                .message("Event created successfully")
                .build();
    }

    /**
     * Update existing event
     * Evicts all event-related caches including the specific event's cache
     * 
     * @param id event ID to update
     * @param request event update request
     * @return event response with updated event details
     * @throws EventNotFoundException if event not found
     */
    @Caching(evict = {
        @CacheEvict(value = "events-all", allEntries = true),
        @CacheEvict(value = "events-filtered", allEntries = true),
        @CacheEvict(value = "event-by-id", key = "#id")
    })
    public EventResponse updateEvent(Long id, EventUpdateRequest request) {
        log.info("Request to update event with id {}: {}", id, request);
        Event event = eventRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        // Update only provided fields (partial update)
        applyIfNotNull(request.name(), event::setName);
        applyIfNotNull(request.dateTime(), event::setDateTime);
        applyIfNotNull(request.venue(), event::setVenue);
        applyIfNotNull(request.address(), event::setAddress);
        applyIfNotNull(request.city(), event::setCity);
        applyIfNotNull(request.type(), event::setType);
        applyIfNotNull(request.ispromotion(), event::setIspromotion);
        applyIfNotNull(request.latitude(), event::setLatitude);
        applyIfNotNull(request.longitude(), event::setLongitude);
        applyIfNotNull(request.capacity(), event::setCapacity);
        applyIfNotNull(request.description(), event::setDescription);
        applyIfNotNull(request.banner_url(), event::setBanner_url);
        applyIfNotNull(request.image_url(), event::setImage_url);
        if (request.ticketTypes() != null) {
            event.getTicketTypes().removeIf(tt -> tt.getQrCodePath() == null);

            // Add new ticket type
            for (TicketType ticketType : request.ticketTypes()) {
                ticketType.setEvent(event);
                event.getTicketTypes().add(ticketType);
            }
        }

        var updatedEvent = eventRepository.save(event);
        
        // Filter out purchased tickets
        var ticketTemplates = updatedEvent.getTicketTypes().stream()
                .filter(tt -> tt.getQrCodePath() == null)
                .toList();

        return EventResponse.builder()
                .id(updatedEvent.getId())
                .name(updatedEvent.getName())
                .dateTime(updatedEvent.getDateTime())
                .venue(updatedEvent.getVenue())
                .address(updatedEvent.getAddress())
                .city(updatedEvent.getCity())
                .type(updatedEvent.getType())
                .ispromotion(updatedEvent.getIspromotion())
                .latitude(updatedEvent.getLatitude())
                .longitude(updatedEvent.getLongitude())
                .capacity(updatedEvent.getCapacity())
                .description(updatedEvent.getDescription())
                .banner_url(updatedEvent.getBanner_url())
                .image_url(updatedEvent.getImage_url())
                .ticketTypes(ticketTemplates)
                .message("Event updated successfully")
                .build();
    }

    /**
     * Utility helper to reduce repetitive null checks when applying updates.
     * Applies the given consumer only when the value is non-null.
     */
    private <T> void applyIfNotNull(T value, java.util.function.Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    /**
     * Soft delete an event by marking it as deleted instead of removing it from the database.
     * Evicts all event-related caches to ensure deleted event no longer appears
     * 
     * @param id event ID to delete
     * @throws EventNotFoundException if event not found
     */
    @Caching(evict = {
            @CacheEvict(value = "events-all", allEntries = true),
            @CacheEvict(value = "events-filtered", allEntries = true),
            @CacheEvict(value = "event-by-id", key = "#id")
    })
    public void softDeleteEvent(Long id) {
        log.info("Request to soft delete event with id {}", id);

        var event = eventRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        event.setDeleted(true);
        event.setDeletedAt(LocalDateTime.now());

        eventRepository.save(event);
        log.info("Event with id {} marked as deleted", id);
    }
}