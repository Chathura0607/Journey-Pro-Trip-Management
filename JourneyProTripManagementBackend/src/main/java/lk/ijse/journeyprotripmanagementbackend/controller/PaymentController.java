// New PaymentController.java
package lk.ijse.journeyprotripmanagementbackend.controller;

import lk.ijse.journeyprotripmanagementbackend.dto.PaymentDTO;
import lk.ijse.journeyprotripmanagementbackend.dto.ResponseDTO;
import lk.ijse.journeyprotripmanagementbackend.service.PaymentService;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<ResponseDTO> processPayment(@RequestBody PaymentDTO paymentDTO) {
        int result = paymentService.processPayment(paymentDTO);

        switch (result) {
            case VarList.Created:
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseDTO(VarList.Created, "Payment processed successfully", null));
            case VarList.Not_Found:
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(VarList.Not_Found, "User or booking not found", null));
            case VarList.Conflict:
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseDTO(VarList.Conflict, "Payment already exists for this booking", null));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to process payment", null));
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ResponseDTO> getPaymentById(@PathVariable String paymentId) {
        PaymentDTO paymentDTO = paymentService.getPaymentById(paymentId);

        if (paymentDTO != null) {
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Payment found", paymentDTO));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(VarList.Not_Found, "Payment not found", null));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> getAllPaymentsForUser(@PathVariable String userId) {
        try {
            List<PaymentDTO> payments = paymentService.getAllPaymentsForUser(userId);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Payments fetched successfully", payments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch payments", null));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ResponseDTO> getAllPaymentsForBooking(@PathVariable String bookingId) {
        try {
            List<PaymentDTO> payments = paymentService.getAllPaymentsForBooking(bookingId);
            return ResponseEntity.ok(new ResponseDTO(VarList.OK, "Payments fetched successfully", payments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Failed to fetch payments", null));
        }
    }
}