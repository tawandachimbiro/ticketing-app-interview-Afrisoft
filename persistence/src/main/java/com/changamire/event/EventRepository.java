package com.changamire.event;

import com.changamire.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Event Repository
 * 
 * This repository provides data access operations for Event entities.
 * It supports standard CRUD operations and advanced filtering using JPA Specifications.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
}
