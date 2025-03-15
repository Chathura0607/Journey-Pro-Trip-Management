package lk.ijse.journeyprotripmanagementbackend.entity;

import jakarta.persistence.*;
import lk.ijse.journeyprotripmanagementbackend.enums.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "trips")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = true) // Admin can create trips
    private Admin admin;

    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TripStatus status; // UPCOMING, ONGOING, COMPLETED

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;

    private LocalDateTime createdAt;
}
