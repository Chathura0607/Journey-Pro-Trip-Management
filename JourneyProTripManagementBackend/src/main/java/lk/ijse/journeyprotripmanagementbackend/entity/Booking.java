package lk.ijse.journeyprotripmanagementbackend.entity;

import jakarta.persistence.*;
import lk.ijse.journeyprotripmanagementbackend.enums.BookingStatus;
import lk.ijse.journeyprotripmanagementbackend.enums.BookingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Enumerated(EnumType.STRING)
    private BookingType bookingType; // HOTEL, BUS, VEHICLE

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel; // For hotel bookings

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus; // For bus bookings

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle; // For vehicle bookings

    @Enumerated(EnumType.STRING)
    private BookingStatus status; // CONFIRMED, CANCELED

    private Double amount;
    private LocalDateTime createdAt;
}