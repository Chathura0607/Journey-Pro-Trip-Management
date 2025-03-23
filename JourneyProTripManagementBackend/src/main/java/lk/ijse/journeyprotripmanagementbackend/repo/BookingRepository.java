package lk.ijse.journeyprotripmanagementbackend.repo;

import lk.ijse.journeyprotripmanagementbackend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUserId(UUID userId);
}