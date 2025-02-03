package com.changamire.bus;

import com.changamire.bus.BusRoute;
import com.changamire.bus.BusSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BusScheduleRepository extends JpaRepository<BusSchedule, Long> {

    // Custom query to fetch schedules with route and service
    @Query("SELECT s FROM BusSchedule s JOIN FETCH s.busRoute r JOIN FETCH r.busService")
    List<BusSchedule> findAllWithRouteAndService();

    @Query("select bs from BusSchedule bs where bs.busRoute.id =:id ")
    List<BusSchedule> getBusScheduleByRoutes(@Param("id")Long routeId);

    Optional<Object> findByTicketId(String ticketId);
}
