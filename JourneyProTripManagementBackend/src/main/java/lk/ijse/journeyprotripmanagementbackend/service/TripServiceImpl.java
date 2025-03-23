package lk.ijse.journeyprotripmanagementbackend.service;

import jakarta.mail.MessagingException;
import lk.ijse.journeyprotripmanagementbackend.dto.TripDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Trip;
import lk.ijse.journeyprotripmanagementbackend.entity.User;
import lk.ijse.journeyprotripmanagementbackend.enums.TripStatus;
import lk.ijse.journeyprotripmanagementbackend.repo.TripRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.UserRepository;
import lk.ijse.journeyprotripmanagementbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Override
    public int createTrip(TripDTO tripDTO) {
        // Convert the userId from String to UUID
        UUID userId;
        try {
            userId = UUID.fromString(tripDTO.getUserId());
        } catch (IllegalArgumentException e) {
            return VarList.Not_Found; // Invalid UUID format
        }

        // Find the user by ID
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Create a new trip
            Trip trip = new Trip();
            trip.setUser(user);
            trip.setDestination(tripDTO.getDestination());
            trip.setStartDate(tripDTO.getStartDate());
            trip.setEndDate(tripDTO.getEndDate());
            trip.setStatus(TripStatus.UPCOMING); // Default status
            trip.setCreatedAt(LocalDateTime.now());

            // Save the trip
            tripRepository.save(trip);

            // Send email to the user using Thymeleaf template
            try {
                Context context = new Context();
                context.setVariable("userName", user.getFirstName()); // Add user's first name
                context.setVariable("destination", tripDTO.getDestination());
                context.setVariable("startDate", tripDTO.getStartDate());
                context.setVariable("endDate", tripDTO.getEndDate());

                emailService.sendHtmlEmailWithTemplate(user.getEmail(), "Trip Confirmation", "trip-confirmation", context);
            } catch (MessagingException e) {
                // Log the error or handle it appropriately
                System.err.println("Failed to send email: " + e.getMessage());
            }

            return VarList.Created; // Success
        } else {
            return VarList.Not_Found; // User not found
        }
    }

    @Override
    public TripDTO getTripById(String tripId) {
        // Convert the tripId from String to UUID
        UUID uuid;
        try {
            uuid = UUID.fromString(tripId);
        } catch (IllegalArgumentException e) {
            return null; // Invalid UUID format
        }

        // Find the trip by ID
        Optional<Trip> optionalTrip = tripRepository.findById(uuid);

        // If the trip exists, map it to TripDTO and return
        if (optionalTrip.isPresent()) {
            Trip trip = optionalTrip.get();
            return modelMapper.map(trip, TripDTO.class);
        } else {
            return null; // Trip not found
        }
    }

    @Override
    public List<TripDTO> getAllTripsForUser(String userId) {
        // Convert the userId from String to UUID
        UUID uuid = UUID.fromString(userId);

        // Find all trips for the user
        List<Trip> trips = tripRepository.findByUserId(uuid);

        // Map trips to TripDTO
        return trips.stream()
                .map(trip -> modelMapper.map(trip, TripDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public int updateTrip(String tripId, TripDTO tripDTO) {
        // Convert the tripId from String to UUID
        UUID uuid = UUID.fromString(tripId);

        // Find the trip by ID
        Optional<Trip> optionalTrip = tripRepository.findById(uuid);
        if (optionalTrip.isPresent()) {
            Trip trip = optionalTrip.get();

            // Update trip details
            trip.setDestination(tripDTO.getDestination());
            trip.setStartDate(tripDTO.getStartDate());
            trip.setEndDate(tripDTO.getEndDate());

            // Save the updated trip
            tripRepository.save(trip);
            return VarList.OK; // Success
        } else {
            return VarList.Not_Found; // Trip not found
        }
    }

    @Override
    public int deleteTrip(String tripId) {
        // Convert the tripId from String to UUID
        UUID uuid = UUID.fromString(tripId);

        // Check if the trip exists
        if (tripRepository.existsById(uuid)) {
            // Delete the trip
            tripRepository.deleteById(uuid);
            return VarList.OK; // Success
        } else {
            return VarList.Not_Found; // Trip not found
        }
    }
}