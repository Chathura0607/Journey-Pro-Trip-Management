package lk.ijse.journeyprotripmanagementbackend.repo;

import lk.ijse.journeyprotripmanagementbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(UUID userId);

    List<User> findByRole(String role);
}