package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.UserDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.User;
import lk.ijse.journeyprotripmanagementbackend.repo.UserRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // Autowire the PasswordEncoder

    @Override
    public int saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return VarList.Not_Acceptable;
        } else {
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encode the password
            userDTO.setRole("USER"); // Set default role
            userDTO.setCreatedAt(LocalDateTime.now()); // Set the createdAt field
            userRepository.save(modelMapper.map(userDTO, User.class));
            return VarList.Created;
        }
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
}