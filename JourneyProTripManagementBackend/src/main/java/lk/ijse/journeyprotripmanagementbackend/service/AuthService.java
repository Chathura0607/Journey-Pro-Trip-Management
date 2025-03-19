package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.AuthDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Admin;
import lk.ijse.journeyprotripmanagementbackend.entity.User;
import lk.ijse.journeyprotripmanagementbackend.repo.AdminRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.UserRepository;
import lk.ijse.journeyprotripmanagementbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check if the email belongs to a user
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("USER"))
            );
        }

        // Check if the email belongs to an admin
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            return new org.springframework.security.core.userdetails.User(
                    admin.getEmail(),
                    admin.getPassword(),
                    List.of(new SimpleGrantedAuthority("ADMIN"))
            );
        }

        throw new UsernameNotFoundException("User or Admin not found with email: " + email);
    }

    public AuthDTO login(String email, String password) {
        try {
            // Load user details
            UserDetails userDetails = loadUserByUsername(email);

            // Validate the password
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            // Generate a JWT token
            String token = jwtUtil.generateToken(userDetails);

            // Determine if the user is an admin or a regular user
            boolean isAdmin = adminRepository.existsByEmail(email);

            // Return the AuthDTO with the token and role
            return new AuthDTO(email, token, isAdmin ? "ADMIN" : "USER");
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("User not found with email: " + email);
        }
    }

    public void logout(String token) {
        // Extract the token from the "Bearer " prefix if present
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Add the token to the blacklist
        tokenBlacklistService.blacklistToken(token);
    }

    public boolean isTokenValid(String token) {
        // Extract the token from the "Bearer " prefix if present
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Check if the token is blacklisted
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            return false;
        }

        // Validate the token using JwtUtil
        return jwtUtil.validateToken(token, loadUserByUsername(jwtUtil.getUsernameFromToken(token)));
    }
}