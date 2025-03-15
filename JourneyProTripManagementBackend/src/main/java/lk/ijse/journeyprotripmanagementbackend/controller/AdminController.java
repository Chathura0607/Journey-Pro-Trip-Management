package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.AdminDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.service.AdminService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchAdmin(@RequestParam String email) {
        AdminDTO adminDTO = adminService.searchAdmin(email);
        if (adminDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(VarList.OK, "Admin found", adminDTO));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(VarList.Not_Found, "Admin not found", null));
        }
    }
}