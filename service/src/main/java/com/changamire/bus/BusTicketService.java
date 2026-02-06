package com.changamire.bus;

import com.changamire.ticket.BusTicketDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Bus Ticket Service
 * <p>
 * This service manages bus ticket operations including retrieving available schedules,
 * creating bus tickets, checking seat availability, and managing bus routes and services.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Service
@RequiredArgsConstructor
public class BusTicketService {

    private final BusScheduleRepository busScheduleRepository;

    private final BusServiceRepository busServiceRepository;
    private final BusRouteRepository busRouteRepository;


    public List<BusTicketDTO> getAllBusTickets() {
        var schedules = busScheduleRepository.findAllWithRouteAndService();
        return schedules.stream()
                .map(this::convertToDTO)
                .toList();
    }


    public BusTicketDTO createBusTicket(BusTicketDTO dto) {
        var towns = validateAndSplitRoute(dto.route());
        var service = getOrCreateBusService(dto.busOperator());
        var route = getOrCreateBusRoute(towns[0], towns[1], service, dto.price());
        var schedule = createScheduleEntity(dto, route);
        busScheduleRepository.save(schedule);
        return convertToDTO(schedule);
    }


    public boolean isSeatAvailable(String ticketId, int quantity) {
        var schedule = (BusSchedule) busScheduleRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return schedule.getAvailableSeats() >= quantity;
    }


    private String[] validateAndSplitRoute(String route) {
        String[] towns = route.split(" to ");
        if (towns.length != 2) throw new IllegalArgumentException("Invalid route format");
        return towns;
    }

    private BusService getOrCreateBusService(String operator) {
        return busServiceRepository.findByName(operator)
                .orElseGet(() -> busServiceRepository.save(
                        new BusService(null, operator, new ArrayList<>())
                ));
    }

    private BusRoute getOrCreateBusRoute(String departure, String destination,
                                         BusService service, double price) {
        return busRouteRepository.findByDepartureTownAndDestinationTownAndBusService(departure, destination, service)
                .orElseGet(() -> busRouteRepository.save(
                        new BusRoute(null, departure, destination,
                                BigDecimal.valueOf(price), 0, service)
                ));
    }
    private BusSchedule createScheduleEntity(BusTicketDTO dto, BusRoute route) {
        return BusSchedule.builder()
                .ticketId(dto.ticketId())
                .travelDate(LocalDate.parse(dto.travelDate()))
                .departureTime(parseTime(dto.departureTime()))
                .arrivalTime(parseTime(dto.arrivalTime()))
                .availableSeats(dto.seatAvailability())
                .busRoute(route)
                .build();
    }

    private BusTicketDTO convertToDTO(BusSchedule schedule) {
        return BusTicketDTO.builder()
                .ticketId(schedule.getTicketId())  // Use actual ticketId from entity
                .route(schedule.getBusRoute().getDepartureTown() + " to " +
                        schedule.getBusRoute().getDestinationTown())
                .price(schedule.getBusRoute().getTicketPrice().doubleValue())
                .departureTime(formatTime(schedule.getDepartureTime()))
                .arrivalTime(formatTime(schedule.getArrivalTime()))
                .busOperator(schedule.getBusRoute().getBusService().getName())
                .seatAvailability(schedule.getAvailableSeats())
                .travelDate(schedule.getTravelDate().toString())
                .build();
    }

    private LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("hh:mm a"));
    }

    private String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }
}