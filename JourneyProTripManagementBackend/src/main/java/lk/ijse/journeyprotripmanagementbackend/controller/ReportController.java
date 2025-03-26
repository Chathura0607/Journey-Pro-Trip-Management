package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.service.ReportService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/user-activity")
    public ResponseEntity<ResponseDTO> generateUserActivityReport() {
        try {
            Object report = reportService.generateUserActivityReport();
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "User activity report generated successfully", report));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to generate user activity report", null));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<ResponseDTO> generateBookingReport() {
        try {
            Object report = reportService.generateBookingReport();
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Booking report generated successfully", report));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to generate booking report", null));
        }
    }

    @GetMapping("/payments")
    public ResponseEntity<ResponseDTO> generatePaymentReport() {
        try {
            Object report = reportService.generatePaymentReport();
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Payment report generated successfully", report));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to generate payment report", null));
        }
    }
}