package lk.ijse.journeyprotripmanagementbackend.dto;

import lk.ijse.journeyprotripmanagementbackend.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private VehicleType vehicleType; // CAR, VAN, BIKE, SUV
    private String model;
    private String registrationNumber;
    private Integer seatCapacity;
    private Boolean isAvailable;
}