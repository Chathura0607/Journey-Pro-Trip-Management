package lk.ijse.journeyprotripmanagementbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private String bookingType; // HOTEL, BUS, VEHICLE
    private String userId; // ID of the user making the booking
    private String tripId; // ID of the trip
    private String hotelId; // ID of the hotel (for hotel bookings)
    private String busId; // ID of the bus (for bus bookings)
    private String vehicleId; // ID of the vehicle (for vehicle bookings)
    private Double amount;
}