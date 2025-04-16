package lk.ijse.journeyprotripmanagementbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;
import lk.ijse.journeyprotripmanagementbackend.service.UserService;
import lk.ijse.journeyprotripmanagementbackend.util.JwtUtil;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> registerUser(
            @RequestPart("userData") String userDataStr,
            @RequestPart(value = "profilePicture", required = false) MultipartFile file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDTO = objectMapper.readValue(userDataStr, UserDTO.class);

        if (file != null && !file.isEmpty()) {
            String fileName = storeProfilePicture(file);
            userDTO.setProfilePicture(fileName);
        }

        int result = userService.saveUser(userDTO);
        if (result == VarList.Created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO(VarList.Created, "User registered successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(VarList.Not_Acceptable, "User already exists", null));
        }
    }

    private String storeProfilePicture(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
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
