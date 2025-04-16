package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.AuthDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;
import lk.ijse.journeyprotripmanagementbackend.service.AuthService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ResponseDTO> login(@RequestBody UserDTO userDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            AuthDTO authDTO = authService.login(userDTO.getEmail(), userDTO.getPassword());
            // Include email in the response
            authDTO.setEmail(userDTO.getEmail());

            responseDTO.setCode(VarList.OK);
            responseDTO.setMessage("Login successful");
            responseDTO.setData(authDTO);

            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setCode(VarList.Internal_Server_Error);
            responseDTO.setMessage("An error occurred: " + e.getMessage());
            responseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Logout the user by blacklisting the token
        authService.logout(token);

        return ResponseEntity.ok("Logged out successfully");
    }
}