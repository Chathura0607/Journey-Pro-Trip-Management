package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.BookingDTO;

import java.util.List;

public interface BookingService {
    int createBooking(BookingDTO bookingDTO);
    BookingDTO getBookingById(String bookingId);
    List<BookingDTO> getAllBookingsForUser(String userId);
    int cancelBooking(String bookingId);
}