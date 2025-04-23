package lk.ijse.journeyprotripmanagementbackend.controller;

import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;
import lk.ijse.journeyprotripmanagementbackend.service.AdminUserService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllUsers(@RequestParam(required = false) String role) {
        try {
            List<UserDTO> users = adminUserService.getAllUsers(role);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error retrieving users", null));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable String userId) {
        try {
            UserDTO user = adminUserService.getUserById(userId);
            if (user != null) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "User retrieved successfully", user));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error retrieving user", null));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDTO> deleteUser(
            @PathVariable String userId,
            @RequestBody(required = false) Map<String, String> request) {
        
        try {
            String reason = request != null ? request.get("reason") : null;
            boolean notifyUser = request != null && Boolean.parseBoolean(request.get("notifyUser"));
            
            int result = adminUserService.deleteUser(userId, reason, notifyUser);
            
            if (result == VarList.OK) {
                return ResponseEntity.ok(new ResponseDTO(VarList.OK, "User deleted successfully", null));
            } else if (result == VarList.Not_Found) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "User not found", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDTO(VarList.Internal_Server_Error, "Error deleting user", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error deleting user", null));
        }
    }

    @GetMapping("/export")
    public void exportUsersToCsv(
            @RequestParam(required = false) String role,
            HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=users_export.csv");

        List<UserDTO> users = adminUserService.getAllUsers(role);

        try (PrintWriter writer = response.getWriter()) {
            writer.println("ID,First Name,Last Name,Email,Phone,Address,Role,Joined Date");
            for (UserDTO user : users) {
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getAddress(),
                        user.getRole(),
                        user.getCreatedAt()));
            }
        }
    }
}