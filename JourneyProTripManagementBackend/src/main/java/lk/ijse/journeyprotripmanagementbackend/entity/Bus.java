package lk.ijse.journeyprotripmanagementbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "buses")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String busNumber; // Unique identifier for the bus
    private String route; // Route information (e.g., "City A to City B")
    private LocalTime departureTime; // Scheduled departure time
    private LocalTime arrivalTime; // Scheduled arrival time
    private Integer availableSeats; // Number of available seats

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}