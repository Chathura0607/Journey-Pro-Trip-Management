package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.RouteDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Route;
import lk.ijse.journeyprotripmanagementbackend.repo.RouteRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<RouteDTO> getAllRoutes() {
        // Fetch all routes from the database
        List<Route> routes = routeRepository.findAll();

        // Map routes to RouteDTO
        return routes.stream()
                .map(route -> modelMapper.map(route, RouteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public RouteDTO getRouteById(String routeId) {
        // Convert the routeId from String to UUID
        UUID uuid = UUID.fromString(routeId);

        // Find the route by ID
        Optional<Route> optionalRoute = routeRepository.findById(uuid);

        // If the route exists, map it to RouteDTO and return
        if (optionalRoute.isPresent()) {
            Route route = optionalRoute.get();
            return modelMapper.map(route, RouteDTO.class);
        } else {
            return null; // Route not found
        }
    }

    @Override
    public int addNewRoute(RouteDTO routeDTO) {
        // Map RouteDTO to Route entity
        Route route = modelMapper.map(routeDTO, Route.class);

        // Save the route
        routeRepository.save(route);
        return VarList.Created; // Success
    }
}