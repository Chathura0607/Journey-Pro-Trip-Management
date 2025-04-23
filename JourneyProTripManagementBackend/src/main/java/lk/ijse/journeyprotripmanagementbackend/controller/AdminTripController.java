package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.TripDTO;
import lk.ijse.journeyprotripmanagementbackend.service.AdminTripService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/trips")
@CrossOrigin(origins = "*")
public class AdminTripController {

    @Autowired
    private AdminTripService adminTripService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllTrips(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String adminId) {
        try {
            List<TripDTO> trips;
            if (userId != null && !userId.isEmpty()) {
                trips = adminTripService.getTripsByUser(userId);
            } else if (adminId != null && !adminId.isEmpty()) {
                trips = adminTripService.getTripsByAdmin(adminId);
            } else if (status != null && !status.isEmpty()) {
                trips = adminTripService.getTripsByStatus(status);
            } else {
                trips = adminTripService.getAllTrips();
            }
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Trips retrieved successfully", trips));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error retrieving trips", null));
        }
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<ResponseDTO> getTripById(@PathVariable String tripId) {
        try {
            TripDTO tripDTO = adminTripService.getTripById(tripId);
            if (tripDTO != null) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Trip retrieved successfully", tripDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Trip not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error retrieving trip", null));
        }
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createTrip(@RequestBody TripDTO tripDTO) {
        try {
            int result = adminTripService.createTrip(tripDTO);
            if (result == VarList.Created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseDTO(VarList.Created, "Trip created successfully", null));
            } else if (result == VarList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Failed to create trip", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error creating trip", null));
        }
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<ResponseDTO> updateTrip(@PathVariable String tripId, @RequestBody TripDTO tripDTO) {
        try {
            int result = adminTripService.updateTrip(tripId, tripDTO);
            if (result == VarList.OK) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Trip updated successfully", null));
            } else if (result == VarList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Trip not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Failed to update trip", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error updating trip", null));
        }
    }

    @PutMapping("/{tripId}/status")
    public ResponseEntity<ResponseDTO> updateTripStatus(
            @PathVariable String tripId,
            @RequestParam String status) {
        try {
            int result = adminTripService.updateTripStatus(tripId, status);
            if (result == VarList.OK) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Trip status updated successfully", null));
            } else if (result == VarList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Trip not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Invalid status value", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error updating trip status", null));
        }
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<ResponseDTO> deleteTrip(@PathVariable String tripId) {
        try {
            int result = adminTripService.deleteTrip(tripId);
            if (result == VarList.OK) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Trip deleted successfully", null));
            } else if (result == VarList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Trip not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Failed to delete trip", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error deleting trip", null));
        }
    }
}