package com.changamire.event;

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
}