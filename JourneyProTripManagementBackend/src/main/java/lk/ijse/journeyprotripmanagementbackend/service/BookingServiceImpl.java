package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.BookingDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.*;
import lk.ijse.journeyprotripmanagementbackend.enums.BookingStatus;
import lk.ijse.journeyprotripmanagementbackend.enums.BookingType;
import lk.ijse.journeyprotripmanagementbackend.repo.*;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int createBooking(BookingDTO bookingDTO) {
        // Convert the userId and tripId from String to UUID
        UUID userId = UUID.fromString(bookingDTO.getUserId());
        UUID tripId = UUID.fromString(bookingDTO.getTripId());

        // Find the user and trip by ID
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);

        if (optionalUser.isPresent() && optionalTrip.isPresent()) {
            User user = optionalUser.get();
            Trip trip = optionalTrip.get();

            // Create a new booking
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setTrip(trip);
            booking.setBookingType(BookingType.valueOf(bookingDTO.getBookingType()));
            booking.setStatus(BookingStatus.CONFIRMED); // Default status
            booking.setAmount(bookingDTO.getAmount());
            booking.setCreatedAt(LocalDateTime.now());

            // Set hotel, bus, or vehicle based on booking type
            switch (bookingDTO.getBookingType()) {
                case "HOTEL":
                    UUID hotelId = UUID.fromString(bookingDTO.getHotelId());
                    Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);
                    optionalHotel.ifPresent(booking::setHotel);
                    break;
                case "BUS":
                    UUID busId = UUID.fromString(bookingDTO.getBusId());
                    Optional<Bus> optionalBus = busRepository.findById(busId);
                    optionalBus.ifPresent(booking::setBus);
                    break;
                case "VEHICLE":
                    UUID vehicleId = UUID.fromString(bookingDTO.getVehicleId());
                    Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
                    optionalVehicle.ifPresent(booking::setVehicle);
                    break;
            }

            // Save the booking
            bookingRepository.save(booking);
            return VarList.Created; // Success
        } else {
            return VarList.Not_Found; // User or Trip not found
        }
    }

    @Override
    public BookingDTO getBookingById(String bookingId) {
        // Convert the bookingId from String to UUID
        UUID uuid = UUID.fromString(bookingId);

        // Find the booking by ID
        Optional<Booking> optionalBooking = bookingRepository.findById(uuid);

        // If the booking exists, map it to BookingDTO and return
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            return modelMapper.map(booking, BookingDTO.class);
        } else {
            return null; // Booking not found
        }
    }

    @Override
    public List<BookingDTO> getAllBookingsForUser(String userId) {
        // Convert the userId from String to UUID
        UUID uuid = UUID.fromString(userId);

        // Find all bookings for the user
        List<Booking> bookings = bookingRepository.findByUserId(uuid);

        // Map bookings to BookingDTO
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public int cancelBooking(String bookingId) {
        // Convert the bookingId from String to UUID
        UUID uuid = UUID.fromString(bookingId);

        // Find the booking by ID
        Optional<Booking> optionalBooking = bookingRepository.findById(uuid);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus(BookingStatus.CANCELED); // Update status to CANCELED
            bookingRepository.save(booking);
            return VarList.OK; // Success
        } else {
            return VarList.Not_Found; // Booking not found
        }
    }
}