package com.changamire.bus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Bus Route Repository
 * 
 * This repository manages bus route data including departure and destination towns,
 * ticket prices, and associations with bus services.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {

    @Query("select br from BusRoute br where br.busService.id =:id ")
    List<BusRouteRepository> getBusRoutesByBusService(@Param("id") Long id);


    @Query("SELECT r FROM BusRoute r WHERE r.departureTown = ?1 AND r.destinationTown = ?2 AND r.busService = ?3")
    Optional<BusRoute> findByDepartureTownAndDestinationTownAndBusService(
            String departureTown,
            String destinationTown,
            BusService service
    );


}
