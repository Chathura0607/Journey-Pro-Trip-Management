package lk.ijse.journeyprotripmanagementbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private String id;
    private String bookingType; // HOTEL, BUS, VEHICLE
    private String userId;
    private String tripId;
    private String hotelId;
    private String hotelName; // Add this field
    private String busId;
    private String busNumber; // Add this field
    private String vehicleId;
    private String vehicleModel; // Add this field
    private Double amount;
    private String status;
    private String createdAt; // Ensure this is in ISO format (yyyy-MM-dd'T'HH:mm:ss)
}