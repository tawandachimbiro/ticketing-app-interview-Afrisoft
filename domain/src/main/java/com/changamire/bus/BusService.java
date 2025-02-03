package com.changamire.bus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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
