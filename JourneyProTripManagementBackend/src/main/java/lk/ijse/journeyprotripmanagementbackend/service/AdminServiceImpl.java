package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.AdminDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Admin;
import lk.ijse.journeyprotripmanagementbackend.repo.AdminRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public String addAdmin(AdminDTO adminDTO) {
        if (adminRepository.existsByEmail(adminDTO.getEmail())) {
            return "01"; // Admin already exists
        }
        Admin admin = modelMapper.map(adminDTO, Admin.class);
        adminRepository.save(admin);
        return "00"; // Success
    }

    @Override
    public String updateAdmin(AdminDTO adminDTO) {
        // Find the existing admin by email
        Admin existingAdmin = adminRepository.findByEmail(adminDTO.getEmail());

        // If the admin does not exist, return an error
        if (existingAdmin == null) {
            return "01"; // Admin not found
        }

        // Check if the new email (if provided) already exists for another admin
        if (adminDTO.getEmail() != null && !adminDTO.getEmail().equals(existingAdmin.getEmail())) {
            Admin adminWithNewEmail = adminRepository.findByEmail(adminDTO.getEmail());
            if (adminWithNewEmail != null && !adminWithNewEmail.getId().equals(existingAdmin.getId())) {
                return "02"; // New email already exists for another admin
            }
        }

        modelMapper.map(adminDTO, existingAdmin);

        adminRepository.save(existingAdmin);
        return "00"; // Success
    }

    @Override
    @Transactional
    public String deleteAdmin(String email) {
        if (!adminRepository.existsByEmail(email)) {
            return "01"; // Admin not found
        }
        adminRepository.deleteByEmail(email);
        return "00"; // Success
    }

    @Override
    public AdminDTO searchAdmin(String email) {
        if (adminRepository.existsByEmail(email)) {
            Admin admin = adminRepository.findByEmail(email);
            return modelMapper.map(admin, AdminDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public List<AdminDTO> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream()
                .map(admin -> modelMapper.map(admin, AdminDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }
}
