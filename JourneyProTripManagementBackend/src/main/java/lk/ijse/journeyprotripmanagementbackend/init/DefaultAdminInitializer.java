package lk.ijse.journeyprotripmanagementbackend.init;

import lk.ijse.journeyprotripmanagementbackend.config.DefaultAdminConfig;
import lk.ijse.journeyprotripmanagementbackend.dto.AdminDTO;
import lk.ijse.journeyprotripmanagementbackend.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAdminInitializer.class);

    private final AdminService adminService;
    private final DefaultAdminConfig defaultAdminConfig;

    public DefaultAdminInitializer(AdminService adminService, DefaultAdminConfig defaultAdminConfig) {
        this.adminService = adminService;
        this.defaultAdminConfig = defaultAdminConfig;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            String adminEmail = defaultAdminConfig.getEmail();
            logger.info("Checking for default admin with email: {}", adminEmail);

            // Check if admin already exists
            if (!adminService.existsByEmail(adminEmail)) {
                // Create default admin
                AdminDTO adminDTO = new AdminDTO();
                adminDTO.setFirstName(defaultAdminConfig.getFirstName());
                adminDTO.setLastName(defaultAdminConfig.getLastName());
                adminDTO.setEmail(adminEmail);
                adminDTO.setPassword(defaultAdminConfig.getPassword());
                adminDTO.setPhoneNumber(defaultAdminConfig.getPhoneNumber());
                adminDTO.setAddress(defaultAdminConfig.getAddress());
                adminDTO.setRole("ADMIN");

                adminService.addAdmin(adminDTO);
                logger.info("Default admin created successfully with email: {}", adminEmail);
            } else {
                logger.info("Default admin already exists with email: {}", adminEmail);
            }
        } catch (Exception e) {
            logger.error("Error while initializing default admin", e);
            throw e;
        }
    }
}