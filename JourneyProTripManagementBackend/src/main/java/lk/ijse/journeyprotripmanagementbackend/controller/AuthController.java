package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.AuthDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;
import lk.ijse.journeyprotripmanagementbackend.service.AuthService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final ResponseDTO responseDTO;

    // Use constructor injection
    @Autowired
    public AuthController(AuthService authService, ResponseDTO responseDTO) {
        this.authService = authService;
        this.responseDTO = responseDTO;
    }

    @PostMapping("/authenticate")
    public ResponseDTO login(@RequestBody UserDTO userDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            // Call the login method in AuthService
            AuthDTO authDTO = authService.login(userDTO.getEmail(), userDTO.getPassword());

            // Set the response
            responseDTO.setCode(VarList.OK);
            responseDTO.setMessage("Login successful");
            responseDTO.setData(authDTO);
        } catch (Exception e) {
            // Handle exceptions
            responseDTO.setCode(VarList.Internal_Server_Error);
            responseDTO.setMessage("An error occurred: " + e.getMessage());
            responseDTO.setData(null);
        }
        return responseDTO;
    }
}