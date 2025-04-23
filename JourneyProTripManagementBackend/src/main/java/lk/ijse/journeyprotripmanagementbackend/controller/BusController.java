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
@RequestMapping("/api/v1/admin/buses")
@CrossOrigin(origins = "*")
public class BusController {

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

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchBusesByRoute(@RequestParam String route) {
        try {
            List<BusDTO> buses = busService.searchBusesByRoute(route);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Buses fetched successfully", buses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to search buses", null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> saveBus(@RequestBody BusDTO busDTO) {
        try {
            String result = busService.saveBus(busDTO);
            if (result.equals(String.valueOf(VarList.OK))) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Bus saved successfully", busDTO));
            } else if (result.equals(String.valueOf(VarList.Not_Acceptable))) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body(new ResponseDTO(VarList.Not_Acceptable, "Bus with this number already exists", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Failed to save bus", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to save bus", null));
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDTO> updateBus(@RequestBody BusDTO busDTO) {
        try {
            String result = busService.updateBus(busDTO);
            if (result.equals(String.valueOf(VarList.OK))) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Bus updated successfully", busDTO));
            } else if (result.equals(String.valueOf(VarList.Not_Found))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Bus not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Failed to update bus", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to update bus", null));
        }
    }

    @DeleteMapping("/{busId}")
    public ResponseEntity<ResponseDTO> deleteBus(@PathVariable String busId) {
        try {
            String result = busService.deleteBus(busId);
            if (result.equals(String.valueOf(VarList.OK))) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Bus deleted successfully", null));
            } else if (result.equals(String.valueOf(VarList.Not_Found))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Bus not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Failed to delete bus", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to delete bus", null));
        }
    }
}