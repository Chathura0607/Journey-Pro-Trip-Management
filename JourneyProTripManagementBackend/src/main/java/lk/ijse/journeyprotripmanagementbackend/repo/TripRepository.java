package lk.ijse.journeyprotripmanagementbackend.repo;

import lk.ijse.journeyprotripmanagementbackend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, String> {
    Optional<Trip> findById(UUID uuid);
    List<Trip> findByUserId(UUID userId);
    boolean existsById(UUID uuid);
    void deleteById(UUID uuid);
}