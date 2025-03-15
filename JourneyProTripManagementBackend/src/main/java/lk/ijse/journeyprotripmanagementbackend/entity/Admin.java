package lk.ijse.journeyprotripmanagementbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admins")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String phoneNumber;
    private String address;

    private String role = "ADMIN"; // Default role for admins

    private LocalDateTime createdAt;
}