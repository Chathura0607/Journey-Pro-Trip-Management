// New PaymentDTO.java
package lk.ijse.journeyprotripmanagementbackend.dto;

import lk.ijse.journeyprotripmanagementbackend.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String userId;
    private String bookingId;
    private PaymentMethod paymentMethod;
    private Double amount;
    private String currency;
    private String description;
    private String merchantId;
    private String orderId;
    private String transactionId;
}