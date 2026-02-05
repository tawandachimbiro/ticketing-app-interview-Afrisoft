package com.changamire.event;

import com.changamire.exceptions.EventNotFoundException;
import com.changamire.ticket.TicketType;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Page<Event> getAllEvents(int page, int size) {
        var pageable = PageRequest.of(page, size);
        var eventsPage = eventRepository.findAll(pageable);
        
        // Filter out purchased tickets from all events
        eventsPage.forEach(event -> {
            var ticketTemplates = event.getTicketTypes().stream()
                    .filter(tt -> tt.getQrCodePath() == null)  // Only templates (no QR codes)
                    .toList();
            event.setTicketTypes(ticketTemplates);
        });
        
        return eventsPage;
    }

    public Event getEventById(Long id) {
        var event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
        
        // Filter out purchased tickets (those with QR codes) - only return ticket templates
        var ticketTemplates = event.getTicketTypes().stream()
                .filter(tt -> tt.getQrCodePath() == null)  // Templates don't have QR codes
                .toList();
        
        event.setTicketTypes(ticketTemplates);
        return event;
    }

    public Page<Event> getEventsByFilters(String name, String city, String type, String ispromotion,
                                          LocalDateTime startDate, LocalDateTime endDate,
                                          Double minPrice, Double maxPrice,
                                          int page, int size) {
        var pageable = PageRequest.of(page, size);
        
        var spec = Specification.where(EventSpecifications.hasName(name))
            .and(EventSpecifications.hasCity(city))
            .and(EventSpecifications.hasType(type))
            .and(EventSpecifications.hasPromotion(ispromotion))
            .and(EventSpecifications.betweenDates(startDate, endDate))
            .and(EventSpecifications.hasTicketPriceBetween(minPrice, maxPrice));

        var eventsPage = eventRepository.findAll(spec, pageable);
        
        // Filter out purchased tickets from all events
        eventsPage.forEach(event -> {
            var ticketTemplates = event.getTicketTypes().stream()
                    .filter(tt -> tt.getQrCodePath() == null)  // Only templates (no QR codes)
                    .toList();
            event.setTicketTypes(ticketTemplates);
        });
        
        return eventsPage;
    }

    public EventResponse createEvent(EventCreateRequest request) {
        // Create event without ticket types first
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

        // Establish bidirectional relationship with ticket types
        if (request.ticketTypes() != null && !request.ticketTypes().isEmpty()) {
            for (TicketType ticketType : request.ticketTypes()) {
                ticketType.setEvent(event);
                event.getTicketTypes().add(ticketType);
            }
        }

        var savedEvent = eventRepository.save(event);
        
        // Filter out purchased tickets (only return templates)
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

    public EventResponse updateEvent(Long id, EventUpdateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        // Update only provided fields (partial update)
        if (request.name() != null) {
            event.setName(request.name());
        }
        if (request.dateTime() != null) {
            event.setDateTime(request.dateTime());
        }
        if (request.venue() != null) {
            event.setVenue(request.venue());
        }
        if (request.address() != null) {
            event.setAddress(request.address());
        }
        if (request.city() != null) {
            event.setCity(request.city());
        }
        if (request.type() != null) {
            event.setType(request.type());
        }
        if (request.ispromotion() != null) {
            event.setIspromotion(request.ispromotion());
        }
        if (request.latitude() != null) {
            event.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            event.setLongitude(request.longitude());
        }
        if (request.capacity() != null) {
            event.setCapacity(request.capacity());
        }
        if (request.description() != null) {
            event.setDescription(request.description());
        }
        if (request.banner_url() != null) {
            event.setBanner_url(request.banner_url());
        }
        if (request.image_url() != null) {
            event.setImage_url(request.image_url());
        }
        if (request.ticketTypes() != null) {
            // IMPORTANT: Only clear ticket TEMPLATES, not purchased tickets (which have QR codes)
            event.getTicketTypes().removeIf(tt -> tt.getQrCodePath() == null);
            
            // Add new ticket type templates with proper relationship
            for (TicketType ticketType : request.ticketTypes()) {
                ticketType.setEvent(event);
                event.getTicketTypes().add(ticketType);
            }
        }

        var updatedEvent = eventRepository.save(event);
        
        // Filter out purchased tickets (only return templates)
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
}