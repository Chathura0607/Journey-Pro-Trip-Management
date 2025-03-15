package lk.ijse.journeyprotripmanagementbackend.entity;

import jakarta.persistence.*;
import lk.ijse.journeyprotripmanagementbackend.enums.PaymentMethod;
import lk.ijse.journeyprotripmanagementbackend.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    private String merchantId; // PayHere merchant ID
    private String orderId; // Unique order ID for PayHere
    private String paymentId; // PayHere payment ID
    private String statusCode; // PayHere status code (e.g., "2" for success)

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // CARD, PAYPAL, CRYPTO

    private String transactionId; // Unique transaction ID
    private Double amount;
    private String currency; // Currency code (e.g., "LKR", "USD")
    private String description; // Payment description

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // SUCCESS, FAILED, PENDING

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // Timestamp for last update
}
