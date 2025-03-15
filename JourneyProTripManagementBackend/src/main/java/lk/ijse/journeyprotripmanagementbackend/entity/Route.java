package lk.ijse.journeyprotripmanagementbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "routes")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String startPoint;
    private String endPoint;
    private Double distance;
    private String estimatedTime;
}
