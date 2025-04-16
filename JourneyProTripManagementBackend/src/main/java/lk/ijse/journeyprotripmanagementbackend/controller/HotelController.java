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
@RequestMapping("/api/v1/admin/hotels")
@CrossOrigin(origins = "*")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @PostMapping
    public ResponseEntity<ResponseDTO> saveHotel(@RequestBody HotelDTO hotelDTO) {
        try {
            String res = hotelService.saveHotel(hotelDTO);
            if (res.equals(VarList.OK)) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseDTO(VarList.Created, "Hotel added successfully", hotelDTO));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Failed to add hotel", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to add hotel", null));
        }
    }

    @PutMapping
    public ResponseEntity<ResponseDTO> updateHotel(@RequestBody HotelDTO hotelDTO) {
        try {
            String res = hotelService.updateHotel(hotelDTO);
            if (res.equals(VarList.OK)) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Hotel updated successfully", hotelDTO));
            } else if (res.equals(VarList.Not_Found)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Hotel not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Failed to update hotel", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to update hotel", null));
        }
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<ResponseDTO> deleteHotel(@PathVariable String hotelId) {
        try {
            String res = hotelService.deleteHotel(hotelId);
            if (res.equals(VarList.OK)) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Hotel deleted successfully", null));
            } else if (res.equals(VarList.Not_Found)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "Hotel not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO(VarList.Bad_Request, "Failed to delete hotel", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to delete hotel", null));
        }
    }

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

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchHotelsByLocation(@RequestParam String location) {
        try {
            List<HotelDTO> hotels = hotelService.searchHotelsByLocation(location);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Hotels fetched successfully", hotels));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to search hotels", null));
        }
    }
}