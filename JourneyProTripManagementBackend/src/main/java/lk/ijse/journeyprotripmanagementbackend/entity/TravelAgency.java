package lk.ijse.journeyprotripmanagementbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "travel_agencies")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TravelAgency {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String contactInfo;
    private String address;

    @OneToMany(mappedBy = "travelAgency", cascade = CascadeType.ALL)
    private List<TripPackage> tripPackages;
}