package com.changamire.bus;

import com.changamire.bus.BusService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusServiceRepository extends JpaRepository<BusService, Long> {
    Optional<BusService> findByName(String intercape);
}

