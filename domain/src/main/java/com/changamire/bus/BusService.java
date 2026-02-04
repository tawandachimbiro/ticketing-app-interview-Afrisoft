package com.changamire.bus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Bus Service Entity
 * <p>
 * This entity represents a bus operator/company in the system.
 * It contains the company name and manages associated bus routes.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BusService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "busService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusRoute> routes = new ArrayList<>();

    public List<BusRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<BusRoute> routes) {
        this.routes = routes;
    }

    public void removeRoute(BusRoute route) {
        routes.remove(route);
        route.setBusService(null);
    }
}
