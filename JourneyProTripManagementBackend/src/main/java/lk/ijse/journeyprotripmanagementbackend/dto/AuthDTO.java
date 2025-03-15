package lk.ijse.journeyprotripmanagementbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    private String email;
    private String token;
    private String role; // Added role for role-based routing
}