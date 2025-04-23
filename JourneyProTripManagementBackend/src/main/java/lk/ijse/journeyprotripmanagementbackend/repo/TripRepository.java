package lk.ijse.journeyprotripmanagementbackend.repo;

import lk.ijse.journeyprotripmanagementbackend.entity.Trip;
import lk.ijse.journeyprotripmanagementbackend.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> { // Changed to UUID
    List<Trip> findByUserId(UUID userId);
    List<Trip> findByStatus(TripStatus status);
    List<Trip> findByAdminId(UUID adminId);
}