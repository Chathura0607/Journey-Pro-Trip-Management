// Modified BookingServiceImpl.java
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
import org.springframework.transaction.annotation.Transactional;

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
    private NotificationRepository notificationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public int createBooking(BookingDTO bookingDTO) {
        try {
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
                        if (bookingDTO.getHotelId() != null && !bookingDTO.getHotelId().isEmpty()) {
                            UUID hotelId = UUID.fromString(bookingDTO.getHotelId());
                            Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);
                            optionalHotel.ifPresent(booking::setHotel);
                        }
                        break;
                    case "BUS":
                        if (bookingDTO.getBusId() != null && !bookingDTO.getBusId().isEmpty()) {
                            UUID busId = UUID.fromString(bookingDTO.getBusId());
                            Optional<Bus> optionalBus = busRepository.findById(busId);
                            optionalBus.ifPresent(bus -> {
                                booking.setBus(bus);
                                // Update available seats
                                if (bus.getAvailableSeats() > 0) {
                                    bus.setAvailableSeats(bus.getAvailableSeats() - 1);
                                    busRepository.save(bus);
                                }
                            });
                        }
                        break;
                    case "VEHICLE":
                        if (bookingDTO.getVehicleId() != null && !bookingDTO.getVehicleId().isEmpty()) {
                            UUID vehicleId = UUID.fromString(bookingDTO.getVehicleId());
                            Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
                            optionalVehicle.ifPresent(vehicle -> {
                                booking.setVehicle(vehicle);
                                // Update vehicle availability
                                vehicle.setIsAvailable(false);
                                vehicleRepository.save(vehicle);
                            });
                        }
                        break;
                }

                // Save the booking
                Booking savedBooking = bookingRepository.save(booking);

                // Create notification for the user
                Notification notification = new Notification();
                notification.setUser(user);
                notification.setMessage("Your booking for " + bookingDTO.getBookingType() + " has been confirmed.");
                notification.setIsRead(false);
                notification.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(notification);

                return VarList.Created; // Success
            } else {
                return VarList.Not_Found; // User or Trip not found
            }
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            throw e; // Rethrow to trigger transaction rollback
        }
    }

    @Override
    public BookingDTO getBookingById(String bookingId) {
        // Existing implementation
        UUID uuid = UUID.fromString(bookingId);
        Optional<Booking> optionalBooking = bookingRepository.findById(uuid);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            return modelMapper.map(booking, BookingDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public List<BookingDTO> getAllBookingsForUser(String userId) {
        // Existing implementation
        UUID uuid = UUID.fromString(userId);
        List<Booking> bookings = bookingRepository.findByUserId(uuid);
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int cancelBooking(String bookingId) {
        try {
            // Convert the bookingId from String to UUID
            UUID uuid = UUID.fromString(bookingId);

            // Find the booking by ID
            Optional<Booking> optionalBooking = bookingRepository.findById(uuid);
            if (optionalBooking.isPresent()) {
                Booking booking = optionalBooking.get();

                // Only allow cancellation if booking is not already canceled
                if (booking.getStatus() == BookingStatus.CANCELED) {
                    return VarList.Conflict; // Already canceled
                }

                booking.setStatus(BookingStatus.CANCELED); // Update status to CANCELED

                // Handle resource freeing based on booking type
                if (booking.getBookingType() == BookingType.BUS && booking.getBus() != null) {
                    Bus bus = booking.getBus();
                    bus.setAvailableSeats(bus.getAvailableSeats() + 1);
                    busRepository.save(bus);
                } else if (booking.getBookingType() == BookingType.VEHICLE && booking.getVehicle() != null) {
                    Vehicle vehicle = booking.getVehicle();
                    vehicle.setIsAvailable(true);
                    vehicleRepository.save(vehicle);
                }

                // Save the updated booking
                bookingRepository.save(booking);

                // Create notification for the user
                Notification notification = new Notification();
                notification.setUser(booking.getUser());
                notification.setMessage("Your booking for " + booking.getBookingType() + " has been canceled.");
                notification.setIsRead(false);
                notification.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(notification);

                return VarList.OK; // Success
            } else {
                return VarList.Not_Found; // Booking not found
            }
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            throw e; // Rethrow to trigger transaction rollback
        }
    }
}