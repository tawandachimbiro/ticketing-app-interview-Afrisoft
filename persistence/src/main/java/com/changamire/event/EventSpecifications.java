package com.changamire.event;

import com.changamire.ticket.TicketType;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.function.Predicate;

/**
 * Event Specifications
 * <p>
 * This utility class provides JPA Specifications for dynamic event filtering
 * and querying. Used by EventRepository to build complex search criteria
 * for event searches by name, city, type, dates, and ticket prices.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public class EventSpecifications  {

    public static Specification<Event> hasName(String name) {
        return (root, query, criteriaBuilder) -> 
            name != null ? criteriaBuilder.like(root.get("name"), "%" + name + "%") : null;
    }

    public static Specification<Event> hasCity(String city) {
        return (root, query, criteriaBuilder) -> 
            city != null ? criteriaBuilder.equal(root.get("city"), city) : null;
    }

    public static Specification<Event> hasType(String type) {
        return (root, query, criteriaBuilder) -> 
            type != null ? criteriaBuilder.equal(root.get("type"), type) : null;
    }

    public static Specification<Event> hasPromotion(String ispromotion) {
        return (root, query, criteriaBuilder) -> 
            ispromotion != null ? criteriaBuilder.equal(root.get("ispromotion"), ispromotion) : null;
    }

    public static Specification<Event> betweenDates(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) -> {
            if (start == null && end == null) return null;
            if (start == null) return criteriaBuilder.lessThanOrEqualTo(root.get("dateTime"), end);
            if (end == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("dateTime"), start);
            return criteriaBuilder.between(root.get("dateTime"), start, end);
        };
    }

    public static Specification<Event> hasTicketPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) return null;

            // Use join instead of subquery
            Join<Event, TicketType> ticketJoin = root.join("ticketTypes");

            Predicate minPredicate = minPrice != null
                    ? (Predicate) criteriaBuilder.ge(ticketJoin.get("price"), minPrice)
                    : null;

            Predicate maxPredicate = maxPrice != null
                    ? (Predicate) criteriaBuilder.le(ticketJoin.get("price"), maxPrice)
                    : null;

            return criteriaBuilder.and((Expression<Boolean>) minPredicate, (Expression<Boolean>) maxPredicate);
        };
    }

    /**
     * Exclude soft-deleted events.
     */
    public static Specification<Event> notDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("deleted"));
    }


}