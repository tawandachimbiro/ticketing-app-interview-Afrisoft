package com.changamire.event;

import com.changamire.exceptions.EventNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Page<Event> getAllEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return eventRepository.findAll(pageable);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Page<Event> getEventsByFilters(String name, String city, String type, String ispromotion,
                                          LocalDateTime startDate, LocalDateTime endDate,
                                          Double minPrice, Double maxPrice,
                                          int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        Specification<Event> spec = Specification.where(EventSpecifications.hasName(name))
            .and(EventSpecifications.hasCity(city))
            .and(EventSpecifications.hasType(type))
            .and(EventSpecifications.hasPromotion(ispromotion))
            .and(EventSpecifications.betweenDates(startDate, endDate))
            .and(EventSpecifications.hasTicketPriceBetween(minPrice, maxPrice));

        return eventRepository.findAll(spec, pageable);
    }

    public EventResponse createEvent(EventCreateRequest request) {
        Event event = Event.builder()
                .name(request.getName())
                .dateTime(request.getDateTime())
                .venue(request.getVenue())
                .address(request.getAddress())
                .city(request.getCity())
                .type(request.getType())
                .ispromotion(request.getIspromotion())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .capacity(request.getCapacity())
                .description(request.getDescription())
                .banner_url(request.getBanner_url())
                .image_url(request.getImage_url())
                .ticketTypes(request.getTicketTypes())
                .build();

        Event savedEvent = eventRepository.save(event);

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
                .ticketTypes(savedEvent.getTicketTypes())
                .message("Event created successfully")
                .build();
    }

    public EventResponse updateEvent(Long id, EventUpdateRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        // Update only provided fields (partial update)
        if (request.getName() != null) {
            event.setName(request.getName());
        }
        if (request.getDateTime() != null) {
            event.setDateTime(request.getDateTime());
        }
        if (request.getVenue() != null) {
            event.setVenue(request.getVenue());
        }
        if (request.getAddress() != null) {
            event.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            event.setCity(request.getCity());
        }
        if (request.getType() != null) {
            event.setType(request.getType());
        }
        if (request.getIspromotion() != null) {
            event.setIspromotion(request.getIspromotion());
        }
        if (request.getLatitude() != null) {
            event.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            event.setLongitude(request.getLongitude());
        }
        if (request.getCapacity() != null) {
            event.setCapacity(request.getCapacity());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getBanner_url() != null) {
            event.setBanner_url(request.getBanner_url());
        }
        if (request.getImage_url() != null) {
            event.setImage_url(request.getImage_url());
        }
        if (request.getTicketTypes() != null) {
            event.setTicketTypes(request.getTicketTypes());
        }

        Event updatedEvent = eventRepository.save(event);

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
                .ticketTypes(updatedEvent.getTicketTypes())
                .message("Event updated successfully")
                .build();
    }
}