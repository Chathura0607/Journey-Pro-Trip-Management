package lk.ijse.journeyprotripmanagementbackend.repo;

import lk.ijse.journeyprotripmanagementbackend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {
    Admin findByEmail(String email);
    boolean existsByEmail(String email);
}