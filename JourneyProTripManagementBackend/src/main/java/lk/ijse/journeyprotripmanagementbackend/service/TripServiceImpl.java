package lk.ijse.journeyprotripmanagementbackend.service;

import lk.ijse.journeyprotripmanagementbackend.dto.TripDTO;
import lk.ijse.journeyprotripmanagementbackend.entity.Trip;
import lk.ijse.journeyprotripmanagementbackend.entity.User;
import lk.ijse.journeyprotripmanagementbackend.enums.TripStatus;
import lk.ijse.journeyprotripmanagementbackend.repo.TripRepository;
import lk.ijse.journeyprotripmanagementbackend.repo.UserRepository;
import lk.ijse.journeyprotripmanagementbackend.service.TripService;
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
@Transactional
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int createTrip(TripDTO tripDTO) {
        try {
            UUID userId = UUID.fromString(tripDTO.getUserId());
            Optional<User> user = userRepository.findById(userId);

            if (user.isPresent()) {
                Trip trip = new Trip();
                trip.setUser(user.get());
                trip.setDestination(tripDTO.getDestination());
                trip.setStartDate(tripDTO.getStartDate());
                trip.setEndDate(tripDTO.getEndDate());
                trip.setStatus(TripStatus.UPCOMING);
                trip.setCreatedAt(LocalDateTime.now());

                tripRepository.save(trip);
                return VarList.Created;
            }
            return VarList.Not_Found;
        } catch (Exception e) {
            return VarList.Internal_Server_Error;
        }
    }

    @Override
    public TripDTO getTripById(String tripId) {
        try {
            UUID uuid = UUID.fromString(tripId); // Convert String to UUID
            Optional<Trip> trip = tripRepository.findById(uuid); // Use UUID here
            return trip.map(this::convertToDto).orElse(null);
        } catch (IllegalArgumentException e) {
            // Handle case where tripId is not a valid UUID
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<TripDTO> getAllTripsForUser(String userId) {
        try {
            UUID uuid = UUID.fromString(userId);
            List<Trip> trips = tripRepository.findByUserId(uuid);
            return trips.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int updateTrip(String tripId, TripDTO tripDTO) {
        try {
            UUID uuid = UUID.fromString(tripId);
            Optional<Trip> trip = tripRepository.findById(uuid);

            if (trip.isPresent()) {
                Trip existingTrip = trip.get();
                existingTrip.setDestination(tripDTO.getDestination());
                existingTrip.setStartDate(tripDTO.getStartDate());
                existingTrip.setEndDate(tripDTO.getEndDate());

                tripRepository.save(existingTrip);
                return VarList.OK;
            }
            return VarList.Not_Found;
        } catch (Exception e) {
            return VarList.Internal_Server_Error;
        }
    }

    @Override
    public int deleteTrip(String tripId) {
        try {
            UUID uuid = UUID.fromString(tripId);
            if (tripRepository.existsById(uuid)) {
                tripRepository.deleteById(uuid);
                return VarList.OK;
            }
            return VarList.Not_Found;
        } catch (Exception e) {
            return VarList.Internal_Server_Error;
        }
    }

    private TripDTO convertToDto(Trip trip) {
        TripDTO tripDTO = modelMapper.map(trip, TripDTO.class);
        tripDTO.setUserId(trip.getUser().getId().toString());
        if (trip.getAdmin() != null) {
            tripDTO.setAdminId(trip.getAdmin().getId().toString());
        }
        return tripDTO;
    }
}