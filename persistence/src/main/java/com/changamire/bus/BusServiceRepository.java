package com.changamire.bus;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Bus Service Repository
 * <p>
 * This repository manages bus operator/service data including company names
 * and their associated routes.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public interface BusServiceRepository extends JpaRepository<BusService, Long> {
    Optional<BusService> findByName(String intercape );
}

