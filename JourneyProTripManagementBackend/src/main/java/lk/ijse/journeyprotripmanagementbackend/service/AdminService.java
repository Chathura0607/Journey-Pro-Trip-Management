package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.AdminDTO;

import java.util.List;

public interface AdminService {
    String addAdmin(AdminDTO adminDTO);

    String updateAdmin(AdminDTO adminDTO);

    String deleteAdmin(String email);

    AdminDTO searchAdmin(String email);

    List<AdminDTO> getAllAdmins();

    boolean existsByEmail(String email);
}