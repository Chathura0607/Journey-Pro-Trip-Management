package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.AdminDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Admin;
import lk.ijse.journeyprotripmanagementbackend.repo.AdminRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AdminDTO searchAdmin(String email) {
        if (adminRepository.existsByEmail(email)) {
            Admin admin = adminRepository.findByEmail(email);
            return modelMapper.map(admin, AdminDTO.class);
        } else {
            return null;
        }
    }
}