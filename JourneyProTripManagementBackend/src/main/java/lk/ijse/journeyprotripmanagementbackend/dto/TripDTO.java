package lk.ijse.journeyprotripmanagementbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String userId; // ID of the user creating the trip
}