package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;
import lk.ijse.journeyprotripmanagementbackend.service.UserService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody UserDTO userDTO) {
        int result = userService.saveUser(userDTO);
        if (result == VarList.Created) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(VarList.Created, "User registered successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(VarList.Not_Acceptable, "User already exists", null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchUser(@RequestParam String email) {
        UserDTO userDTO = userService.searchUser(email);
        if (userDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(VarList.OK, "User found", userDTO));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(VarList.Not_Found, "User not found", null));
        }
    }
}