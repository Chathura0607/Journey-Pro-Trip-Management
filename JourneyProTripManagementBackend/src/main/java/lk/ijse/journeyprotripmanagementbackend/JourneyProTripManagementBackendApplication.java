package lk.ijse.journeyprotripmanagementbackend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JourneyProTripManagementBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(JourneyProTripManagementBackendApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
