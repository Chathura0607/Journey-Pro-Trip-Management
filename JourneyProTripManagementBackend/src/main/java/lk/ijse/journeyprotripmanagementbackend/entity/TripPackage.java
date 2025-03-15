package lk.ijse.journeyprotripmanagementbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "trip_packages")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TripPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "travel_agency_id", nullable = false)
    private TravelAgency travelAgency;

    private String name;
    private String description;
    private Double price;
    private String includedServices;
}
