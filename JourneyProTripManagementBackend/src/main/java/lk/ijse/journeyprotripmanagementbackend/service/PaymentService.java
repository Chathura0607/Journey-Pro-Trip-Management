// New PaymentService.java interface
package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.PaymentDTO;

import java.util.List;

public interface PaymentService {
    int processPayment(PaymentDTO paymentDTO);
    PaymentDTO getPaymentById(String paymentId);
    List<PaymentDTO> getAllPaymentsForUser(String userId);
    List<PaymentDTO> getAllPaymentsForBooking(String bookingId);
}