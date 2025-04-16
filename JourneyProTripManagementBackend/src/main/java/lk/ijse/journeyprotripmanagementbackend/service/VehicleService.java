package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.VehicleDTO;
import java.util.List;

public interface VehicleService {
    String saveVehicle(VehicleDTO vehicleDTO);
    String updateVehicle(VehicleDTO vehicleDTO);
    String deleteVehicle(String vehicleId);
    List<VehicleDTO> getAllVehicles();
    VehicleDTO getVehicleById(String vehicleId);
    List<VehicleDTO> searchVehiclesByType(String type);
}
