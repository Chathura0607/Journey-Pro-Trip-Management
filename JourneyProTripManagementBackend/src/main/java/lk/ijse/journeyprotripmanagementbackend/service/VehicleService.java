package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.VehicleDTO;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;

import java.util.List;

public interface VehicleService {
    List<VehicleDTO> getAllVehicles();
    VehicleDTO getVehicleById(String vehicleId);
    List<VehicleDTO> searchVehiclesByType(String type);
}