package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.BusDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Bus;
import lk.ijse.journeyprotripmanagementbackend.repo.BusRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BusServiceImpl implements BusService {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<BusDTO> getAllBuses() {
        // Fetch all buses from the database
        List<Bus> buses = busRepository.findAll();

        // Map buses to BusDTO
        return buses.stream()
                .map(bus -> modelMapper.map(bus, BusDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BusDTO getBusById(String busId) {
        // Convert the busId from String to UUID
        UUID uuid = UUID.fromString(busId);

        // Find the bus by ID
        Optional<Bus> optionalBus = busRepository.findById(uuid);

        // If the bus exists, map it to BusDTO and return
        if (optionalBus.isPresent()) {
            Bus bus = optionalBus.get();
            return modelMapper.map(bus, BusDTO.class);
        } else {
            return null; // Bus not found
        }
    }

    @Override
    public List<BusDTO> searchBusesByRoute(String route) {
        // Fetch buses by route
        List<Bus> buses = busRepository.findByRouteContainingIgnoreCase(route);

        // Map buses to BusDTO
        return buses.stream()
                .map(bus -> modelMapper.map(bus, BusDTO.class))
                .collect(Collectors.toList());
    }
}