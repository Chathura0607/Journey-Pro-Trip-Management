package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.BookingDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.service.BookingService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<ResponseDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        int result = bookingService.createBooking(bookingDTO);
        if (result == VarList.Created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO(VarList.Created, "Booking created successfully", null));
        } else if (result == VarList.Not_Found) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User or Trip not found", null));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to create booking", null));
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ResponseDTO> getBookingById(@PathVariable String bookingId) {
        BookingDTO bookingDTO = bookingService.getBookingById(bookingId);
        if (bookingDTO != null) {
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Booking found", bookingDTO));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "Booking not found", null));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllBookingsForUser(@RequestParam String userId) {
        try {
            List<BookingDTO> bookings = bookingService.getAllBookingsForUser(userId);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Bookings fetched successfully", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch bookings", null));
        }
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ResponseDTO> cancelBooking(@PathVariable String bookingId) {
        int result = bookingService.cancelBooking(bookingId);
        if (result == VarList.OK) {
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Booking canceled successfully", null));
        } else if (result == VarList.Not_Found) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "Booking not found", null));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to cancel booking", null));
        }
    }
}