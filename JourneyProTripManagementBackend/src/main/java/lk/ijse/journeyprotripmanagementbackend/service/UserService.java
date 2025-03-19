package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;

public interface UserService {
    int saveUser(UserDTO userDTO);

    UserDTO searchUser(String email);

    int updateUserProfile(UserDTO userDTO);

    int changePassword(String email, String oldPassword, String newPassword);

    UserDTO getUserProfile(String email);
}