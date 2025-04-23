package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.HotelDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.service.HotelService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/hotels")
@CrossOrigin(origins = "*")
public class UserHotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllHotels() {
        try {
            List<HotelDTO> hotels = hotelService.getAllHotels();
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Hotels fetched successfully", hotels));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch hotels", null));
        }
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<ResponseDTO> getHotelById(@PathVariable String hotelId) {
        try {
            HotelDTO hotelDTO = hotelService.getHotelById(hotelId);
            if (hotelDTO != null) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Hotel found", hotelDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Hotel not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch hotel", null));
        }
    }

    // Add other user-specific hotel endpoints as needed
}