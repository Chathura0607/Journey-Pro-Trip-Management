package lk.ijse.journeyprotripmanagementbackend.dto;

import lk.ijse.journeyprotripmanagementbackend.enums.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    private String id;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String userId;
    private String adminId;
    private TripStatus status;
    private LocalDateTime createdAt;
}