package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;
import java.util.List;

public interface AdminUserService {
    List<UserDTO> getAllUsers(String role);
    UserDTO getUserById(String userId);
    int deleteUser(String userId, String reason, boolean notifyUser);
}