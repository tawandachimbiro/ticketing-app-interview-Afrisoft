package com.changamire.bus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Bus Schedule Repository
 * <p>
 * This repository manages bus schedules including travel dates, departure/arrival times,
 * seat availability, and relationships with routes and services.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public interface BusScheduleRepository extends JpaRepository<BusSchedule, Long> {


    @Query("SELECT s FROM BusSchedule s JOIN FETCH s.busRoute r JOIN FETCH r.busService")
    List<BusSchedule> findAllWithRouteAndService();

    @Query("select bs from BusSchedule bs where bs.busRoute.id =:id ")
    List<BusSchedule> getBusScheduleByRoutes(@Param("id")Long routeId);

    Optional<Object> findByTicketId(String ticketId);
}
