package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.BusDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.service.BusService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/buses")
@CrossOrigin(origins = "*")
public class UserBusController {

    @Autowired
    private BusService busService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllBuses() {
        try {
            List<BusDTO> buses = busService.getAllBuses();
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Buses fetched successfully", buses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch buses", null));
        }
    }

    @GetMapping("/{busId}")
    public ResponseEntity<ResponseDTO> getBusById(@PathVariable String busId) {
        try {
            BusDTO busDTO = busService.getBusById(busId);
            if (busDTO != null) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Bus found", busDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Bus not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch bus", null));
        }
    }

    // Add other user-specific bus endpoints as needed
}