package lk.ijse.journeyprotripmanagementbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusDTO {
    private String id;
    private String busNumber; // Unique identifier for the bus
    private String route; // Route information (e.g., "City A to City B")
    private LocalTime departureTime; // Scheduled departure time
    private LocalTime arrivalTime; // Scheduled arrival time
    private Integer availableSeats; // Number of available seats
}