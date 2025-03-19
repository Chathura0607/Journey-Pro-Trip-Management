package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;
import lk.ijse.journeyprotripmanagementbackend.service.UserService;
import lk.ijse.journeyprotripmanagementbackend.util.JwtUtil;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody UserDTO userDTO) {
        int result = userService.saveUser(userDTO);
        if (result == VarList.Created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO(VarList.Created, "User registered successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(VarList.Not_Acceptable, "User already exists", null));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchUser(@RequestParam String email) {
        UserDTO userDTO = userService.searchUser(email);
        if (userDTO != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDTO(VarList.OK, "User found", userDTO));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ResponseDTO> updateProfile(@RequestBody UserDTO userDTO) {
        int result = userService.updateUserProfile(userDTO);

        if (result == VarList.OK) {
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Profile updated successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseDTO> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> passwordData) {

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.getUsernameFromToken(jwt);

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(VarList.Unauthorized, "Invalid token", null));
        }

        // Extract old and new passwords from JSON body
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(VarList.Bad_Request, "Old and new passwords are required", null));
        }

        int result = userService.changePassword(email, oldPassword, newPassword);
        if (result == VarList.OK) {
            return ResponseEntity.ok(new ResponseDTO(VarList.Created, "Password changed successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(VarList.Unauthorized, "Invalid credentials", null));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO> getProfile(@RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String email = jwtUtil.getUsernameFromToken(jwt);

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(VarList.Unauthorized, "Invalid token", null));
        }

        UserDTO userDTO = userService.getUserProfile(email);
        if (userDTO != null) {
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Profile retrieved successfully", userDTO));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
        }
    }
}
