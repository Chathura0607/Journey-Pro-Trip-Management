package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.User;
import lk.ijse.journeyprotripmanagementbackend.repo.UserRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public int saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return VarList.Not_Acceptable;
        } else {
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userDTO.setRole("USER");
            userDTO.setCreatedAt(LocalDateTime.now());
            userRepository.save(modelMapper.map(userDTO, User.class));
            return VarList.Created;
        }
    }

    public String getProfilePicturePath(String filename) {
        return Paths.get(uploadDir).resolve(filename).toString();
    }

    @Override
    public UserDTO searchUser(String email) {
        if (userRepository.existsByEmail(email)) {
            User user = userRepository.findByEmail(email);
            return modelMapper.map(user, UserDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public int updateUserProfile(UserDTO userDTO) {
        User existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser != null) {
            // Update profile fields
            existingUser.setFirstName(userDTO.getFirstName());
            existingUser.setLastName(userDTO.getLastName());
            existingUser.setPhoneNumber(userDTO.getPhoneNumber());
            existingUser.setAddress(userDTO.getAddress());
            existingUser.setProfilePicture(userDTO.getProfilePicture());

            userRepository.save(existingUser);
            return VarList.OK; // Profile updated successfully
        }
        return VarList.Not_Found; // User not found
    }

    @Override
    public int changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return VarList.OK; // Password changed successfully
        }
        return VarList.Unauthorized; // Invalid credentials
    }

    @Override
    public UserDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return modelMapper.map(user, UserDTO.class);
        }
        return null; // User not found
    }
}