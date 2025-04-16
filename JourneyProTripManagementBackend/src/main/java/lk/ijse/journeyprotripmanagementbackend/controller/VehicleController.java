package lk.ijse.journeyprotripmanagementbackend.controller;

import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<ResponseDTO> saveVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        try {
            String res = vehicleService.saveVehicle(vehicleDTO);
            if (res.equals(String.valueOf(VarList.OK))) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseDTO(VarList.Created, "Vehicle added successfully", vehicleDTO));
            } else if (res.contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO(VarList.Conflict, res, null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, res, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to add vehicle: " + e.getMessage(), null));
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDTO> updateVehicle(@Valid @RequestBody VehicleDTO vehicleDTO) {
        try {
            String res = vehicleService.updateVehicle(vehicleDTO);
            if (res.equals(String.valueOf(VarList.OK))) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Vehicle updated successfully", vehicleDTO));
            } else if (res.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, res, null));
            } else if (res.contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO(VarList.Conflict, res, null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, res, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to update vehicle: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<ResponseDTO> deleteVehicle(@PathVariable String vehicleId) {
        try {
            String res = vehicleService.deleteVehicle(vehicleId);
            if (res.equals(String.valueOf(VarList.OK))) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Vehicle deleted successfully", null));
            } else if (res.contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, res, null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, res, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to delete vehicle: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllVehicles() {
        try {
            List<VehicleDTO> vehicles = vehicleService.getAllVehicles();
            return !vehicles.isEmpty()
                    ? ResponseEntity.ok(new ResponseDTO(VarList.OK, "Vehicles retrieved successfully", vehicles))
                    : ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "No vehicles found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to retrieve vehicles: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<ResponseDTO> getVehicleById(@PathVariable String vehicleId) {
        try {
            VehicleDTO vehicleDTO = vehicleService.getVehicleById(vehicleId);
            return vehicleDTO != null
                    ? ResponseEntity.ok(new ResponseDTO(VarList.OK, "Vehicle found", vehicleDTO))
                    : ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "Vehicle not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to retrieve vehicle: " + e.getMessage(), null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchVehiclesByType(@RequestParam String type) {
        try {
            List<VehicleDTO> vehicles = vehicleService.searchVehiclesByType(type);
            return !vehicles.isEmpty()
                    ? ResponseEntity.ok(new ResponseDTO(VarList.OK, "Vehicles retrieved successfully", vehicles))
                    : ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "No vehicles found for type: " + type, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to search vehicles: " + e.getMessage(), null));
        }
    }
}
