package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.VehicleDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Vehicle;
import lk.ijse.journeyprotripmanagementbackend.enums.VehicleType;
import lk.ijse.journeyprotripmanagementbackend.repo.VehicleRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<VehicleDTO> getAllVehicles() {
        // Fetch all vehicles from the database
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // Map vehicles to VehicleDTO
        return vehicles.stream()
                .map(vehicle -> modelMapper.map(vehicle, VehicleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VehicleDTO getVehicleById(String vehicleId) {
        // Convert the vehicleId from String to UUID
        UUID uuid = UUID.fromString(vehicleId);

        // Find the vehicle by ID
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(uuid);

        // If the vehicle exists, map it to VehicleDTO and return
        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();
            return modelMapper.map(vehicle, VehicleDTO.class);
        } else {
            return null; // Vehicle not found
        }
    }

    @Override
    public List<VehicleDTO> searchVehiclesByType(String type) {
        // Convert the type to VehicleType enum
        VehicleType vehicleType = VehicleType.valueOf(type.toUpperCase());

        // Fetch vehicles by type
        List<Vehicle> vehicles = vehicleRepository.findByVehicleType(vehicleType);

        // Map vehicles to VehicleDTO
        return vehicles.stream()
                .map(vehicle -> modelMapper.map(vehicle, VehicleDTO.class))
                .collect(Collectors.toList());
    }
}