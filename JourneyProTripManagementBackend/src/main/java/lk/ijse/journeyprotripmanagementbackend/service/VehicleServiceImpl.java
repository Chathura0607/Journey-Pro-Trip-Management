package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.VehicleDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Vehicle;
import lk.ijse.journeyprotripmanagementbackend.enums.VehicleType;
import lk.ijse.journeyprotripmanagementbackend.repo.VehicleRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public String saveVehicle(VehicleDTO vehicleDTO) {
        if (vehicleRepository.existsByRegistrationNumber(vehicleDTO.getRegistrationNumber())) {
            return "Vehicle with this registration number already exists";
        }

        Vehicle vehicle = modelMapper.map(vehicleDTO, Vehicle.class);
        vehicleRepository.save(vehicle);
        return String.valueOf(VarList.OK);
    }

    @Override
    public String updateVehicle(VehicleDTO vehicleDTO) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(UUID.fromString(vehicleDTO.getId()));
        if (optionalVehicle.isEmpty()) {
            return "Vehicle not found";
        }

        Vehicle existingVehicle = vehicleRepository.findByRegistrationNumber(vehicleDTO.getRegistrationNumber());
        if (existingVehicle != null && !existingVehicle.getId().toString().equals(vehicleDTO.getId())) {
            return "Another vehicle with this registration number already exists";
        }

        Vehicle vehicle = optionalVehicle.get();
        vehicle.setVehicleType(vehicleDTO.getVehicleType());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setRegistrationNumber(vehicleDTO.getRegistrationNumber());
        vehicle.setSeatCapacity(vehicleDTO.getSeatCapacity());
        vehicle.setIsAvailable(vehicleDTO.getIsAvailable());

        vehicleRepository.save(vehicle);
        return String.valueOf(VarList.OK);
    }

    @Override
    public String deleteVehicle(String vehicleId) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(UUID.fromString(vehicleId));
        if (optionalVehicle.isEmpty()) {
            return "Vehicle not found";
        }

        vehicleRepository.deleteById(UUID.fromString(vehicleId));
        return String.valueOf(VarList.OK);
    }

    @Override
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(vehicle -> {
                    VehicleDTO dto = modelMapper.map(vehicle, VehicleDTO.class);
                    dto.setId(vehicle.getId().toString());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public VehicleDTO getVehicleById(String vehicleId) {
        return vehicleRepository.findById(UUID.fromString(vehicleId))
                .map(vehicle -> {
                    VehicleDTO dto = modelMapper.map(vehicle, VehicleDTO.class);
                    dto.setId(vehicle.getId().toString());
                    return dto;
                }).orElse(null);
    }

    @Override
    public List<VehicleDTO> searchVehiclesByType(String type) {
        VehicleType vehicleType = VehicleType.valueOf(type.toUpperCase());
        return vehicleRepository.findByVehicleType(vehicleType).stream()
                .map(vehicle -> {
                    VehicleDTO dto = modelMapper.map(vehicle, VehicleDTO.class);
                    dto.setId(vehicle.getId().toString());
                    return dto;
                }).collect(Collectors.toList());
    }
}
