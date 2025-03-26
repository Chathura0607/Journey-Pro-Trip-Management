package lk.ijse.journeyprotripmanagementbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.journeyprotripmanagementbackend.dto.AdminDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.service.AdminService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Endpoint to add a new admin
    @PostMapping("/add")
    public ResponseEntity<ResponseDTO> addAdmin(@Valid @RequestBody AdminDTO adminDTO) {
        try {
            if (adminService.existsByEmail(adminDTO.getEmail())) {
                return new ResponseEntity<>(
                        new ResponseDTO(VarList.Conflict, "Admin already exists", null),
                        HttpStatus.CONFLICT
                );
            }
            String response = adminService.addAdmin(adminDTO);
            return response.equals("00")
                    ? new ResponseEntity<>(
                    new ResponseDTO(VarList.Created, "Admin added successfully", adminDTO),
                    HttpStatus.CREATED
            )
                    : new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "Failed to add admin", null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "An error occurred: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Endpoint to update an existing admin
    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateAdmin(@Valid @RequestBody AdminDTO adminDTO) {
        try {
            if (!adminService.existsByEmail(adminDTO.getEmail())) {
                return new ResponseEntity<>(
                        new ResponseDTO(VarList.Not_Found, "Admin not found", null),
                        HttpStatus.NOT_FOUND
                );
            }
            String response = adminService.updateAdmin(adminDTO);
            return response.equals("00")
                    ? new ResponseEntity<>(
                    new ResponseDTO(VarList.OK, "Admin updated successfully", adminDTO),
                    HttpStatus.OK
            )
                    : new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "Failed to update admin", null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "An error occurred: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Endpoint to delete an admin by email
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDTO> deleteAdmin(@RequestParam String email) {
        try {
            if (!adminService.existsByEmail(email)) {
                return new ResponseEntity<>(
                        new ResponseDTO(VarList.Not_Found, "Admin not found", null),
                        HttpStatus.NOT_FOUND
                );
            }
            String response = adminService.deleteAdmin(email);
            return response.equals("00")
                    ? new ResponseEntity<>(
                    new ResponseDTO(VarList.OK, "Admin deleted successfully", null),
                    HttpStatus.OK
            )
                    : new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "Failed to delete admin", null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "An error occurred: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Endpoint to search for an admin by email
    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchAdmin(@RequestParam String email) {
        try {
            AdminDTO adminDTO = adminService.searchAdmin(email);
            return adminDTO != null
                    ? new ResponseEntity<>(
                    new ResponseDTO(VarList.OK, "Admin found", adminDTO),
                    HttpStatus.OK
            )
                    : new ResponseEntity<>(
                    new ResponseDTO(VarList.Not_Found, "Admin not found", null),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "An error occurred: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Endpoint to get all admins
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAllAdmins() {
        try {
            List<AdminDTO> adminList = adminService.getAllAdmins();
            return !adminList.isEmpty()
                    ? new ResponseEntity<>(
                    new ResponseDTO(VarList.OK, "Admins retrieved successfully", adminList),
                    HttpStatus.OK
            )
                    : new ResponseEntity<>(
                    new ResponseDTO(VarList.Not_Found, "No admins found", null),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseDTO(VarList.Internal_Server_Error, "An error occurred: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}

