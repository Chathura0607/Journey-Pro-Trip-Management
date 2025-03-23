package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.TripDTO;
import lk.ijse.journeyprotripmanagementbackend.service.TripService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/trips")
@CrossOrigin(origins = "*")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createTrip(@RequestBody TripDTO tripDTO) {
        int result = tripService.createTrip(tripDTO);
        if (result == VarList.Created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO(VarList.Created, "Trip created successfully", null));
        } else if (result == VarList.Not_Found) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to create trip", null));
        }
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<ResponseDTO> getTripById(@PathVariable String tripId) {
        TripDTO tripDTO = tripService.getTripById(tripId);
        if (tripDTO != null) {
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Trip found", tripDTO));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "Trip not found", null));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllTripsForUser(@RequestParam String userId) {
        try {
            List<TripDTO> trips = tripService.getAllTripsForUser(userId);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Trips fetched successfully", trips));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch trips", null));
        }
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<ResponseDTO> updateTrip(@PathVariable String tripId, @RequestBody TripDTO tripDTO) {
        try {
            int result = tripService.updateTrip(tripId, tripDTO);
            if (result == VarList.OK) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Trip updated successfully", null));
            } else if (result == VarList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Trip not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to update trip", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to update trip", null));
        }
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<ResponseDTO> deleteTrip(@PathVariable String tripId) {
        try {
            int result = tripService.deleteTrip(tripId);
            if (result == VarList.OK) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Trip deleted successfully", null));
            } else if (result == VarList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Trip not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to delete trip", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to delete trip", null));
        }
    }
}