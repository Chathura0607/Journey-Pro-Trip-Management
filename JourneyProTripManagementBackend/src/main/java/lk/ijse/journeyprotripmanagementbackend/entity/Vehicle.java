package lk.ijse.journeyprotripmanagementbackend.entity;

import jakarta.persistence.*;
import lk.ijse.journeyprotripmanagementbackend.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType; // CAR, VAN, BIKE, SUV

    private String model;
    private String registrationNumber;
    private Integer seatCapacity;
    private Boolean isAvailable;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}
