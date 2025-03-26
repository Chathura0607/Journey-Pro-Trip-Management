package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.VehicleDTO;
import lk.ijse.journeyprotripmanagementbackend.service.VehicleService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllVehicles() {
        try {
            List<VehicleDTO> vehicles = vehicleService.getAllVehicles();
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Vehicles fetched successfully", vehicles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch vehicles", null));
        }
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<ResponseDTO> getVehicleById(@PathVariable String vehicleId) {
        try {
            VehicleDTO vehicleDTO = vehicleService.getVehicleById(vehicleId);
            if (vehicleDTO != null) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Vehicle found", vehicleDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Vehicle not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch vehicle", null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchVehiclesByType(@RequestParam String type) {
        try {
            List<VehicleDTO> vehicles = vehicleService.searchVehiclesByType(type);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Vehicles fetched successfully", vehicles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to search vehicles", null));
        }
    }
}