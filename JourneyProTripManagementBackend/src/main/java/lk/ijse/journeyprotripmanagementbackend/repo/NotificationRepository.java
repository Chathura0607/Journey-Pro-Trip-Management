package lk.ijse.journeyprotripmanagementbackend.repo;

import lk.ijse.journeyprotripmanagementbackend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserId(UUID userId);
}