package lk.ijse.journeyprotripmanagementbackend.repo;

import lk.ijse.journeyprotripmanagementbackend.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HotelRepository extends JpaRepository<Hotel, UUID> {
}