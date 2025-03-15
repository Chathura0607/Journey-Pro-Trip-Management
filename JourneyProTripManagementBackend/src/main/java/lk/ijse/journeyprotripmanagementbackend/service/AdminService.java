package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.AdminDTO;

public interface AdminService {
    AdminDTO searchAdmin(String email);
}