// New PaymentServiceImpl.java with transaction management
package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.PaymentDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Booking;
import lk.ijse.journeyprotripmanagementbackend.entity.Notification;
import lk.ijse.journeyprotripmanagementbackend.entity.Payment;
import lk.ijse.journeyprotripmanagementbackend.entity.User;
import lk.ijse.journeyprotripmanagementbackend.enums.PaymentStatus;
import lk.ijse.journeyprotripmanagementbackend.repo.BookingRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.NotificationRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.PaymentRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.UserRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public int processPayment(PaymentDTO paymentDTO) {
        try {
            // Convert IDs from String to UUID
            UUID userId = UUID.fromString(paymentDTO.getUserId());
            UUID bookingId = UUID.fromString(paymentDTO.getBookingId());

            // Find the user and booking
            Optional<User> optionalUser = userRepository.findById(userId);
            Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

            if (optionalUser.isPresent() && optionalBooking.isPresent()) {
                User user = optionalUser.get();
                Booking booking = optionalBooking.get();

                // Check if payment already exists for this booking
                List<Payment> existingPayments = paymentRepository.findAll()
                        .stream()
                        .filter(p -> p.getBooking().getId().equals(bookingId))
                        .collect(Collectors.toList());

                if (!existingPayments.isEmpty()) {
                    return VarList.Conflict; // Payment already exists
                }

                // Create new payment
                Payment payment = new Payment();
                payment.setUser(user);
                payment.setBooking(booking);
                payment.setPaymentMethod(paymentDTO.getPaymentMethod());
                payment.setAmount(paymentDTO.getAmount());
                payment.setCurrency(paymentDTO.getCurrency() != null ? paymentDTO.getCurrency() : "USD");
                payment.setDescription(paymentDTO.getDescription());
                payment.setMerchantId(paymentDTO.getMerchantId() != null ? paymentDTO.getMerchantId() : "JOURNEYPRO-MERCHANT");
                payment.setOrderId(paymentDTO.getOrderId() != null ? paymentDTO.getOrderId() : UUID.randomUUID().toString());
                payment.setTransactionId(paymentDTO.getTransactionId() != null ? paymentDTO.getTransactionId() : UUID.randomUUID().toString());
                payment.setStatusCode("2"); // Success code
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setCreatedAt(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());

                // Save payment
                paymentRepository.save(payment);

                // Create notification for the user
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setMessage("Your payment of " + payment.getAmount() + " " + payment.getCurrency() +
                        " for " + booking.getBookingType() + " booking has been processed successfully.");
                notification.setIsRead(false);
                notification.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(notification);

                return VarList.Created; // Success
            } else {
                return VarList.Not_Found; // User or booking not found
            }
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            throw e; // Rethrow to trigger transaction rollback
        }
    }

    @Override
    public PaymentDTO getPaymentById(String paymentId) {
        UUID uuid = UUID.fromString(paymentId);
        Optional<Payment> optionalPayment = paymentRepository.findById(uuid);

        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            return modelMapper.map(payment, PaymentDTO.class);
        } else {
            return null; // Payment not found
        }
    }

    @Override
    public List<PaymentDTO> getAllPaymentsForUser(String userId) {
        UUID uuid = UUID.fromString(userId);

        List<Payment> payments = paymentRepository.findAll()
                .stream()
                .filter(p -> p.getUser().getId().equals(uuid))
                .collect(Collectors.toList());

        return payments.stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getAllPaymentsForBooking(String bookingId) {
        UUID uuid = UUID.fromString(bookingId);

        List<Payment> payments = paymentRepository.findAll()
                .stream()
                .filter(p -> p.getBooking().getId().equals(uuid))
                .collect(Collectors.toList());

        return payments.stream()
                .map(payment -> modelMapper.map(payment, PaymentDTO.class))
                .collect(Collectors.toList());
    }
}