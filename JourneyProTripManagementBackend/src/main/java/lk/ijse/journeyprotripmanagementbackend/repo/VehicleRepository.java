package lk.ijse.journeyprotripmanagementbackend.repo;

import lk.ijse.journeyprotripmanagementbackend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
}