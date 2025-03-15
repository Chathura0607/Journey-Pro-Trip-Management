package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;

public interface UserService {
    int saveUser(UserDTO userDTO);
    UserDTO searchUser(String email);
}