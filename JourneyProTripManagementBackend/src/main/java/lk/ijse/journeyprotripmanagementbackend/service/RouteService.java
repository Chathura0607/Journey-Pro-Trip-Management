package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.RouteDTO;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;

import java.util.List;

public interface RouteService {
    List<RouteDTO> getAllRoutes();
    RouteDTO getRouteById(String routeId);
    int addNewRoute(RouteDTO routeDTO);
}