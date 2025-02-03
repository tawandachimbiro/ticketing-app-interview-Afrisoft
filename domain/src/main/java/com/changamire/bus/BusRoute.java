package com.changamire.bus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BusRoute {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String departureTown;
    private String destinationTown;
    private String departureTime;
    private BigDecimal ticketPrice;
    private int ticketSize;



    @ManyToOne
    @JoinColumn(name = "bus_service_id", nullable = true)
    private BusService busService;

    @OneToMany(mappedBy = "busRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusSchedule> schedules = new ArrayList<>();

    public BusRoute(Object o, String departure, String destination, BigDecimal bigDecimal, int i, BusService service) {
    }


    public void addSchedule(BusSchedule schedule) {
        schedules.add(schedule);
        schedule.setBusRoute(this);
    }

    public void removeSchedule(BusSchedule schedule) {
        schedules.remove(schedule);
        schedule.setBusRoute(null);
    }
}
