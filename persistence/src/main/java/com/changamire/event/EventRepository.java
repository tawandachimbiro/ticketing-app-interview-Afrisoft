package com.changamire.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Event Repository
 *
 * This repository provides data access operations for Event entities.
 * It supports standard CRUD operations and advanced filtering using JPA Specifications.
 * Soft-deleted events (deleted = true) are excluded from normal find operations.
 *
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findByDeletedFalse(Pageable pageable);

    Optional<Event> findByIdAndDeletedFalse(Long id);
}
